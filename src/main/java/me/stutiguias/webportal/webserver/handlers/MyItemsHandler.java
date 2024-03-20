package me.stutiguias.webportal.webserver.handlers;

import com.sun.net.httpserver.HttpExchange;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.webserver.request.MyItemsRequest;
import me.stutiguias.webportal.webserver.request.UserRequest;

import java.io.IOException;
import java.util.Map;

public class MyItemsHandler implements IRequestHandler {

    private final WebPortal plugin;

    public MyItemsHandler(WebPortal plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(HttpExchange exchange, Map<String, Object> params) throws IOException {
        String url = exchange.getRequestURI().getPath();
        String sessionId = (String) params.get("sessionid");
        MyItemsRequest MyItems = new MyItemsRequest(plugin, exchange);
        UserRequest UserInfo = new UserRequest(plugin, exchange);
        if(url.startsWith("/myitems/get")) MyItems.GetMyItemsForSelectBox(sessionId);
        if(url.startsWith("/myitems/dataTable")) MyItems.GetMyItems(sessionId, url, params);
        if(url.startsWith("/myitems/postauction") && !isLocked(sessionId)) MyItems.CreateSell(sessionId, url, params);
        if(url.startsWith("/myitems/lore")) UserInfo.ItemLore(sessionId, params);
    }

    private boolean isLocked(String sessionId) {
        if(WebPortal.LockTransact.get(WebPortal.AuthPlayers.get(sessionId).WebSitePlayer.getName()) != null) {
            return WebPortal.LockTransact.get(WebPortal.AuthPlayers.get(sessionId).WebSitePlayer.getName());
        }else{
            return false;
        }
    }

    @Override
    public boolean isPublic() {
        return false;
    }
}
