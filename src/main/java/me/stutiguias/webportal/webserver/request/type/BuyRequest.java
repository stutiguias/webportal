/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.request.type;

import java.util.List;
import java.util.Map;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.settings.Auction;
import me.stutiguias.webportal.webserver.HttpResponse;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Daniel
 */
public class WithListRequest extends HttpResponse {
    
    private WebPortal plugin;
    
    public WithListRequest(WebPortal plugin) {
        super(plugin);
        this.plugin = plugin;
        
    }
    
    public void AddItem(String sessionId,Map param) {
          try{
            String itemId = (String)param.get("itemId");
            String price = (String)param.get("price");
            String quantity = (String)param.get("quantity");

            ItemStack Item = ConvertToItemStack(itemId);
            if(Item == null) Print("Item ID not found","text/html");
            Double Price = Double.parseDouble(price);
            Integer Quantity = Integer.parseInt(quantity);

            String type = Item.getType().toString();
            String searchtype = GetSearchType(Item);
            String player = WebPortal.AuthPlayers.get(sessionId).AuctionPlayer.getName();
            plugin.dataQueries.createItem(Item.getTypeId(), Item.getDurability(), player, Quantity, Price,"", plugin.WithList, type, searchtype);
            Print("ok","text/html");
          }catch(Exception ex) {
              ex.printStackTrace();
          }

    }
     
    public void GetItems(String sessionId,Map param) {
        Integer to = Integer.parseInt((String)param.get("to"));
        Integer from = Integer.parseInt((String)param.get("from"));
        
        String player = WebPortal.AuthPlayers.get(sessionId).AuctionPlayer.getName();
        List<Auction> auctions = plugin.dataQueries.GetWithList(player,to,from);
        int founds = plugin.dataQueries.getFound();
        JSONObject json;
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < auctions.size(); i++) {
            Auction auction = auctions.get(i);
            String[] itemConfig = GetItemConfig(auction.getItemStack());
            
            if(plugin.AllowMetaItem) {
                itemConfig[0] = ChangeItemToItemMeta(auction, itemConfig[0]);
            }
            
            json = new JSONObject();
            json.put("Id",auction.getId());
            json.put("Item Name",itemConfig[0]);
            json.put("Quantity",auction.getItemStack().getAmount());
            json.put("Image",itemConfig[1]);
            json.put("Item Category",GetSearchType(auction.getItemStack()));
            json.put("Price",auction.getPrice());
            jsonArray.add(json);
        }
        JSONObject jsonresult = new JSONObject();
        jsonresult.put(founds,jsonArray);
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
