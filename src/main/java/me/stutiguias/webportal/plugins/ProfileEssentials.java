/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.plugins;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Daniel
 */
public class ProfileEssentials {
    String playerName;
    YamlConfiguration PlayerYML;
    
    public ProfileEssentials(String playerName) {
        this.playerName = playerName;
    }
    
    public Boolean LoadProfile(){
        playerName = playerName.toLowerCase();
        File configplayerfile = new File("plugins/Essentials/userdata/"+ playerName +".yml");
        PlayerYML = new YamlConfiguration();
        try {
            PlayerYML.load(configplayerfile);
            return true;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return false;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        } catch (InvalidConfigurationException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public String GetIp() {
        return PlayerYML.getString("ipAddress");
    }
    
    public List<String> GetMail() {
        return PlayerYML.getStringList("mail");
    }
    
    public List<String> GetHomes() {
        List<String> listhomes = new ArrayList<String>();
        try {
            for(String key:PlayerYML.getConfigurationSection("homes").getKeys(false)) {
                listhomes.add(key);
            }
        }catch(NullPointerException ex){
            listhomes.add("Home Not Found");
        }
        return listhomes;
    }
        
}
