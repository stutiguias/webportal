/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.request;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.model.WebSitePlayer;
import me.stutiguias.webportal.webserver.authentication.LoggedPlayer;
import me.stutiguias.webportal.webserver.authentication.AuthSystem;
import me.stutiguias.webportal.webserver.HttpResponse;

/**
 *
 * @author Daniel
 */
public class LoginRequest extends HttpResponse {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    public AuthSystem AS;
    
    public LoginRequest(WebPortal plugin, HttpExchange exchange){
        super(plugin);
        setHttpExchange(exchange);
        AS = new AuthSystem(plugin);
    }
    
    public void TryToLogin(Map param) {
        String username = firstString(param.get("Username"));
        String pass = firstString(param.get("Password"));

        if (isBlank(username) || isBlank(pass)) {
            Print("no", "text/plain");
            return;
        }

        if(AS.Auth(username, pass))
        {
            LoggedPlayer _AuthPlayer = new LoggedPlayer();
            WebSitePlayer _AuctionPlayer = plugin.db.getPlayer(username);
            if (_AuctionPlayer == null
             || _AuctionPlayer.getWebban().equalsIgnoreCase("Y")) {
                Print("no", "text/plain");
                return;
            }
            String sessionid = createSessionId();
            _AuthPlayer.WebSitePlayer = _AuctionPlayer;
            _AuthPlayer.WebSitePlayer.setIp(sessionid);

            Calendar cal = Calendar.getInstance(); 
            cal.setTime(new Date()); 
            cal.add(Calendar.MINUTE, plugin.SessionTime);
            cal.getTime(); 
            _AuthPlayer.setDate(cal.getTime());

            Date d = new Date();

            WebPortal.AuthPlayers.entrySet().removeIf(entry -> {
                LoggedPlayer authplayer = entry.getValue();
                return authplayer.GetLogin().equalsIgnoreCase(username) || authplayer.getDate().before(d);
            });

            WebPortal.AuthPlayers.put(sessionid,_AuthPlayer);
            setSessionCookie(sessionid, plugin.SessionTime);
            Print("ok","text/plain");
        }else{
            Print("no","text/plain");
        }
    }

    private String createSessionId() {
        byte[] token = new byte[32];
        SECURE_RANDOM.nextBytes(token);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(token);
    }

    private void setSessionCookie(String sessionId, int sessionMinutes) {
        int maxAge = Math.max(60, sessionMinutes * 60);
        getHttpExchange().getResponseHeaders().add(
                "Set-Cookie",
                "sessionid=" + sessionId + "; Max-Age=" + maxAge + "; Path=/; HttpOnly; Secure; SameSite=Strict");
    }

    private String firstString(Object value) {
        if (value instanceof List<?>) {
            List<?> values = (List<?>) value;
            return values.isEmpty() ? null : String.valueOf(values.get(0));
        }
        return value != null ? String.valueOf(value) : null;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
