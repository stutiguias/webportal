package me.stutiguias.webportal.webserver.handlers;

import com.sun.net.httpserver.HttpExchange;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.webserver.request.UserRequest;

import java.io.IOException;
import java.util.Map;

public class UserHandler implements IRequestHandler {
    private final WebPortal plugin;
    private UserRequest UserInfo;

    public UserHandler(WebPortal plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(HttpExchange exchange, Map<String, Object> params) throws IOException {
        String url = exchange.getRequestURI().getPath();
        String sessionId = (String) params.get("sessionid");


        if(url.startsWith("/user/info")) new UserRequest(plugin, exchange).GetInfo(sessionId);
    }

    @Override
    public boolean isPublic() {
        return false;
    }
}
