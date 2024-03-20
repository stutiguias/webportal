package me.stutiguias.webportal.webserver.handlers;

import com.sun.net.httpserver.HttpExchange;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.webserver.request.MailRequest;

import java.io.IOException;
import java.util.Map;

public class MailHandler implements IRequestHandler {

    private final WebPortal plugin;
    public MailHandler(WebPortal plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(HttpExchange exchange, Map<String, Object> params) throws IOException {
        String url = exchange.getRequestURI().getPath();
        String sessionid = (String) params.get("sessionid");

        MailRequest Mail = new MailRequest(plugin, exchange);
        if(url.startsWith("/mail/get")) Mail.GetMails(sessionid,params);
        if(url.startsWith("/mail/send") && !isLocked(sessionid)) Mail.SendMail(sessionid, url, params);
    }

    private boolean isLocked(String SessionId) {
        if(WebPortal.LockTransact.get(WebPortal.AuthPlayers.get(SessionId).WebSitePlayer.getName()) != null) {
            return WebPortal.LockTransact.get(WebPortal.AuthPlayers.get(SessionId).WebSitePlayer.getName());
        }else{
            return false;
        }
    }

    @Override
    public boolean isPublic() {
        return false;
    }
}
