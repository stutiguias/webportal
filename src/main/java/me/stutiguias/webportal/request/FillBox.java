/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.request;

import java.net.Socket;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.settings.AuthPlayer;
import me.stutiguias.webportal.webserver.Response;
import org.bukkit.OfflinePlayer;

/**
 *
 * @author Daniel
 */
public class FillBox extends Response {
    
    private WebPortal plugin;
    
    public FillBox(WebPortal plugin) {
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
        print(sb.toString(), "text/plain");
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
        print(sb.toString(),"text/plain");
    }  
}
