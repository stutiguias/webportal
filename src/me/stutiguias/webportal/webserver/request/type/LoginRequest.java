/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.request.type;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.settings.WebSitePlayer;
import me.stutiguias.webportal.webserver.authentication.LoggedPlayer;
import me.stutiguias.webportal.webserver.authentication.AuthSystem;
import me.stutiguias.webportal.webserver.HttpResponse;

/**
 *
 * @author Daniel
 */
public class LoginRequest extends HttpResponse {

    public AuthSystem AS;
    
    public LoginRequest(WebPortal plugin){
        super(plugin);
        AS = new AuthSystem(plugin);
    }
    
    public void TryToLogin(String HostAddress,Map param) {
        String sessionid = (String)param.get("sessionid");
        String username = (String)param.get("Username");
        String pass = (String)param.get("Password");

        if(AS.Auth(username, pass))
        {
            LoggedPlayer _AuthPlayer = new LoggedPlayer();
            WebSitePlayer _AuctionPlayer = plugin.dataQueries.getPlayer(username);
            if(_AuctionPlayer == null) {
                plugin.dataQueries.createPlayer(username,pass, 1, 1, 0);
                _AuctionPlayer = plugin.dataQueries.getPlayer(username);
            }
            if(_AuctionPlayer.getWebban().equalsIgnoreCase("Y")){
                Print("no","text/plain");
                return;
            }
            _AuthPlayer.WebSitePlayer = _AuctionPlayer;
            _AuthPlayer.WebSitePlayer.setIp(HostAddress);
            
            Calendar cal = Calendar.getInstance(); 
            cal.setTime(new Date()); 
            cal.add(Calendar.MINUTE, plugin.SessionTime);
            cal.getTime(); 
            _AuthPlayer.setDate(cal.getTime());

            Date d = new Date();
            
            for (Map.Entry<String,LoggedPlayer> pairs : WebPortal.AuthPlayers.entrySet()) {
                LoggedPlayer authplayer = pairs.getValue();
                if(authplayer.GetLogin().equalsIgnoreCase(username) || authplayer.getDate().before(d))
                    WebPortal.AuthPlayers.remove(pairs.getKey());
            }
            
            WebPortal.AuthPlayers.put(sessionid,_AuthPlayer);
            Print("ok","text/plain");
        }else{
            Print("no","text/plain");
        }
    }
}
