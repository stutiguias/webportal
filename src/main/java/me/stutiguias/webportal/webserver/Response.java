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
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

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
       
        String[] nameAndImg = getItemNameAndImg(item.getItemStack());
        String item_name = nameAndImg[0];
        String img_name = nameAndImg[1];
        
        if(!img_name.contains("http") || !img_name.contains("www"))
            img_name = String.format("images/%s",img_name);
        
        return String.format("<div class='itemTableName'><img src='%s' style='max-height:32px;max-width:32px;' /><br />%s</div>",img_name,item_name);
        
    }
    
    public String GetEnchant(Auction item) {
        StringBuilder enchant = new StringBuilder();
        for (Map.Entry<Enchantment, Integer> entry : item.getItemStack().getEnchantments().entrySet()) {
            int enchId = entry.getKey().getId();
            int level = entry.getValue();
            enchant.append(new Enchant().getEnchantName(enchId, level)).append("<br />");
        }
        if(item.getItemStack().getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta bookmeta = (EnchantmentStorageMeta)item.getItemStack().getItemMeta();
            for (Map.Entry<Enchantment, Integer> entry : bookmeta.getStoredEnchants().entrySet()) {
                int enchId = entry.getKey().getId();
                int level = entry.getValue();
                enchant.append(new Enchant().getEnchantName(enchId, level)).append("<br />");
            }
        }
        return enchant.toString();
    }
    
    public String GetDurability(Auction item) {
        Short dmg = item.getItemStack().getDurability();
        Short maxdur = item.getItemStack().getType().getMaxDurability();
        String Durability = "";
        if(!item.getItemStack().getType().isBlock() && !isPotion(item.getItemStack())) {
            Durability = dmg + "/" + maxdur;
        }
        return Durability;
    }
    
    public Boolean isPotion(ItemStack item) {
        return item.getType() == org.bukkit.Material.POTION;
    }
    
    public String[] getItemNameAndImg(ItemStack item) {
        
        String itemId = getItemId(item);
        String itemConfig;
        
        String SearchType = plugin.getSearchType(itemId);
        
        if(!SearchType.equalsIgnoreCase("Others"))
            itemConfig = getConfigName(itemId,SearchType);
        else
            itemConfig = "Not Found,Not Found";
        
        itemConfig += "," + SearchType;
        
        return itemConfig.split(",");
    }
    
    public String getItemId(ItemStack item) {
        
        String itemId;
        Short dmg = item.getDurability();
        if( ( item.getType().isBlock() || isPotion(item) ) && !dmg.equals(Short.valueOf("0")) ) 
            itemId = item.getTypeId() + "-" + item.getDurability();
        else
            itemId = String.valueOf(item.getTypeId());
        return itemId;
        
    }
    
    private String getConfigName(String itemId,String type) {
            for (Iterator<String> it = plugin.materials.getConfig().getConfigurationSection(type).getKeys(false).iterator(); it.hasNext();) {
                String key = it.next();
                if(key.equalsIgnoreCase(itemId)) {
                    return plugin.materials.getConfig().getString(type + "." + key);
                }
            }
            return "Not Found,Not Found";
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
    
    public double MarketPrice(Auction item,Double price) {
           double mprice = plugin.dataQueries.GetMarketPriceofItem(item.getItemStack().getTypeId(),item.getItemStack().getDurability());
           if(mprice == 0.0) {
             return 0.0;
           }
           return (( price * 100 ) / mprice);
    }
}
