/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.settings.Auction;
import me.stutiguias.webportal.settings.Enchant;
import org.bukkit.enchantments.Enchantment;

/**
 *
 * @author Daniel
 */
public class Response {
    
    WebPortal plugin;
    Socket WebServerSocket;
    
    public Response(WebPortal plugin,Socket s)
    {
        this.plugin = plugin;
        WebServerSocket = s;
    }
        
    public Boolean isAdmin(String Hostadress) {
        if (WebPortal.AuthPlayers.get(Hostadress).AuctionPlayer.getIsAdmin() == 1) {
          return true;
        }else{
          return false;
        }
    }
    
    public void print(String data, String MimeType)
    {
        try
        {
            DataOutputStream out = new DataOutputStream(WebServerSocket.getOutputStream());
            out.writeBytes("HTTP/1.1 200 OK\r\n");
            out.writeBytes((new StringBuilder()).append("Content-Type: ").append(MimeType).append("; charset=utf-8\r\n").toString());
            out.writeBytes("Cache-Control: no-cache, must-revalidate\r\n");
            out.writeBytes((new StringBuilder()).append("Content-Length: ").append(data.length()).append("\r\n").toString());
            out.writeBytes("Server: webportal Server\r\n");
            out.writeBytes("Connection: Close\r\n\r\n");
            out.write(data.getBytes());
            out.flush();
            out.close();
        }
        catch(Exception e)
        {
            WebPortal.logger.info((new StringBuilder()).append("ERROR in print(): ").append(e.getMessage()).toString());
        }
    }

    public void httperror(String error)
    {
        try
        {
            DataOutputStream out = new DataOutputStream(WebServerSocket.getOutputStream());
            out.writeBytes((new StringBuilder()).append("HTTP/1.1 ").append(error).append("\r\n").toString());
            out.writeBytes("Server: webportal Server\r\n");
            out.writeBytes("Connection: Close\r\n\r\n");
            out.flush();
            out.close();
        }
        catch(Exception e)
        {
            WebPortal.logger.info((new StringBuilder()).append("ERROR in httperror(): ").append(e.getMessage()).toString());
        }
    }
    
    public void readFileAsBinary(String path,String Mime) throws IOException
    {
        try
        {
            File archivo = new File(path);
            if(archivo.exists())
            {
                OutputStream out = WebServerSocket.getOutputStream();
                FileInputStream file = new FileInputStream(archivo);
                byte fileData[] = new byte[0x10000];
                long length = archivo.length();
                int leng;
                out.write("HTTP/1.1 200 OK\r\n".getBytes());
                out.write((new StringBuilder()).append("Content-Type: ").append(Mime).append("; charset=utf-8\r\n").toString().getBytes());
                out.write("Cache-Control: no-cache, must-revalidate\r\n".getBytes());
                out.write((new StringBuilder()).append("Content-Length: ").append(length).append("\r\n").toString().getBytes());
                out.write("Server: webportal server\r\n".getBytes());
                out.write("Connection: Close\r\n\r\n".getBytes());
                while ((leng = file.read(fileData)) > 0) {
                    out.write(fileData, 0, leng);
                }
                out.flush();
                out.close();
                file.close();
                
            } else
            {
                httperror("404 Not Found");
            }
        }
        catch(Exception e)
        {
            WebPortal.logger.info((new StringBuilder()).append("ERROR in readFileAsBinary(): ").append(e.getMessage()).toString());
        }
    }
    
    public String getParam(String param, String URL)
    {
        Pattern regex = Pattern.compile("[\\?&]"+param+"=([^&#]*)");
        Matcher result = regex.matcher(URL);
        if(result.find()){
            try{
                String resdec = URLDecoder.decode(result.group(1),"UTF-8");
                return resdec;
            }catch (UnsupportedEncodingException e){
                WebPortal.logger.log(Level.INFO, "{0} ERROR in getParam(): {1}", new Object[]{plugin.logPrefix, e.getMessage()});
                return "";
            }
        }else
        return "";
    }
            
    public String format(double x) {  
        return String.format("%.2f", x);  
    } 
    
    public String ConvertItemToResult(Auction item,String type) {
        String item_name;
        String img_name;
        Short dmg = item.getItemStack().getDurability();
        String Durability = "";
        
        // Not is a block ( have durability )
        if(!item.getItemStack().getType().isBlock()) {
            Durability = (!dmg.equals(Short.valueOf("0"))) ? "Dur.: " + dmg + "%" : "";
        }
        img_name = Material.getItemName(item.getItemStack().getTypeId(),item.getItemStack().getDurability());
      
        if(!(item.getItemStack().getType() == org.bukkit.Material.POTION)) {
            item_name = getConfigName(img_name,type);
        }else{
            item_name = Material.getItemName(item.getItemStack().getTypeId(),item.getItemStack().getDurability());
            Durability = "";
        }
        
        // Enchant if need
        String enchant = "";
        for (Map.Entry<Enchantment, Integer> entry : item.getItemStack().getEnchantments().entrySet()) {
            int enchId = entry.getKey().getId();
            int level = entry.getValue();
            enchant += "<br />" + new Enchant().getEnchantName(enchId, level);
        }
        
        if(item.getItemStack().getType() == org.bukkit.Material.POTION) {
            return "<img src='images/potion.png'><br /><font size='-1'>"+ item_name + "<br />" + Durability + enchant +"</font>";
        }else{
            return "<img src='images/"+ img_name.replace(" ","_").toLowerCase() +".png'><br /><font size='-1'>"+ item_name + "<br />" + Durability + enchant +"</font>";
        }
    }
    
    public String getConfigName(String Itemname,String type) {
            try {
                for (Iterator<String> it = plugin.materials.getConfig().getConfigurationSection(type).getKeys(false).iterator(); it.hasNext();) {
                    String key = it.next();
                    if(key.equalsIgnoreCase(Itemname)) {
                        return plugin.materials.getConfig().getString(type + "." + key);
                    }
                }
            }catch(NullPointerException ex){
                
            }
            return Itemname;
    }
    
    public String getConfigKey(String Itemname,String type) {
            try {
                for (Iterator<String> it = plugin.materials.getConfig().getConfigurationSection(type).getKeys(false).iterator(); it.hasNext();) {
                    String key = it.next();
                    if(Itemname.equalsIgnoreCase(plugin.materials.getConfig().getString(type + "." + key))) {
                        return key;
                    }
                }
            }catch(NullPointerException ex){
                
            }
            return Itemname;
    }
}
