/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.settings;

import me.stutiguias.webportal.init.WebAuction;
import org.bukkit.entity.Player;
import uk.org.whoami.authme.security.PasswordSecurity;
/**
 *
 * @author Daniel
 */
public final class AuthSystem {
    
    private WebAuction plugin;
    
    public AuthSystem(WebAuction plugin) {
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
            return PasswordSecurity.comparePasswordWithHash(password,hash);
         }catch(Exception e) {
            return false; 
         }
    }
    
    public boolean AuthDbisLogged(String player,String password) {
         try {
            String hash = plugin.dataQueries.getPassword(player);
            String hashpassword = Algorithm.stringHexa(Algorithm.gerarHash(password,plugin.algorithm));
            if(hash.equals(hashpassword)) return true;
            return false;
         }catch(Exception e) {
            return false; 
         }
    }
    
    
}
