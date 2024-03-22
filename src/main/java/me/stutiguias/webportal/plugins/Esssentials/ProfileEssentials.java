/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.plugins.Esssentials;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Daniel
 */
public class ProfileEssentials {
    String uuid;
    YamlConfiguration PlayerYML;
    
    public ProfileEssentials(String uuid) {
        this.uuid = uuid;
    }
    
    public Boolean LoadProfile(){
        try {
            uuid = uuid.toLowerCase();
            File configplayerfile = new File("plugins/Essentials/userdata/"+ uuid +".yml");
            PlayerYML = new YamlConfiguration();
            PlayerYML.load(configplayerfile);
            return true;
        } catch (IOException | InvalidConfigurationException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public String GetIp() {
        return PlayerYML.getString("ip-address");
    }
    
    public List<Map<?, ?>>  GetMail() {
        return PlayerYML.getMapList("mail");
    }
    
    public List<String> GetHomes() {
        ConfigurationSection config = PlayerYML.getConfigurationSection("homes");
        if(config == null) return new ArrayList<>();
        return new ArrayList<>(config.getKeys(false));
    }
        
}
