/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.authentication;

import java.security.NoSuchAlgorithmException;
import me.stutiguias.webportal.init.WebPortal;
import org.bukkit.entity.Player;
import fr.xephi.authme.security.PasswordSecurity;
/**
 *
 * @author Daniel
 */
public final class AuthSystem {
    
    private final WebPortal plugin;
    
    public AuthSystem(WebPortal plugin) {
        this.plugin = plugin;
    }
        
    public boolean Auth(String name,String pass) {
        if(plugin.allowlogifonline) {
            Player _player = plugin.getServer().getPlayer(name);
            if(_player == null) return false;
        }
        if(plugin.authplugin.equalsIgnoreCase("AuthMe") && AuthMeReloadedisLogged(name,pass)) return true;
        return plugin.authplugin.equalsIgnoreCase("WebPortal") && WebPortalisLogged(name,pass);
    }
    
    public boolean WebPortalisLogged(String player,String pass) {
        pass = Algorithm.stringHexa(Algorithm.gerarHash(pass,plugin.algorithm));
        String pass_db = plugin.db.getPassword(player);
        return pass.equals(pass_db);
    }
    
    public boolean AuthMeReloadedisLogged(String player,String password) {
         try {
            String hash = plugin.db.getPassword(player);
            return PasswordSecurity.comparePasswordWithHash(password,hash,player);
         }catch(NoSuchAlgorithmException e) {
            return false; 
         }
    }
    
}
