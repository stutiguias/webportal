/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.plugins;

import java.util.List;
import java.util.logging.Level;
import me.stutiguias.webportal.init.WebPortal;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Daniel
 */
public class Essentials {
     public WebPortal plugin;
    public ProfileEssentials profile;
    
    public Essentials(WebPortal instance) {
        plugin = instance;
        Plugin pl = plugin.getServer().getPluginManager().getPlugin("Essentials");
        if(pl != null) {
            WebPortal.logger.log(Level.INFO, plugin.logPrefix + "PortalBox Essentials set to true and Essentials found !!!");
        }
    }
    
    
    public String getBox(String player) {
        StringBuilder sb = new StringBuilder();
        profile = new ProfileEssentials(player);
        if(profile.LoadProfile()) {
            sb = BOX(sb);
        }else{
            sb.append("Erro Loading User File");
        }
        return sb.toString();
    }
    
    public StringBuilder BOX(StringBuilder sb) {
        sb.append("<div id='boxmcmmo'>");
            sb.append("<div style=\"text-align:center;\" >Essentials Info</div><br/>");
            sb = BoxIp(sb);
            sb = BoxMail(sb);
            sb = BoxHomes(sb);
        sb.append("</div>");
        return sb;
    }
    
    public StringBuilder BoxHomes(StringBuilder sb) {
        try {
            List<String> homes = profile.GetHomes();
            if(homes != null) {
                sb.append("You have ").append(homes.size()).append(" homes<br/>");
                sb.append("( ");
                if(homes.size() > 0) {
                    for (String home:homes) {
                        sb.append(home).append(",");
                    }
                sb.append(" )<br/>");
                }
            }else{
                sb.append("You have 0 homes<br/>");
            }
        }catch(Exception ex){
            ex.printStackTrace();
            sb.append("Unable to get homes");
        }
        return sb;
    }
    
    public StringBuilder BoxMail(StringBuilder sb) {
           try {
                List<String> mails = profile.GetMail();
                sb.append("You have ").append(mails.size()).append(" mail(s)<br/>");
                String showmails = "";
                for(String mail:mails) {
                showmails += mail + "<br/>";
                }
                sb.append("<input class=\"button\" id=\"mailread\" type=\"button\" value=\"Read\"><br/>");
                sb.append("<div id=\"mail\" style=\"display:none;padding:10px;background:#E7E7E7; border:1px solid black;border-radius: 6px;-moz-border-radius: 6px;position:absolute;z-index:100;top:30%;left:40;\">").append(showmails).append("<br/>");
                sb.append("<input class=\"button\" id=\"mailclose\" type=\"button\" value=\"Close\"></div>");
           }catch(Exception ex) {
                ex.printStackTrace();
                sb.append("Unable to get Mail");
           }
           return sb;
    }
    
    public StringBuilder BoxIp(StringBuilder sb) {
          try {
              sb.append("Your IP is ").append(profile.GetIp()).append("<br/>");
              return sb;
          }catch(Exception ex){
              ex.printStackTrace();
              sb.append("Unable to get IP");
          }
          return sb;
    }
}
