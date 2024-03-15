/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.request;

import java.util.List;
import java.util.Map;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.init.json.JSONArray;
import me.stutiguias.webportal.init.json.JSONObject;
import me.stutiguias.webportal.model.Shop;
import me.stutiguias.webportal.webserver.HttpResponse;
import org.bukkit.Material;

/**
 *
 * @author Daniel
 */
public class SellRequest extends HttpResponse {

    public SellRequest(WebPortal plugin) {
        super(plugin);
    }
    
    public void GetSell(String ip,String url,Map param) {
        
        Integer from = Integer.parseInt((String)param.get("from"));
        Integer qtd = Integer.parseInt((String)param.get("qtd"));
        
        List<Shop> shops = plugin.db.getAuctionsLimitbyPlayer(WebPortal.AuthPlayers.get(ip).WebSitePlayer.getName(),from,qtd,plugin.Sell);

        JSONObject json;
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < shops.size(); i++) {
            Shop shop = shops.get(i);          
            json = new JSONObject();
            
            json.put("1",JSON("Id",shop.getId()));
            json.put("2",JSON(message.WebItemName,ConvertItemToResult(shop.getItemStack())));
            json.put("3",JSON(message.WebPrice,shop.getPrice()));
            json.put("4",JSON(message.WebPriceEach,shop.getPrice() * shop.getItemStack().getAmount()));
            json.put("5",JSON(message.WebMarketPrice,Format(MarketPrice(shop, shop.getPrice())) + "%"));
            json.put("6",JSON(message.WebEnchant,GetEnchant(shop)));
            json.put("7",JSON(message.WebDurability,GetDurability(shop)));
            json.put("8",JSON(message.WebQuantity,shop.getItemStack().getAmount()));
            json.put("9",JSON(message.WebItemCategory,shop.getItemStack().GetSearchType()));
            
            jsonArray.add(json);
        }
        JSONObject jsonresult = new JSONObject();
        jsonresult.put(plugin.db.getFound(),jsonArray);
        
        Print(jsonresult.toJSONString(),"application/json");
    }
    
    public void Cancel(String url,Map param,String sessionId) {
        int id = Integer.parseInt((String)param.get("ID"));
        
        Shop auction = plugin.db.getAuction(id);
        
        String player = auction.getPlayerName();
        String cancelItemName = auction.getItemStack().getType().name();
        Short cancelItemDamage = auction.getItemStack().getDurability();
        
        if(!WebPortal.AuthPlayers.get(sessionId).WebSitePlayer.getName().equals(player)) {
            Print(message.WebIdNotFound,"text/plain");
        }

        List<Shop> auctions = plugin.db.getItem(player,cancelItemName,cancelItemDamage, true, plugin.Myitems);
        
        if(!auctions.isEmpty() && auction.getItemStack().getType() != Material.ENCHANTED_BOOK) {
            
            Integer newAmount = auction.getItemStack().getAmount() + auctions.get(0).getItemStack().getAmount();
            Integer itemId = auctions.get(0).getId();
            plugin.db.updateItemQuantity(newAmount,itemId);
            plugin.db.DeleteAuction(id);
            
            
        }else{
            plugin.db.updateTable(id, plugin.Myitems);
        }
        Print(message.WebCancelDone,"text/plain");
    }

}
