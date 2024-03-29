package me.stutiguias.webportal.webserver.handlers;

import com.sun.net.httpserver.HttpExchange;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.webserver.request.AdminRequest;
import me.stutiguias.webportal.webserver.request.AdminShopRequest;

import java.io.IOException;
import java.util.Map;

public class AdminHandler implements IRequestHandler {

    private final WebPortal plugin;
    public AdminHandler(WebPortal plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(HttpExchange exchange, Map<String, Object> params) throws IOException {
        String url = exchange.getRequestURI().getPath();
        String SessionId = (String) params.get("sessionid");

        AdminRequest Admin = new AdminRequest(plugin, exchange);
        AdminShopRequest AdminShop = new AdminShopRequest(plugin, exchange);
        if(url.startsWith("/adm/search")) Admin.AdmGetInfo(SessionId,params);
        if(url.startsWith("/adm/deleteshop")) AdminShop.Delete(SessionId, url, params);
        if(url.startsWith("/adm/addshop")) AdminShop.AddShop(SessionId, url, params);
        if(url.startsWith("/adm/shoplist")) AdminShop.List(SessionId, url, params);
        if(url.startsWith("/adm/webban")) Admin.WebBan(SessionId, params);
        if(url.startsWith("/adm/webunban")) Admin.WebUnBan(SessionId, params);
        if(url.startsWith("/adm/getMonitor")) Admin.getMonitor(SessionId);

        if(plugin.DisableCmd){
            Admin.Print("Command Disabled", "text/plain");
            return;
        }

        // ESSENTIALS COMMANDS
        if(url.startsWith("/adm/essentials/whois")) Admin.CmdEssentials("whois",SessionId, params);
        if(url.startsWith("/adm/essentials/mail")) Admin.CmdEssentials("mail",SessionId, params);

        // BASE COMMANDS
        if(url.startsWith("/adm/base/op")) Admin.CmdBaseWithParams("op", SessionId, 1, params);
        if(url.startsWith("/adm/base/deop")) Admin.CmdBaseWithParams("deop", SessionId,1, params);

        if(url.startsWith("/adm/base/ipban")) Admin.CmdBaseWithParams("ban-ip", SessionId,1, params);
        if(url.startsWith("/adm/base/ban")) Admin.CmdBaseWithParams("ban", SessionId,1, params);

        if(url.startsWith("/adm/base/ippardon")) Admin.CmdBaseWithParams("pardon-ip", SessionId,1, params);
        if(url.startsWith("/adm/base/pardon")) Admin.CmdBaseWithParams("pardon", SessionId,1, params);

        if(url.startsWith("/adm/base/kick")) Admin.CmdBaseWithParams("kick", SessionId,1, params);

        if(url.startsWith("/adm/base/say")) Admin.CmdBaseWithParams("say", SessionId,1, params);

        if(url.startsWith("/adm/base/gamemode")) Admin.CmdBaseWithParams("gamemode", SessionId,1, params);

    }

    @Override
    public boolean isPublic() {
        return false;
    }
}
