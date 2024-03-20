package me.stutiguias.webportal.webserver.handlers;

import com.sun.net.httpserver.HttpExchange;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.webserver.request.BuyRequest;

import java.io.IOException;
import java.util.Map;

public class BuyHandler implements IRequestHandler {

    private final WebPortal plugin;
    public BuyHandler(WebPortal plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(HttpExchange exchange, Map<String, Object> params) throws IOException {
        String url = exchange.getRequestURI().getPath();
        String SessionId = (String) params.get("sessionid");

        BuyRequest Buy = new BuyRequest(plugin, exchange);
        if(url.startsWith("/buy/additem")) Buy.AddItem(SessionId, params);
        if(url.startsWith("/buy/remitem")) Buy.Cancel(params,SessionId);
        if(url.startsWith("/buy/getitem")) Buy.GetItems(SessionId, params);
    }

    @Override
    public boolean isPublic() {
        return false;
    }
}
