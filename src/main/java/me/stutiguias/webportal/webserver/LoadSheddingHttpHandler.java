package me.stutiguias.webportal.webserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import me.stutiguias.webportal.init.WebPortal;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;

public class LoadSheddingHttpHandler implements HttpHandler {
    private static final String BUSY_RESPONSE = "WebPortal is busy. Please try again in a moment.";

    private final WebPortal plugin;
    private final HttpHandler delegate;
    private final Semaphore requestSlots;
    private final Semaphore workerSlots;

    public LoadSheddingHttpHandler(WebPortal plugin, HttpHandler delegate, int workerThreads, int queueLimit) {
        this.plugin = plugin;
        this.delegate = delegate;
        this.workerSlots = new Semaphore(workerThreads, true);
        this.requestSlots = new Semaphore(workerThreads + queueLimit, true);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!requestSlots.tryAcquire()) {
            sendBusyResponse(exchange);
            return;
        }

        boolean workerAcquired = false;
        try {
            workerSlots.acquire();
            workerAcquired = true;
            delegate.handle(exchange);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            WebPortal.logger.log(Level.WARNING, "{0} Web request interrupted while waiting for a worker", plugin.logPrefix);
            sendBusyResponse(exchange);
        } finally {
            if (workerAcquired) {
                workerSlots.release();
            }
            requestSlots.release();
        }
    }

    private void sendBusyResponse(HttpExchange exchange) throws IOException {
        byte[] response = BUSY_RESPONSE.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        exchange.getResponseHeaders().set("Retry-After", "1");
        exchange.getResponseHeaders().set("Connection", "Close");
        exchange.sendResponseHeaders(503, response.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(response);
        }
    }
}
