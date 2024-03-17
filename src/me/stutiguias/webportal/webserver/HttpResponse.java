/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver;

import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.URLDecoder;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.init.Messages;
import me.stutiguias.webportal.init.json.JSONObject;
import me.stutiguias.webportal.model.Enchant;
import me.stutiguias.webportal.model.Shop;
import me.stutiguias.webportal.model.WebItemStack;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

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
    

    public double MarketPrice(Shop item,Double price) {
           double mprice = plugin.db.GetMarketPriceofItem(item.getItemStack().getType().name(),item.getItemStack().getDurability());
           if(mprice == 0.0) {
             return 0.0;
           }
           return (( price * 100 ) / mprice);
    }   
        
    public String GetDurability(Shop item) {
        int dmg = ((Damageable) Objects.requireNonNull(item.getItemStack().getItemMeta())).getDamage();
        int maxdur = item.getItemStack().getType().getMaxDurability();
        String Durability = "";
        if(maxdur != 0) Durability = dmg + "/" + maxdur;
        return Durability;
    }
            
    public String GetEnchant(Shop item) {
        StringBuilder enchant = new StringBuilder();
        for (Map.Entry<Enchantment, Integer> entry : item.getItemStack().getEnchantments().entrySet()) {
            String enchId = entry.getKey().getKey().getKey();
            int level = entry.getValue();
            enchant.append(new Enchant().getEnchantName(enchId, level)).append("<br />");
        }
        if(item.getItemStack().getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta bookmeta = (EnchantmentStorageMeta)item.getItemStack().getItemMeta();
            for (Map.Entry<Enchantment, Integer> entry : bookmeta.getStoredEnchants().entrySet()) {
                String enchId = entry.getKey().getKey().getKey();
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
    
    public String ConvertItemToResult(WebItemStack item) {

        // TODO Analisar meta itens
        //String metaCSV = plugin.db.GetItemInfo(itemId,"meta");
        //item.SetMetaItemNameForDisplay(metaCSV,true);
        
        String itemName = item.getName().substring(0, 1).toUpperCase() + item.getName().toLowerCase().substring(1);
        String itemImage = item.getImage();
 
        if(!itemImage.contains("http") || !itemImage.contains("www"))
            itemImage = String.format("images/%s.png",itemName);
        
        return String.format("<div class='itemTableName'><img src='%s' style='max-height:32px;max-width:32px;' /> %s</div>",itemImage,itemName);
        
    }

    public JSONObject JSON(String title,Object value) {
        JSONObject json = new JSONObject();
        json.put("Title",title);
        json.put("Val",value); 
        return json;
    }
    
      public WebItemStack ConvertInputToWebItemStack(String itemName) {
        return new WebItemStack(Material.getMaterial(itemName.toUpperCase()) ,1,0);
    }
}
