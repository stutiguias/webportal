/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.tasks;

import com.sun.net.httpserver.*;

import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.security.KeyStore;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import me.stutiguias.webportal.init.WebPortal;
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

    private WebPortal plugin;
    int Port;
    boolean useSSL;
    public int NUM_CONN_MAX;
    public HttpServer server;
    public HttpsServer serverSSL;

    public WebPortalHttpServer(WebPortal plugin,int NUM_CONN_MAX)
    {
        this.NUM_CONN_MAX = NUM_CONN_MAX;
        Port = plugin.port;
        useSSL = plugin.useSSL;
        this.plugin = plugin;
    }
    
    @Override
    public void run()
    {
        try {
            if(useSSL) {
                WebPortal.logger.log(Level.INFO,"{0} Start HTTPS Server",plugin.logPrefix);
                serverSSL = HttpsServer.create(new InetSocketAddress(Port), NUM_CONN_MAX);
                HttpContext cc  = serverSSL.createContext("/", new WebPortalHttpHandler(plugin));
                cc.getFilters().add(new ParameterFilter());
                SSLContext sslContext = SSLContext.getInstance("TLS");
                char[] password = "123".toCharArray();
                KeyStore ks = KeyStore.getInstance("PKCS12");
                Path dataFolder = plugin.getDataFolder().toPath();
                Path keystorePath = dataFolder.resolve("myserver.p12");
                FileInputStream fis = new FileInputStream(keystorePath.toAbsolutePath().toString());
                ks.load(fis, password);
                fis.close();
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
                serverSSL.start();
            }else{
                WebPortal.logger.log(Level.INFO,"{0} Start HTTP Server",plugin.logPrefix);
                server = HttpServer.create(new InetSocketAddress(Port),NUM_CONN_MAX);
                HttpContext cc  = server.createContext("/", new WebPortalHttpHandler(plugin));
                cc.getFilters().add(new ParameterFilter());
                server.start();
            }
            // TODO : Implementer Executor
            // server.setExecutor(Executors.newFixedThreadPool(10));
            WebPortal.logger.log(Level.INFO,"{0} Server start on port {1} ",new Object[]{ plugin.logPrefix , Port });
        }catch(Exception ex) {
            WebPortal.logger.info("ERROR : " + ex.getMessage());
        }
    }
    
}