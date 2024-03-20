package me.stutiguias.webportal.webserver.handlers;

import com.sun.net.httpserver.HttpExchange;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.webserver.request.ShopRequest;

import java.io.IOException;
import java.util.Map;

public class ShopHandler implements IRequestHandler {

    private final WebPortal plugin;
    public ShopHandler(WebPortal plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(HttpExchange exchange, Map<String, Object> params) throws IOException {
        String url = exchange.getRequestURI().getPath();
        String SessionId = (String) params.get("sessionid");

        ShopRequest Shop = new ShopRequest(plugin, exchange);
        if(url.startsWith("/auction/get")) Shop.RequestShopBy(SessionId,url,params);
        if(url.startsWith("/auction/shop")) Shop.BuySellShop(SessionId,params);
    }

    @Override
    public boolean isPublic() {
        return false;
    }
}
