/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.plugins.Esssentials;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import me.stutiguias.webportal.init.WebPortal;
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
            WebPortal.logger.log(Level.INFO, plugin.logPrefix + " Essentials set to true and Essentials found !!!");
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
        sb.append("<div id='essentials'>");
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
                if(!homes.isEmpty()) {
                    sb.append("( ");
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
               List<Map<?,?>> mails = profile.GetMail();
               sb.append("You have ").append(mails.size()).append(" mail(s)<br/>Mails:<br/>");
               for(Map<?, ?> mail:mails) {
                   sb.append("msg : ").append(mail.get("message")).append("<br/>")
                     .append("de : ").append(mail.get("sender-name")).append("<br/>");
               }
               sb.append("<br/>");
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
