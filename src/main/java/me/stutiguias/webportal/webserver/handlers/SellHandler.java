package me.stutiguias.webportal.webserver.handlers;

import com.sun.net.httpserver.HttpExchange;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.webserver.request.SellRequest;

import java.io.IOException;
import java.util.Map;

public class SellHandler implements IRequestHandler {

    private final WebPortal plugin;

    public SellHandler(WebPortal plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(HttpExchange exchange, Map<String, Object> params) throws IOException {
        String url = exchange.getRequestURI().getPath();
        String sessionId = (String) params.get("sessionid");

        SellRequest Sell = new SellRequest(plugin, exchange);
        if(url.startsWith("/myauctions/cancel")) Sell.Cancel(url, params,sessionId);
        if(url.startsWith("/myauctions/get")) Sell.GetSell(sessionId, url, params);
    }

    @Override
    public boolean isPublic() {
        return false;
    }
}
