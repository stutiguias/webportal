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
    
    public void BOX1(String HostAddress) {
        if(plugin.mcmmo == null) {
            NotActive();
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        LoggedPlayer authPlayer = WebPortal.AuthPlayers.get(HostAddress);
        String Name = authPlayer.WebSitePlayer.getName();

        if(!(Boolean)plugin.mcmmo.Config.get("McMMOMYSql")) {
            OfflinePlayer player = plugin.getServer().getOfflinePlayer(Name);
            sb.append(plugin.mcmmo.getBox(player));
        }else {
            sb.append(plugin.mcmmo.getBox(Name));
        }
        Print(sb.toString(), "text/plain");
    }
    
    public void BOX2(String HostAddress) {
        if(plugin.essentials == null) {
            NotActive();
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        LoggedPlayer authPlayer = WebPortal.AuthPlayers.get(HostAddress);
        String Name = authPlayer.WebSitePlayer.getName();
        sb.append(plugin.essentials.getBox(Name));
        Print(sb.toString(),"text/plain");
    }
    
    private void NotActive() {
        Print("","text/plain");
    }
}
