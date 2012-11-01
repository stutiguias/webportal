/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.request;

import java.net.Socket;
import me.stutiguias.webportal.init.WebAuction;
import me.stutiguias.webportal.settings.AuthPlayer;
import me.stutiguias.webportal.webserver.Response;

/**
 *
 * @author Daniel
 */
public class Userinfo extends Response {
    
    public WebAuction plugin;
    public Socket _Socket;
    
    public Userinfo(WebAuction plugin,Socket socket) {
        super(plugin, socket);
        this.plugin = plugin;
        _Socket = socket;
    }
    
    public void GetInfo()  {
        AuthPlayer authPlayer = WebAuction.AuthPlayer.get(_Socket.getInetAddress().getHostAddress());
        String Name = authPlayer.AuctionPlayer.getName();
        String Admin = (authPlayer.AuctionPlayer.getIsAdmin() == 1) ? ", <a href='/admin.html' >Admin Painel</a>":",";
        String response = Name + Admin + ",";
        response += "$ " + plugin.economy.getBalance(Name) + ",";
        response += plugin.dataQueries.getMail(Name).size();
        print(response,"text/plain");
    }
}
