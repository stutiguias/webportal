package me.stutiguias.webportal.webserver.handlers;

import com.sun.net.httpserver.HttpExchange;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.webserver.request.LoginRequest;
import me.stutiguias.webportal.webserver.request.ShopRequest;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

public class LoginHandler implements IRequestHandler {

    private final WebPortal plugin;

    public LoginHandler(WebPortal plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(HttpExchange exchange, Map<String, Object> params) throws IOException {
        String url = exchange.getRequestURI().getPath();
        String sessionId = (String) params.get("sessionid");
        if(url.startsWith("/web/login")){
            LoginRequest loginRequest = new LoginRequest(plugin, exchange);
            if(sessionId.isEmpty()) {
                loginRequest.Print("Cookie Disable, please enable cookie","text/plain");
                return;
            }
            loginRequest.TryToLogin(sessionId,params);
        }

        if(url.startsWith("/web/auction")) new ShopRequest(plugin,exchange).GetShopWithoutLogin(params);
    }

    @Override
    public boolean isPublic() {
        return true;
    }
}
