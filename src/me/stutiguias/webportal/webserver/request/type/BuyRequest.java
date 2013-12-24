/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.request.type;

import java.util.List;
import java.util.Map;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.settings.Shop;
import me.stutiguias.webportal.webserver.HttpResponse;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Daniel
 */
public class BuyRequest extends HttpResponse {
    
    public BuyRequest(WebPortal plugin) {
        super(plugin);
    }
    
    public void AddItem(String sessionId,Map param) {
          try{
            String itemId = (String)param.get("itemId");
            String price = (String)param.get("price");
            String quantity = (String)param.get("quantity");

            ItemStack Item = ConvertToItemStack(itemId);
            if(Item == null) Print(message.WebIdNotFound,"text/html");
            Double Price = Double.parseDouble(price);
            Integer Quantity = Integer.parseInt(quantity);

            String type = Item.getType().toString();
            String searchtype = GetSearchType(Item);
            String player = WebPortal.AuthPlayers.get(sessionId).WebSitePlayer.getName();
            plugin.dataQueries.createItem(Item.getTypeId(), Item.getDurability(), player, Quantity, Price,"", plugin.Buy, type, searchtype);
            Print(message.WebSucessCreateBuy,"text/html");
          }catch(Exception ex) {
              ex.printStackTrace();
          }

    }
     
    public void Cancel(Map param) {
        try {
            int id = Integer.parseInt((String)param.get("id"));
            int result = plugin.dataQueries.DeleteAuction(id);
            if(result == 0) {
                 Print(message.WebInvalidNumber,"text/plain");
                 return;
            }
        }catch(NumberFormatException ex) {
            Print(message.WebInvalidNumber,"text/plain");
            return;
        }catch(Exception ex) {
            Print(message.WebInvalidNumber,"text/plain");
            return;
        }
        Print(message.WebCancelDone,"text/plain");
    }
    
    public void GetItems(String sessionId,Map param) {
        Integer from = Integer.parseInt((String)param.get("from"));
        Integer qtd = Integer.parseInt((String)param.get("qtd"));
        
        String player = WebPortal.AuthPlayers.get(sessionId).WebSitePlayer.getName();
        List<Shop> shops = plugin.dataQueries.GetBuyList(player,from,qtd);
        
        JSONObject json;
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < shops.size(); i++) {
            Shop shop = shops.get(i);
           
            json = new JSONObject();
            json.put("1",JSON("Id",shop.getId()));
            json.put("2",JSON(message.WebItemName,ConvertItemToResult(shop,shop.getType())));
            json.put("3",JSON(message.WebQuantity,shop.getItemStack().getAmount()));
            json.put("4",JSON(message.WebItemCategory,GetSearchType(shop.getItemStack())));
            json.put("5",JSON(message.WebPrice,shop.getPrice()));
            jsonArray.add(json);
        }
        JSONObject jsonresult = new JSONObject();
        
        jsonresult.put(plugin.dataQueries.getFound(),jsonArray);
        Print(jsonresult.toJSONString(),"application/json");
    }
    
    public ItemStack ConvertToItemStack(String ItemId) {
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
        ItemStack item = new ItemStack(Name ,1,Damage);
        return item; 
    }
}
