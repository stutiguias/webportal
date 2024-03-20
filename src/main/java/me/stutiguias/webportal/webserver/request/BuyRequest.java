/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.request;

import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.init.json.JSONArray;
import me.stutiguias.webportal.init.json.JSONObject;
import me.stutiguias.webportal.model.Shop;
import me.stutiguias.webportal.model.WebItemStack;
import me.stutiguias.webportal.webserver.HttpResponse;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Daniel
 */
@SuppressWarnings("unchecked")
public class BuyRequest extends HttpResponse {
    
    public BuyRequest(WebPortal plugin, HttpExchange exchange) {
        super(plugin);
        setHttpExchange(exchange);
    }
    
    public void AddItem(String sessionId,Map param) {
          try{
            String itemId = (String)param.get("itemId");
            String price = (String)param.get("price");
            String quantity = (String)param.get("quantity");

            WebItemStack Item = ConvertInputToWebItemStack(itemId);
            if(Item == null) {
                Print(message.WebIdNotFound,"text/html");
                return;
            }
            Double Price = Double.parseDouble(price);
            int Quantity = Integer.parseInt(quantity);

            String type = Item.getType().toString();
            String searchtype = Item.GetSearchType();
            String player = WebPortal.AuthPlayers.get(sessionId).WebSitePlayer.getName();

            ItemMeta meta = Item.getItemMeta();
            int dmg = meta != null ? ((Damageable) meta).getDamage() : 0;

            plugin.db.CreateItem(Item.getType().name(), dmg, player, Quantity, Price,"", plugin.Buy, type, searchtype);
            Print(message.WebSucessCreateBuy,"text/html");
          }catch(NumberFormatException ex) {
              ex.printStackTrace();
          }

    }
     
    public void Cancel(Map param,String sessionId) {         
        try {  
            
            int id = Integer.parseInt((String)param.get("id")); 

            Shop auction = plugin.db.getAuction(id);
            String player = auction.getPlayerName();

            if(!WebPortal.AuthPlayers.get(sessionId).WebSitePlayer.getName().equals(player)) {
                Print(message.WebIdNotFound,"text/plain");
            }

            int result = plugin.db.DeleteAuction(id);
            if(result == 0) {
                 Print(message.WebInvalidNumber,"text/plain");
                 return;
            }
            
        }catch(NumberFormatException ex) {
            Print(message.WebInvalidNumber,"text/plain");
            return;
        }
        Print(message.WebCancelDone,"text/plain");
    }
    
    public void GetItems(String sessionId,Map param) {
        Integer from = Integer.parseInt((String)param.get("from"));
        Integer qtd = Integer.parseInt((String)param.get("qtd"));
        
        String player = WebPortal.AuthPlayers.get(sessionId).WebSitePlayer.getName();
        List<Shop> shops = plugin.db.GetBuyList(player,from,qtd);
        
        JSONObject json;
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < shops.size(); i++) {
            Shop shop = shops.get(i);
           
            json = new JSONObject();
            json.put("1",JSON("Id",shop.getId()));
            json.put("2",JSON(message.WebItemName,ConvertItemToResult(shop.getItemStack())));
            json.put("3",JSON(message.WebQuantity,shop.getItemStack().getAmount()));
            json.put("4",JSON(message.WebItemCategory,shop.getItemStack().GetSearchType()));
            json.put("5",JSON(message.WebPrice,shop.getPrice()));
            jsonArray.add(json);
        }
        JSONObject jsonresult = new JSONObject();
        
        jsonresult.put(plugin.db.getFound(),jsonArray);
        Print(jsonresult.toJSONString(),"application/json");
    }
    
}
