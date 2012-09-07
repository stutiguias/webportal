/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.settings;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import me.stutiguias.webportal.init.WebAuction;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import uk.org.whoami.authme.cache.auth.PlayerCache;
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
        if(plugin.authplugin.equalsIgnoreCase("AuthMe Reloaded") && AuthMeReloadedisLogged(name,pass)) return true;
        if(plugin.authplugin.equalsIgnoreCase("WebPortal") && WebPortalisLogged(name,pass)) return true;
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
            String hash =  "2";
            return PasswordSecurity.comparePasswordWithHash(password,hash);
         /*  if (hash.contains("$")) {
            String[] line = hash.split("\\$");
            if (line.length > 3 && line[1].equals("SHA")) {
                WebAuction.log.info(hash);
                WebAuction.log.info(getSaltedHash(password, line[2]));
                return hash.equals(getSaltedHash(password, line[2]));
            } else {
                return false;
            }
           } */
         }catch(Exception e) {
              WebAuction.log.info("errro" + e.getMessage());
              e.printStackTrace();
            return false; 
         }
    }
    
    private static String getSaltedHash(String message, String salt) throws NoSuchAlgorithmException {
        return "$SHA$" + salt + "$" + getSHA256(getSHA256(message) + salt);
    }
    
    private static String getSHA256(String message) throws NoSuchAlgorithmException {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");

        sha256.reset();
        sha256.update(message.getBytes());
        byte[] digest = sha256.digest();

        return String.format("%0" + (digest.length << 1) + "x", new BigInteger(1,digest));
    }
    
}
