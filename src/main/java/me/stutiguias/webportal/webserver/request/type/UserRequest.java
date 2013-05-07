/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.request.type;

import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.webserver.authentication.AuthPlayer;
import me.stutiguias.webportal.webserver.HttpResponse;

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
        String Name = authPlayer.AuctionPlayer.getName();
        String Admin = (authPlayer.AuctionPlayer.getIsAdmin() == 1) ? ", <a href='/admin.html' >Admin Panel</a>":",";
        String response = Name + Admin + ",";
        response += "$ " + Format(plugin.economy.getBalance(Name)) + ",";
        response += plugin.dataQueries.getMail(Name).size();
        Print(response,"text/plain");
    }

}
