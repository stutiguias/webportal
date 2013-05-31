/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.authentication;

import com.cypherx.xauth.password.PasswordHandler;
import com.cypherx.xauth.xAuth;
import me.stutiguias.webportal.init.WebPortal;
import org.bukkit.entity.Player;
import uk.org.whoami.authme.security.PasswordSecurity;
/**
 *
 * @author Daniel
 */
public final class AuthSystem {
    
    private WebPortal plugin;
    
    public AuthSystem(WebPortal plugin) {
        this.plugin = plugin;
    }
        
    public boolean Auth(String name,String pass) {
        if(plugin.allowlogifonline) {
            Player _player = plugin.getServer().getPlayer(name);
            if(_player == null) return false;
        }
        if(plugin.authplugin.equalsIgnoreCase("AuthMe") && AuthMeReloadedisLogged(name,pass)) return true;
        if(plugin.authplugin.equalsIgnoreCase("WebPortal") && WebPortalisLogged(name,pass)) return true;
        if(plugin.authplugin.equalsIgnoreCase("AuthDb") && AuthDbisLogged(name,pass)) return true;
        if(plugin.authplugin.equalsIgnoreCase("xAuth") && xAuthisLogged(name,pass)) return true;
        return false;
    }
    
    public boolean WebPortalisLogged(String player,String pass) {
        pass = Algorithm.stringHexa(Algorithm.gerarHash(pass,plugin.algorithm));
        String pass_db = plugin.dataQueries.getPassword(player);
        if(pass.equals(pass_db))
        {
            return true;
        }else{
            return false;
        }
    }
    
    public boolean AuthMeReloadedisLogged(String player,String password) {
         try {
            String hash = plugin.dataQueries.getPassword(player);
            return PasswordSecurity.comparePasswordWithHash(password,hash,player);
         }catch(Exception e) {
            return false; 
         }
    }
    
    public boolean AuthDbisLogged(String player,String password) {
         try {
            String pass = Algorithm.stringHexa(Algorithm.gerarHash(password,plugin.algorithm));
            String pass_db = plugin.dataQueries.getPassword(player);
            if(pass_db.equals(pass)) return true;
            return false;
         }catch(Exception e) {
            return false; 
         }
    }
        
    public boolean xAuthisLogged(String player,String password) {
         try {
            String id = plugin.dataQueries.getPassword(player);
            PasswordHandler ph = new PasswordHandler(xAuth.getPlugin());
            return ph.checkPassword(Integer.parseInt(id), password);
         }catch(Exception e) {
            return false; 
         }
    }
    
}
