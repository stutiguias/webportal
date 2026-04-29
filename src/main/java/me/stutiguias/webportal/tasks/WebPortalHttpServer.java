/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.tasks;

import com.sun.net.httpserver.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.webserver.LoadSheddingHttpHandler;
import me.stutiguias.webportal.webserver.ParameterFilter;
import me.stutiguias.webportal.webserver.WebPortalHttpHandler;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 *
 * @author Daniel
 */
public class WebPortalHttpServer extends Thread {
    private static final int DEFAULT_WORKER_THREADS = 4;
    private static final int MAX_WORKER_THREADS = 16;
    private static final int DEFAULT_QUEUE_LIMIT = 8;
    private static final int MAX_QUEUE_LIMIT = 64;

    private final WebPortal plugin;
    private final int Port;
    public int NUM_CONN_MAX;
    private final int workerThreads;
    private final int queueLimit;
    public HttpsServer serverSSL;
    private ExecutorService executor;

    public WebPortalHttpServer(WebPortal plugin, int NUM_CONN_MAX, int workerThreads, int queueLimit)
    {
        this.NUM_CONN_MAX = NUM_CONN_MAX;
        this.workerThreads = workerThreads;
        this.queueLimit = queueLimit;
        Port = plugin.port;
        this.plugin = plugin;
    }
    
    @Override
    public void run()
    {
        ExecutorService executorService = null;
        try {
            executorService = createExecutor();
            HttpHandler rootHandler = createRootHandler();
            WebPortal.logger.log(Level.INFO,"{0} Start HTTPS Server",plugin.logPrefix);
            serverSSL = HttpsServer.create(new InetSocketAddress(Port), NUM_CONN_MAX);
            HttpContext cc  = serverSSL.createContext("/", rootHandler);
            cc.getFilters().add(new ParameterFilter());
            SSLContext sslContext = SSLContext.getInstance("TLS");
            Path keystorePath = resolveKeystorePath();
            char[] password = plugin.sslCertificatePassword.toCharArray();
            if (password.length == 0) {
                throw new IllegalStateException("SSL certificate password is empty");
            }
            if (!Files.isRegularFile(keystorePath)) {
                throw new IOException("SSL certificate file not found: " + keystorePath.toAbsolutePath());
            }
            KeyStore ks = KeyStore.getInstance("PKCS12");
            try (FileInputStream fis = new FileInputStream(keystorePath.toAbsolutePath().toString())) {
                ks.load(fis, password);
            }
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, password);
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ks);
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            serverSSL.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
                public void configure(HttpsParameters params) {
                    SSLContext context = getSSLContext();
                    params.setSSLParameters(context.getDefaultSSLParameters());
                }
            });
            serverSSL.setExecutor(executorService);
            serverSSL.start();
            executor = executorService;
            WebPortal.logger.log(Level.INFO,"{0} Server start on port {1} ",new Object[]{ plugin.logPrefix , Port });
        }catch(Exception ex) {
            shutdownExecutor(executorService);
            WebPortal.logger.info("ERROR : " + ex.getMessage());
        }
    }

    public void stopServer() {
        if (serverSSL != null) {
            serverSSL.stop(0);
        }
        shutdownExecutor(executor);
        executor = null;
    }

    private ExecutorService createExecutor() {
        int sanitizedWorkerThreads = getWorkerThreads();
        int sanitizedQueueLimit = getQueueLimit();
        AtomicInteger workerCounter = new AtomicInteger(1);
        ThreadFactory threadFactory = runnable -> {
            Thread thread = new Thread(runnable, "WebPortal-HTTP-" + workerCounter.getAndIncrement());
            thread.setDaemon(true);
            return thread;
        };
        WebPortal.logger.log(Level.INFO, "{0} Web worker threads set {1}", new Object[]{plugin.logPrefix, sanitizedWorkerThreads});
        WebPortal.logger.log(Level.INFO, "{0} Web queue limit set {1}", new Object[]{plugin.logPrefix, sanitizedQueueLimit});
        return Executors.newCachedThreadPool(threadFactory);
    }

    private void shutdownExecutor(ExecutorService executorService) {
        if (executorService == null) {
            return;
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException ex) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private Path resolveKeystorePath() {
        String configuredPath = plugin.sslCertificatePath != null ? plugin.sslCertificatePath.trim() : "";
        if (configuredPath.isEmpty()) {
            throw new IllegalStateException("SSL certificate path is empty");
        }

        Path path = Paths.get(configuredPath);
        if (path.isAbsolute()) {
            return path.normalize();
        }
        return plugin.getDataFolder().toPath().resolve(path).normalize();
    }

    private HttpHandler createRootHandler() {
        return new LoadSheddingHttpHandler(plugin, new WebPortalHttpHandler(plugin), getWorkerThreads(), getQueueLimit());
    }

    private int getWorkerThreads() {
        return Math.max(1, Math.min(MAX_WORKER_THREADS, workerThreads > 0 ? workerThreads : DEFAULT_WORKER_THREADS));
    }

    private int getQueueLimit() {
        return Math.max(0, Math.min(MAX_QUEUE_LIMIT, queueLimit >= 0 ? queueLimit : DEFAULT_QUEUE_LIMIT));
    }
    
}
