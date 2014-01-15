/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver;

import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.URLDecoder;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.init.Messages;
import me.stutiguias.webportal.settings.Enchant;
import me.stutiguias.webportal.settings.Shop;
import me.stutiguias.webportal.settings.WebItemStack;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.json.simple.JSONObject;

/**
 *
 * @author Daniel
 */
public class HttpResponse {
    
    public WebPortal plugin;
    private HttpExchange httpExchange;
    public Messages message;
    
    public HttpResponse(WebPortal plugin)
    {
        this.plugin = plugin;
        message = WebPortal.Messages;
    }

    public void Print(String data, String MimeType)
    {
        try
        {       
            getHttpExchange().getResponseHeaders().set("Content-Type", MimeType);
            getHttpExchange().getResponseHeaders().set("Server","WebPortal Server");
            getHttpExchange().getResponseHeaders().set("Connection","Close");
            getHttpExchange().getResponseHeaders().set("Cache-Control","no-cache, must-revalidate");
            if(plugin.EnableExternalSource) {
                getHttpExchange().getResponseHeaders().set("Access-Control-Allow-Origin",plugin.allowexternal);
            }
            getHttpExchange().sendResponseHeaders(200,data.getBytes().length);
            getHttpExchange().getResponseBody().write(data.getBytes());
            getHttpExchange().getResponseBody().flush();
            getHttpExchange().getResponseBody().close();
        }
        catch(IOException e)
        {
            WebPortal.logger.info((new StringBuilder()).append("ERROR in print(): ").append(e.getMessage()).toString());
            e.printStackTrace();
        }
    }

    public void Error(String error)
    {
        try
        {
            getHttpExchange().getResponseHeaders().set("Content-Type", "text/html");
            getHttpExchange().getResponseHeaders().set("Server","WebPortal Server");
            getHttpExchange().getResponseHeaders().set("Connection","Close");
            if(plugin.EnableExternalSource) {
               getHttpExchange().getResponseHeaders().set("Access-Control-Allow-Origin",plugin.allowexternal);
            }
            getHttpExchange().sendResponseHeaders(400, error.getBytes().length);
            getHttpExchange().getResponseBody().write(error.getBytes());
            getHttpExchange().getResponseBody().flush();
            getHttpExchange().getResponseBody().close();
        }
        catch(IOException e)
        {
            WebPortal.logger.info((new StringBuilder()).append("ERROR in httperror(): ").append(e.getMessage()).toString());
        }
    }
    
    public void ReadFile(String path,String Mime) throws IOException
    {
        try
        {
            File archivo = new File(path);
            if(archivo.exists())
            {
                try (FileInputStream file = new FileInputStream(archivo)) {
                    byte[] buffer = new byte[0x10000];
                    long length = archivo.length();
                    int leng;
                    
                    if(plugin.EnableExternalSource) {
                        getHttpExchange().getResponseHeaders().set("Access-Control-Allow-Origin",plugin.allowexternal);
                    }
                    
                    getHttpExchange().getResponseHeaders().set("Content-Type", Mime);
                    getHttpExchange().sendResponseHeaders(200, length);
                    
                    try (OutputStream out = getHttpExchange().getResponseBody()) {
                        while ((leng = file.read(buffer)) >= 0) {
                            out.write(buffer, 0, leng);
                        }
                    }
                    
                }
                
            } else
            {
                Error("404 Not Found");
            }
        }
        catch(IOException e)
        {
            WebPortal.logger.info((new StringBuilder()).append("ERROR in readFileAsBinary(): ").append(e.getMessage()).toString());
        }
    }
    
    public String GetParam(String param, String URL)
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

    /**
     * @return the httpExchange
     */
    public HttpExchange getHttpExchange() {
        return httpExchange;
    }

    /**
     * @param httpExchange the httpExchange to set
     */
    public void setHttpExchange(HttpExchange httpExchange) {
        this.httpExchange = httpExchange;
    }
    
    public WebItemStack ConvertToItemStack(String ItemId) {
        Integer Name;
        Short Damage;
        if(ItemId.contains(":")) {
            String[] NameDamage = ItemId.split(":");
            Name = Integer.parseInt(NameDamage[0]);
            Damage = Short.parseShort(NameDamage[1]);
        }else{
            Name = Integer.parseInt(ItemId);
            Damage = 0;
        }
        WebItemStack item = new WebItemStack(Name ,1,Damage);
        return item; 
    }
           
    public double MarketPrice(Shop item,Double price) {
           double mprice = plugin.db.GetMarketPriceofItem(item.getItemStack().getTypeId(),item.getItemStack().getDurability());
           if(mprice == 0.0) {
             return 0.0;
           }
           return (( price * 100 ) / mprice);
    }   
        
    public String GetDurability(Shop item) {
        Short dmg = item.getItemStack().getDurability();
        Short maxdur = item.getItemStack().getType().getMaxDurability();
        String Durability = "";
        if(!item.getItemStack().getType().isBlock() && !item.getItemStack().isPotion() && maxdur != 0) {
            Durability = dmg + "/" + maxdur;
        }
        return Durability;
    }
            
    public String GetEnchant(Shop item) {
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
         
    public Boolean isAdmin(String sessionId) {
        return WebPortal.AuthPlayers.get(sessionId).WebSitePlayer.getIsAdmin() == 1;
    }
    
    public String Format(double x) {  
        return String.format("%.2f", x);  
    } 
    
    public String ConvertItemToResult(int itemId,WebItemStack item,String type) {

        String itemName = item.getName();
        String itemImage = item.getImage();
        
        String metaCSV = plugin.db.GetItemInfo(itemId,"meta");
        item.SetMetaItemName(metaCSV);
        
        if(!itemImage.contains("http") || !itemImage.contains("www"))
            itemImage = String.format("images/%s",itemImage);
        
        return String.format("<div class='itemTableName'><img src='%s' style='max-height:32px;max-width:32px;' /> %s</div>",itemImage,itemName);
        
    }
    
    public String ConvertItemToResult(Shop item,String type) {
        return ConvertItemToResult(item.getId(),item.getItemStack(), type);
    }
    
    public JSONObject JSON (String title,Object value) {
        JSONObject json = new JSONObject();
        json.put("Title",title);
        json.put("Val",value); 
        return json;
    }
}
