/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.request.type;

import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.settings.AuctionPlayer;
import me.stutiguias.webportal.webserver.authentication.AuthPlayer;
import me.stutiguias.webportal.webserver.authentication.AuthSystem;
import me.stutiguias.webportal.webserver.HttpResponse;

/**
 *
 * @author Daniel
 */
public class LoginRequest extends HttpResponse {
    
    public WebPortal plugin;
    public AuthSystem AS;
    
    public LoginRequest(WebPortal plugin){
        super(plugin);
        this.plugin = plugin;
        AS = new AuthSystem(plugin);
    }
    
    public void TryToLogin(String HostAddress,String param) {
        String username = GetParam("Username", param);
        String pass = GetParam("Password", param);
        String json;
        if(AS.Auth(username, pass))
        {
            AuthPlayer _AuthPlayer = new AuthPlayer();
            AuctionPlayer _AuctionPlayer = plugin.dataQueries.getPlayer(username);
            if(_AuctionPlayer == null) {
                plugin.dataQueries.createPlayer(username,pass,0.0, 1, 1, 0);
                _AuctionPlayer = plugin.dataQueries.getPlayer(username);
            }
            _AuthPlayer.AuctionPlayer = _AuctionPlayer;
            _AuthPlayer.AuctionPlayer.setIp(HostAddress);
            WebPortal.AuthPlayers.put(HostAddress,_AuthPlayer);
            json = "ok";
        }else{
            json = "no";
        }
        Print(json,"text/plain");
    }
}
