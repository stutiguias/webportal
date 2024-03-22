/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.authentication;

import java.security.NoSuchAlgorithmException;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.plugins.LoginSecurity.LoginSecurity;
import me.stutiguias.webportal.plugins.LoginSecurity.ProfileLoginSecurity;
import org.bukkit.entity.Player;
/**
 *
 * @author Daniel
 */
public final class AuthSystem {
    
    private final WebPortal plugin;
    private LoginSecurity loginSecurity;
    
    public AuthSystem(WebPortal plugin) {
        this.plugin = plugin;
    }
        
    public boolean Auth(String name,String pass) {
        if(plugin.allowlogifonline) {
            Player _player = plugin.getServer().getPlayer(name);
            if(_player == null) return false;
        }
        // TODO : Implement More Auth System Here
        if(plugin.authplugin.equalsIgnoreCase("LoginSecurity")) return plugin.loginSecutiry.Auth(name, pass);
        return plugin.authplugin.equalsIgnoreCase("WebPortal") && WebPortalisLogged(name,pass);
    }
    
    public boolean WebPortalisLogged(String player,String pass) {
        pass = Algorithm.stringHexa(Algorithm.gerarHash(pass,plugin.algorithm));
        String pass_db = plugin.db.getPassword(player);
        return pass.equals(pass_db);
    }
    
}
