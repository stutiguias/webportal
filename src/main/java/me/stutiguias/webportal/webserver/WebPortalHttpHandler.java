/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
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
    private Map<String, IRequestHandler> requestHandlers = new HashMap<>();
    String htmlDir = "./plugins/WebPortal/html";
    String url;

    public WebPortalHttpHandler(WebPortal plugin)
    {
        this.plugin = plugin;
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
        url = exchange.getRequestURI().getPath();
        Map<String, Object> params = (Map<String, Object>) exchange.getAttribute("parameters");
        String sessionId = (String) params.get("sessionid");
        boolean isAuthenticated = sessionId != null && plugin.AuthPlayers.containsKey(sessionId);

        if (url.contains("..") || url.contains("./")) {
            serveStaticFile("/login.html", "text/html", exchange);
            return;
        }
        if(url.equalsIgnoreCase("/")){
            serveStaticFile(htmlDir+"/login.html","text/html", exchange);
            return;
        }
        if(isAllowed()) {
            serveStaticFile(htmlDir+url,GetMimeType(url), exchange);
            return;
        }
        if(url.startsWith("/logout")) {
            WebPortal.AuthPlayers.remove(sessionId);
            serveStaticFile(htmlDir+"/login.html","text/html", exchange);
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
                }
            } else {
                if(plugin.EnableExternalSource) serveStaticFile(htmlDir+"/external.html","text/html",exchange);
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

    private void serveStaticFile(String path, String mimeType, HttpExchange exchange) throws IOException {
        try
        {
            File file = new File(path);
            if(file.exists()) {
                serveFile(file, mimeType, exchange);
            } else {
                sendErrorResponse(exchange);
            }
        }
        catch(IOException e)
        {
            WebPortal.logger.info("ERROR in readFileAsBinary(): " + e.getMessage());
        }
    }

    private void serveFile(File file, String mimeType, HttpExchange exchange) throws IOException {
        byte[] buffer = new byte[0x10000];
        long length = file.length();

        if(plugin.EnableExternalSource) {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin",plugin.allowexternal);
        }

        exchange.getResponseHeaders().set("Content-Type", mimeType);
        exchange.sendResponseHeaders(200, length);

        try (FileInputStream fis = new FileInputStream(file); OutputStream out = exchange.getResponseBody()) {
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
        }
        exchange.sendResponseHeaders(400,"404 Not Found".getBytes().length);
        exchange.getResponseBody().write("404 Not Found".getBytes());
        exchange.getResponseBody().flush();
        exchange.getResponseBody().close();
    }
    
    public String GetMimeType(String url) {
        if(url.contains(".js"))
            return "text/javascript";
        if(url.contains(".png"))
            return "image/jpg";
        if(url.contains(".css"))
            return "text/css";
        if(url.contains(".html"))
            return "text/html";
        return "text/plain";
    }
    
    public Boolean isAllowed() {
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
