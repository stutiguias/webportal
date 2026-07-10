/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.webserver.handlers.*;
import me.stutiguias.webportal.webserver.request.AdminRequest;
import me.stutiguias.webportal.webserver.request.AdminShopRequest;
import me.stutiguias.webportal.webserver.request.BoxRequest;
import me.stutiguias.webportal.webserver.request.BuyRequest;
import me.stutiguias.webportal.webserver.request.LoginRequest;
import me.stutiguias.webportal.webserver.request.MailRequest;
import me.stutiguias.webportal.webserver.request.MyItemsRequest;
import me.stutiguias.webportal.webserver.request.SellRequest;
import me.stutiguias.webportal.webserver.request.ShopRequest;
import me.stutiguias.webportal.webserver.request.UserRequest;

/**
 *
 * @author Daniel
 */
public class WebPortalHttpHandler implements HttpHandler {

    private final WebPortal plugin;
    private final Map<String, IRequestHandler> requestHandlers = new HashMap<>();
    private final Path htmlDir;

    public WebPortalHttpHandler(WebPortal plugin)
    {
        this.plugin = plugin;
        this.htmlDir = plugin.getDataFolder().toPath().resolve("html").toAbsolutePath().normalize();
        initializeRequestHandlers();
    }

    private void initializeRequestHandlers() {
        requestHandlers.put("web", new LoginHandler(plugin));
        requestHandlers.put("myitems", new MyItemsHandler(plugin));
        requestHandlers.put("mail", new MailHandler(plugin));
        requestHandlers.put("auction", new ShopHandler(plugin));
        requestHandlers.put("adm", new AdminHandler(plugin));
        requestHandlers.put("box", new BoxHandler(plugin));
        requestHandlers.put("myauctions", new SellHandler(plugin));
        requestHandlers.put("user", new UserHandler(plugin));
        requestHandlers.put("buy", new BuyHandler(plugin));

    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String url = exchange.getRequestURI().getPath();
        Map<String, Object> params = (Map<String, Object>) exchange.getAttribute("parameters");
        if (params == null) {
            params = new HashMap<>();
        }
        String sessionId = firstString(params.get("sessionid"));
        if (isBlankSession(sessionId)) {
            sessionId = getCookie(exchange, "sessionid");
            if (!isBlankSession(sessionId)) {
                params.put("sessionid", sessionId);
            }
        }
        boolean isAuthenticated = sessionId != null && WebPortal.AuthPlayers.containsKey(sessionId);

        if (url.contains("..") || url.contains("./")) {
            serveStaticFile("/login.html", exchange);
            return;
        }
        if(url.equalsIgnoreCase("/")){
            serveStaticFile("/login.html", exchange);
            return;
        }
        if(isAllowed(url)) {
            serveStaticFile(url, exchange);
            return;
        }
        if(url.startsWith("/logout")) {
            WebPortal.AuthPlayers.remove(sessionId);
            exchange.getResponseHeaders().add("Set-Cookie", "sessionid=deleted; Max-Age=0; Path=/; HttpOnly; Secure; SameSite=Strict");
            serveStaticFile("/login.html", exchange);
            return;
        }

        IRequestHandler handler = requestHandlers.getOrDefault(url.split("/")[1], null);
        if (handler != null) {
            if (isAuthenticated || handler.isPublic()) {
                try {
                    handler.handle(exchange, params);
                } catch (IOException e) {
                    WebPortal.logger.log(Level.INFO, "Error handling request: {0}", e.getMessage());
                    e.printStackTrace();
                    sendErrorResponse(exchange);
                } catch (IllegalArgumentException | NullPointerException e) {
                    WebPortal.logger.log(Level.INFO, "Invalid web request: {0}", e.getMessage());
                    sendBadRequestResponse(exchange);
                }
            } else {
                if(plugin.EnableExternalSource) {
                    serveStaticFile("/external.html", exchange);
                    return;
                }
                sendUnauthorizedResponse(exchange);
            }
        } else {
            sendNotFoundResponse(exchange);
        }
    }

    private void sendUnauthorizedResponse(HttpExchange exchange) throws IOException {
        String response = "Access denied: authentication required.";
        exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
        exchange.sendResponseHeaders(401, response.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }

    private void sendNotFoundResponse(HttpExchange exchange) throws IOException {
        String response = "Page not found.";
        exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
        exchange.sendResponseHeaders(404, response.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }

    private void sendBadRequestResponse(HttpExchange exchange) throws IOException {
        String response = "Invalid request.";
        byte[] body = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        exchange.getResponseHeaders().set("Connection", "Close");
        exchange.sendResponseHeaders(400, body.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(body);
        }
    }

    private String firstString(Object value) {
        if (value instanceof List<?>) {
            List<?> values = (List<?>) value;
            return values.isEmpty() ? null : String.valueOf(values.get(0));
        }
        return value != null ? String.valueOf(value) : null;
    }

    private String getCookie(HttpExchange exchange, String name) {
        List<String> cookies = exchange.getRequestHeaders().get("Cookie");
        if (cookies == null) {
            return null;
        }

        String prefix = name + "=";
        for (String cookieHeader : cookies) {
            String[] parts = cookieHeader.split(";");
            for (String part : parts) {
                String cookie = part.trim();
                if (cookie.startsWith(prefix)) {
                    return cookie.substring(prefix.length());
                }
            }
        }
        return null;
    }

    private boolean isBlankSession(String sessionId) {
        return sessionId == null
                || sessionId.isEmpty()
                || "null".equalsIgnoreCase(sessionId)
                || "undefined".equalsIgnoreCase(sessionId);
    }

    private void serveStaticFile(String url, HttpExchange exchange) throws IOException {
        Path file = resolveStaticFile(url);
        if (file == null) {
            sendErrorResponse(exchange);
            return;
        }

        serveFile(file, getMimeType(file), exchange);
    }

    private Path resolveStaticFile(String url) {
        if (url == null || !url.startsWith("/") || url.indexOf('\0') >= 0 || url.indexOf('\\') >= 0) {
            return null;
        }

        try {
            Path requested = Paths.get(url.substring(1));
            Path normalized = htmlDir.resolve(requested).normalize();
            if (!normalized.startsWith(htmlDir) || !Files.isRegularFile(normalized)) {
                return null;
            }

            Path realBase = htmlDir.toRealPath();
            Path realFile = normalized.toRealPath();
            return realFile.startsWith(realBase) ? realFile : null;
        } catch (InvalidPathException | IOException e) {
            return null;
        }
    }

    private void serveFile(Path file, String mimeType, HttpExchange exchange) throws IOException {
        byte[] buffer = new byte[0x10000];
        long length = Files.size(file);

        if(plugin.EnableExternalSource) {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin",plugin.allowexternal);
            exchange.getResponseHeaders().set("Access-Control-Allow-Credentials", "true");
        }

        exchange.getResponseHeaders().set("Content-Type", mimeType);
        exchange.sendResponseHeaders(200, length);

        try (java.io.InputStream fis = Files.newInputStream(file); OutputStream out = exchange.getResponseBody()) {
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    private void sendErrorResponse(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/html");
        exchange.getResponseHeaders().set("Server","WebPortal Server");
        exchange.getResponseHeaders().set("Connection","Close");
        if(plugin.EnableExternalSource) {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin",plugin.allowexternal);
            exchange.getResponseHeaders().set("Access-Control-Allow-Credentials", "true");
        }
        exchange.sendResponseHeaders(404,"404 Not Found".getBytes().length);
        exchange.getResponseBody().write("404 Not Found".getBytes());
        exchange.getResponseBody().flush();
        exchange.getResponseBody().close();
    }
    
    private String getMimeType(Path file) {
        try {
            String mimeType = Files.probeContentType(file);
            if (mimeType != null && !mimeType.isBlank()) {
                return mimeType;
            }
        } catch (IOException ignored) {
            // Use the extension fallback below when the platform cannot identify the file.
        }

        String fileName = file.getFileName().toString().toLowerCase();
        if (fileName.endsWith(".js")) return "text/javascript";
        if (fileName.endsWith(".css")) return "text/css";
        if (fileName.endsWith(".html")) return "text/html";
        if (fileName.endsWith(".json")) return "application/json";
        if (fileName.endsWith(".svg")) return "image/svg+xml";
        if (fileName.endsWith(".png")) return "image/png";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) return "image/jpeg";
        if (fileName.endsWith(".gif")) return "image/gif";
        return "application/octet-stream";
    }
    
    public Boolean isAllowed(String url) {
        if(plugin.EnableExternalSource) return false;
        
        if(url.contains("./") || url.contains("..")) return false;
        
        return  url.startsWith("/css") || 
                url.startsWith("/styles") ||
                url.contains("/image") ||
                url.contains("/favicon.ico") ||
                url.contains("/Images") ||
                url.startsWith("/img") ||
                url.startsWith("/js") ||
                url.startsWith("/scripts") ||
                url.startsWith("/about") ||
                url.startsWith("/myitems.html") ||
                url.startsWith("/login.html") ||
                url.startsWith("/admin.html") ||
                url.startsWith("/sell.html") ||
                url.startsWith("/index.html") ||
                url.startsWith("/about.html") ||
                url.startsWith("/shop.html") ||
                url.startsWith("/mail.html") ||
                url.startsWith("/buy.html") ||
                url.startsWith("/signs.html");
    }
}
