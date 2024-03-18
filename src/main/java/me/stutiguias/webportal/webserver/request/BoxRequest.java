/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.request;

import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.webserver.authentication.LoggedPlayer;
import me.stutiguias.webportal.webserver.HttpResponse;
import org.bukkit.OfflinePlayer;

/**
 *
 * @author Daniel
 */
public class BoxRequest extends HttpResponse {
  
    public BoxRequest(WebPortal plugin) {
        super(plugin);
    }
    private StringBuilder sb;
    private String Name;

    public void BoxMcMMO(String HostAddress) {
        if(plugin.mcmmo == null) {
            NotActive();
            return;
        }
        
        sb = new StringBuilder();
        Name = getNameLoggedPlayerWebsite(HostAddress);

        if(!(Boolean)plugin.mcmmo.Config.get("McMMOMYSql")) {
            sb.append(plugin.mcmmo.getBox(Name));
        }else {
            sb.append(plugin.mcmmo.getBoxMcMMoMySql(Name));
        }

        Print(sb.toString(), "text/plain");
    }

    public void BOX2(String HostAddress) {
        if(plugin.essentials == null) {
            NotActive();
            return;
        }
        
        sb = new StringBuilder();
        Name = getNameLoggedPlayerWebsite(HostAddress);

        sb.append(plugin.essentials.getBox(Name));
        Print(sb.toString(),"text/plain");
    }

    private static String getNameLoggedPlayerWebsite(String HostAddress) {
        LoggedPlayer authPlayer = WebPortal.AuthPlayers.get(HostAddress);
        return authPlayer.WebSitePlayer.getName();
    }

    private void NotActive() {
        Print("","text/plain");
    }
}
