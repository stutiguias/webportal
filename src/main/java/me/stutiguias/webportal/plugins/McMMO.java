/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.plugins;

import java.util.logging.Level;

import me.stutiguias.webportal.init.WebPortal;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import java.util.HashMap;

/**
 *
 * @author Daniel
 */
public class McMMO {
    
    public WebPortal plugin;
    public ProfileMcMMO profile;
    public HashMap<String,Object> Config;
    
    public McMMO(WebPortal instance) {
        plugin = instance;
        profile = new ProfileMcMMO();
        Config = new HashMap<>();
        Plugin pl = plugin.getServer().getPluginManager().getPlugin("mcMMO");
        if(pl != null) {
            WebPortal.logger.log(Level.INFO, "{0} mcMMO set to true and Mcmmo found !!!", plugin.logPrefix);
        }
    }
    
    
    public String getBox(String player) {
       StringBuilder sb = new StringBuilder();
       try {
        GetInfoOnPlugin(player);
        sb = Box(sb); 
       }catch(Exception ex) {
            sb.append("<div id='boxmcmmo'>");
            sb.append("You need to login on server to see your mcmmo stats<br/>");
            sb.append("</div>");
       }
        return sb.toString();
    }
    
    public String getBoxMcMMoMySql(String player) {
       StringBuilder sb = new StringBuilder();
       try {
        GetInfoOnMysql(player);
        sb = Box(sb);
       }catch(Exception ex) {
            sb.append("<div id='boxmcmmo'>");
            sb.append("You need to login on server to see your mcmmo stats<br/>");
            sb.append("</div>");
       }
        return sb.toString();
    }
    
    public void GetInfoOnPlugin(String player) {
        PlayerProfile mcpl = new PlayerProfile(player);
        profile.setEXCAVATION(mcpl.getSkillLevel(PrimarySkillType.EXCAVATION));
        profile.setFISHING(mcpl.getSkillLevel(PrimarySkillType.FISHING));
        profile.setHERBALISM(mcpl.getSkillLevel(PrimarySkillType.HERBALISM));
        profile.setMINING(mcpl.getSkillLevel(PrimarySkillType.MINING));
        profile.setAXES(mcpl.getSkillLevel(PrimarySkillType.AXES));
        profile.setARCHERY(mcpl.getSkillLevel(PrimarySkillType.ARCHERY));
        profile.setSWORDS(mcpl.getSkillLevel(PrimarySkillType.SWORDS));
        profile.setTAMING(mcpl.getSkillLevel(PrimarySkillType.TAMING));
        profile.setUNARMED(mcpl.getSkillLevel(PrimarySkillType.UNARMED));
        profile.setACROBATICS(mcpl.getSkillLevel(PrimarySkillType.ACROBATICS));
        profile.setREPAIR(mcpl.getSkillLevel(PrimarySkillType.REPAIR));
    }
    
    public void GetInfoOnMysql(String player) {
        profile = plugin.db.getMcMMOProfileMySql((String)Config.get("McMMOTablePrefix"), player);
    }
    
    public int getPowerLevel(Player player) {
        return ExperienceAPI.getPowerLevel(player);
    }
    
    public int getLevel(Player player,PrimarySkillType skt) {
        return ExperienceAPI.getLevel(player, skt);
    }
    
    public StringBuilder Box(StringBuilder sb) {
          sb.append("<div id='boxmcmmo'>");
            sb.append("<div style=\"text-align:center;\" >McMMO BOX Info</div><br/>");
            sb.append("<table width=\"100%\"><tr>");
            sb.append("<td colspan='2'><div style=\"text-align:center;\" >GATHERING SKILL</div><br/></td></tr><tr>");
            sb.append("<td>Excavation</td><td>").append(profile.getEXCAVATION()).append("</td></tr><tr>");
            sb.append("<td>Fishing</td><td>").append(profile.getFISHING()).append("</td></tr><tr>");
            sb.append("<td>Herbalism</td><td>").append(profile.getHERBALISM()).append("</td></tr><tr>");
            sb.append("<td>Mining</td><td>").append(profile.getMINING()).append("</td></tr><tr>");
            sb.append("<td colspan='2'><div style=\"text-align:center;\" >COMBAT SKILL</div></td></tr><tr>");
            sb.append("<td>Axes</td><td>").append(profile.getAXES()).append("</td></tr><tr>");
            sb.append("<td>Archery</td><td>").append(profile.getARCHERY()).append("</td></tr><tr>");
            sb.append("<td>Swords</td><td>").append(profile.getSWORDS()).append("</td></tr><tr>");
            sb.append("<td>Taming</td><td>").append(profile.getTAMING()).append("</td></tr><tr>");
            sb.append("<td>Unarmed</td><td>").append(profile.getUNARMED()).append("</td></tr><tr>");
            sb.append("<td colspan='2'><div style=\"text-align:center;\" >MISC SKILL</div></td></tr><tr>");
            sb.append("<td>Acrobatics</td><td>").append(profile.getACROBATICS()).append("</td></tr><tr>");
            sb.append("<td>Repair</td><td>").append(profile.getREPAIR()).append("</td></tr><tr>");
            sb.append("</tr></table>");
            sb.append("Your Power Level is ").append(profile.getPowerlevel()).append("</td><br/>");
        sb.append("</div>");   
        return sb;
    }

    /**
     * @return the mcmmoconfig
     */
    public HashMap<String,Object> getMcmmoconfig() {
        return Config;
    }

    /**
     * @param mcmmoconfig the mcmmoconfig to set
     */
    public void setMcmmoconfig(HashMap<String,Object> mcmmoconfig) {
        this.Config = mcmmoconfig;
    }
}
