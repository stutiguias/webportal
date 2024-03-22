/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.request;

import com.sun.net.httpserver.HttpExchange;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.webserver.authentication.LoggedPlayer;
import me.stutiguias.webportal.webserver.HttpResponse;

import java.util.Map;

/**
 *
 * @author Daniel
 */
public class BoxRequest extends HttpResponse {

    public BoxRequest(WebPortal plugin, HttpExchange exchange) {
        super(plugin);
        setHttpExchange(exchange);
    }

    public void handleBoxRequest(String sessionid,BoxType type) {
        String box = getBox(type, sessionid);
        if(box == null) {
            NotActive();
            return;
        }
        Print(box, "text/plain");
    }

    private String getBox(BoxType type, String sessionid) {
        switch(type) {
            case MCMMO:
                return getBoxMcMMo(sessionid);
            case ESSENTIALS:
                return plugin.essentials.getBox(getUUID(sessionid));
            default:
                return null;
        }
    }

    private String getBoxMcMMo(String sessionid) {
        if(!(Boolean)plugin.mcmmo.Config.get("McMMOMYSql")) {
            return plugin.mcmmo.getBox(getUUID(sessionid));
        } else {
            return plugin.mcmmo.getBoxMcMMoMySql(getUUID(sessionid));
        }
    }

    private static String getUUID(String sessionid) {
        LoggedPlayer authPlayer = WebPortal.AuthPlayers.get(sessionid);
        return authPlayer.WebSitePlayer.getUUID();
    }

    private void NotActive() {
        Print("","text/plain");
    }
}

