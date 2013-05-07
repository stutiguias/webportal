/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.request.type;

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
        StringBuilder sb = new StringBuilder();
        AuthPlayer authPlayer = WebPortal.AuthPlayers.get(HostAddress);
        String Name = authPlayer.AuctionPlayer.getName();
        if((Boolean)plugin.mcmmoconfig.get("UseMcMMO") && !(Boolean)plugin.mcmmoconfig.get("McMMOMYSql")) {
            OfflinePlayer player = plugin.getServer().getOfflinePlayer(Name);
            sb.append(plugin.mcmmo.getBox(player));
        }else if((Boolean)plugin.mcmmoconfig.get("UseMcMMO") && (Boolean)plugin.mcmmoconfig.get("McMMOMYSql")) {
            sb.append(plugin.mcmmo.getBox(Name));
        }else{
            sb.append("");
        }
        Print(sb.toString(), "text/plain");
    }
    
    public void BOX2(String HostAddress) {
        StringBuilder sb = new StringBuilder();
        AuthPlayer authPlayer = WebPortal.AuthPlayers.get(HostAddress);
        String Name = authPlayer.AuctionPlayer.getName();
        if(plugin.UseEssentialsBox) {
           sb.append(plugin.essentials.getBox(Name));
        }else{
            sb.append("");
        }
        Print(sb.toString(),"text/plain");
    }  
}
