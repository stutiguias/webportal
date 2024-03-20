package me.stutiguias.webportal.webserver.handlers;

import com.sun.net.httpserver.HttpExchange;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.webserver.request.BoxRequest;

import java.io.IOException;
import java.util.Map;

public class BoxHandler implements IRequestHandler {

    private final WebPortal plugin;

    public BoxHandler(WebPortal plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(HttpExchange exchange, Map<String, Object> params) throws IOException {
        String url = exchange.getRequestURI().getPath();
        String SessionId = (String) params.get("sessionid");

        BoxRequest Box = new BoxRequest(plugin, exchange);
        if(url.startsWith("/box/1")) Box.BoxMcMMO(SessionId);
        if(url.startsWith("/box/2")) Box.BOX2(SessionId);
    }

    @Override
    public boolean isPublic() {
        return false;
    }
}
