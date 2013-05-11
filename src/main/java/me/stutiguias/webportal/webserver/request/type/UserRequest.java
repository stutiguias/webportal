/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.request.type;

import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.webserver.authentication.AuthPlayer;
import me.stutiguias.webportal.webserver.HttpResponse;
import org.json.simple.JSONObject;

/**
 *
 * @author Daniel
 */
public class UserRequest extends HttpResponse {
    
    public WebPortal plugin;

    
    public UserRequest(WebPortal plugin) {
        super(plugin);
        this.plugin = plugin;
    }
    
    public void GetInfo(String HostAddress)  {
        AuthPlayer authPlayer = WebPortal.AuthPlayers.get(HostAddress);
        JSONObject json = new JSONObject();
            json.put("Name", authPlayer.AuctionPlayer.getName() );
            json.put("Admin", authPlayer.AuctionPlayer.getIsAdmin() );
            json.put("Money", plugin.economy.getBalance( authPlayer.AuctionPlayer.getName() ) );
            json.put("Mail", plugin.dataQueries.getMail(authPlayer.AuctionPlayer.getName() ).size() );
        Print(json.toJSONString(),"application/json");
    }
   

}
