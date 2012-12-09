/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.plugins;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.util.Users;
import java.util.logging.Level;
import me.stutiguias.webportal.init.WebPortal;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Daniel
 */
public class McMMO {
    
    public WebPortal plugin;
    public ProfileMcMMO profile;
    
    public McMMO(WebPortal instance) {
        plugin = instance;
        profile = new ProfileMcMMO();
        Plugin pl = plugin.getServer().getPluginManager().getPlugin("mcMMO");
        if(pl != null) {
            WebPortal.logger.log(Level.INFO, "{0} PortalBox mcMMO set to true and Mcmmo found !!!", plugin.logPrefix);
        }
    }
    
    
    public String getBox(OfflinePlayer player) {
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
    
    public String getBox(String player) {
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
    
    public void GetInfoOnPlugin(OfflinePlayer player) {
        PlayerProfile mcpl = Users.getProfile(player);
        profile.setEXCAVATION(mcpl.getSkillLevel(SkillType.EXCAVATION));
        profile.setFISHING(mcpl.getSkillLevel(SkillType.FISHING));
        profile.setHERBALISM(mcpl.getSkillLevel(SkillType.HERBALISM));
        profile.setMINING(mcpl.getSkillLevel(SkillType.MINING));
        profile.setAXES(mcpl.getSkillLevel(SkillType.AXES));
        profile.setARCHERY(mcpl.getSkillLevel(SkillType.ARCHERY));
        profile.setSWORDS(mcpl.getSkillLevel(SkillType.SWORDS));
        profile.setTAMING(mcpl.getSkillLevel(SkillType.TAMING));
        profile.setUNARMED(mcpl.getSkillLevel(SkillType.UNARMED));
        profile.setACROBATICS(mcpl.getSkillLevel(SkillType.ACROBATICS));
        profile.setREPAIR(mcpl.getSkillLevel(SkillType.REPAIR));
    }
    
    public void GetInfoOnMysql(String player) {
        profile = plugin.dataQueries.getMcMMOProfileMySql((String)plugin.mcmmoconfig.get("McMMOTablePrefix"), player);
    }
    
    public int getPowerLevel(Player player) {
        return ExperienceAPI.getPowerLevel(player);
    }
    
    public int getLevel(Player player,SkillType skt) {
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
}
