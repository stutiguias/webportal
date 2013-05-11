/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.request.type;

import java.util.logging.Level;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.webserver.authentication.AuthPlayer;
import me.stutiguias.webportal.webserver.HttpResponse;
import org.bukkit.OfflinePlayer;

/**
 *
 * @author Daniel
 */
public class BoxRequest extends HttpResponse {
    
    private WebPortal plugin;
    
    public BoxRequest(WebPortal plugin) {
        super(plugin);
        this.plugin = plugin;
    }
    
    public void BOX1(String HostAddress) {
        if(plugin.mcmmo == null) {
            NotActive();
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        AuthPlayer authPlayer = WebPortal.AuthPlayers.get(HostAddress);
        String Name = authPlayer.AuctionPlayer.getName();

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
        AuthPlayer authPlayer = WebPortal.AuthPlayers.get(HostAddress);
        String Name = authPlayer.AuctionPlayer.getName();
        sb.append(plugin.essentials.getBox(Name));
        Print(sb.toString(),"text/plain");
    }
    
    private void NotActive() {
        Print("","text/plain");
    }
}
