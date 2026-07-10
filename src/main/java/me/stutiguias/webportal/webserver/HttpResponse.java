/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver;

import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.init.Messages;
import me.stutiguias.webportal.init.json.JSONArray;
import me.stutiguias.webportal.init.json.JSONObject;
import me.stutiguias.webportal.model.Enchant;
import me.stutiguias.webportal.model.Shop;
import me.stutiguias.webportal.model.WebItemStack;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Daniel
 */
@SuppressWarnings("unchecked")
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
            byte[] response = data.getBytes(StandardCharsets.UTF_8);
            getHttpExchange().getResponseHeaders().set("Content-Type", contentTypeWithCharset(MimeType));
            getHttpExchange().getResponseHeaders().set("Server","WebPortal Server");
            getHttpExchange().getResponseHeaders().set("Connection","Close");
            getHttpExchange().getResponseHeaders().set("Cache-Control","no-cache, must-revalidate");
            if(plugin.EnableExternalSource) {
                getHttpExchange().getResponseHeaders().set("Access-Control-Allow-Origin",plugin.allowexternal);
                getHttpExchange().getResponseHeaders().set("Access-Control-Allow-Credentials", "true");
            }
            getHttpExchange().sendResponseHeaders(200,response.length);
            getHttpExchange().getResponseBody().write(response);
            getHttpExchange().getResponseBody().flush();
            getHttpExchange().getResponseBody().close();
        }
        catch(IOException e)
        {
            WebPortal.logger.info("ERROR in print(): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendJsonResponse(String jsonResponse) {
        try {
            HttpExchange exchange = getHttpExchange();
            byte[] response = jsonResponse.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            exchange.getResponseHeaders().set("Server", "WebPortal Server");
            exchange.getResponseHeaders().set("Connection", "Close");
            exchange.getResponseHeaders().set("Cache-Control", "no-cache, must-revalidate");
            if (plugin.EnableExternalSource) {
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", plugin.allowexternal);
                exchange.getResponseHeaders().set("Access-Control-Allow-Credentials", "true");
            }
            exchange.sendResponseHeaders(200, response.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SendResultCMD(List<String> messages, String webSitePlayerName, String x, String nickname) {
        JSONObject json = new JSONObject();
        String cleanedResult = messages.stream()
                .map(this::removeColorCodes)
                .collect(Collectors.joining("\n"));
        json.put("result", cleanedResult);

        JSONArray jsonarray = new JSONArray();
        jsonarray.add(json);

        plugin.getLogger().info("Sender by : " + webSitePlayerName);
        plugin.getLogger().info(x + nickname);
        plugin.getLogger().info("Result : " + cleanedResult);

        sendJsonResponse(jsonarray.toJSONString());
    }

    public String removeColorCodes(String textWithCodes) {
        return textWithCodes.replaceAll("§[0-9a-fk-or]", "");
    }

    public boolean PrintWithReturn(String data, String MimeType)
    {
        Print(data,MimeType);
        return true;
    }

    public void Error(String error)
    {
        try
        {
            byte[] response = error.getBytes(StandardCharsets.UTF_8);
            getHttpExchange().getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
            getHttpExchange().getResponseHeaders().set("Server","WebPortal Server");
            getHttpExchange().getResponseHeaders().set("Connection","Close");
            if(plugin.EnableExternalSource) {
               getHttpExchange().getResponseHeaders().set("Access-Control-Allow-Origin",plugin.allowexternal);
            }
            getHttpExchange().sendResponseHeaders(400, response.length);
            getHttpExchange().getResponseBody().write(response);
            getHttpExchange().getResponseBody().flush();
            getHttpExchange().getResponseBody().close();
        }
        catch(IOException e)
        {
            WebPortal.logger.info("ERROR in httperror(): " + e.getMessage());
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
            WebPortal.logger.info("ERROR in readFileAsBinary(): " + e.getMessage());
        }
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
        ItemMeta meta = item.getItemStack().getItemMeta();
        int dmg = meta != null ?  ((Damageable) meta).getDamage() : 0;
        double mprice = plugin.db.GetMarketPriceofItem(item.getItemStack().getType().name(),dmg);
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
            for (Map.Entry<Enchantment, Integer> entry : Objects.requireNonNull(bookmeta).getStoredEnchants().entrySet()) {
                String enchId = entry.getKey().getKey().getKey();
                int level = entry.getValue();
                enchant.append(new Enchant().getEnchantName(enchId, level)).append("<br />");
            }
        }
        return enchant.toString();
    }
         
    public Boolean isAdmin(String sessionId) {
        if (sessionId == null || !WebPortal.AuthPlayers.containsKey(sessionId)) {
            return false;
        }
        return WebPortal.AuthPlayers.get(sessionId).WebSitePlayer.getIsAdmin() == 1;
    }
    
    public String Format(double x) {  
        return String.format("%.2f", x);  
    } 
    
    public String ConvertItemToResult(WebItemStack item) {

        // TODO Analisar meta itens
        //String metaCSV = plugin.db.GetItemInfo(itemId,"meta");
        //item.SetMetaItemNameForDisplay(metaCSV,true);
        
        String rawItemName = item.getName() != null ? item.getName() : "";
        String itemName = rawItemName.isEmpty()
                ? "Unknown"
                : rawItemName.substring(0, 1).toUpperCase() + rawItemName.toLowerCase().substring(1);
        String itemImage = item.getImage() != null ? item.getImage() : "";

        if (!itemImage.matches("(?i)^https?://.*") && !itemImage.startsWith("images/")) {
            String imageName = itemImage.isEmpty() ? rawItemName : itemImage;
            itemImage = String.format("images/%s.png", imageName);
        }
        
        return String.format("<div class='itemTableName'><img src='%s' style='max-height:32px;max-width:32px;' /> %s</div>",
                escapeHtmlAttribute(itemImage),
                escapeHtml(itemName));
        
    }

    public JSONObject JSON(String title,Object value) {
        JSONObject json = new JSONObject();
        json.put("Title",title);
        json.put("Val",value); 
        return json;
    }
    
      public WebItemStack ConvertInputToWebItemStack(String itemName) {
        if (itemName == null || itemName.trim().isEmpty()) {
            return null;
        }
        return new WebItemStack(Material.getMaterial(itemName.toUpperCase()) ,1,0);
    }

    private String contentTypeWithCharset(String mimeType) {
        if (mimeType == null || mimeType.toLowerCase().contains("charset")) {
            return mimeType;
        }
        if (mimeType.startsWith("text/") || mimeType.equals("application/json")) {
            return mimeType + "; charset=utf-8";
        }
        return mimeType;
    }

    private String escapeHtml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }

    private String escapeHtmlAttribute(String value) {
        return escapeHtml(value).replace("`", "&#x60;");
    }
}
