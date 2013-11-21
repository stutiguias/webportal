/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.request.type;

import java.util.List;
import java.util.Map;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.settings.Shop;
import me.stutiguias.webportal.webserver.Html;
import me.stutiguias.webportal.webserver.HttpResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Daniel
 */
public class SellRequest extends HttpResponse {
    
    private WebPortal plugin;
    private Html html;
    
    public SellRequest(WebPortal plugin) {
        super(plugin);
        this.plugin = plugin;
        html = new Html(plugin);
    }
    
    public void GetSell(String ip,String url,Map param) {
        
        Integer from = Integer.parseInt((String)param.get("from"));
        Integer qtd = Integer.parseInt((String)param.get("qtd"));
        
        List<Shop> shops = plugin.dataQueries.getAuctionsLimitbyPlayer(WebPortal.AuthPlayers.get(ip).AuctionPlayer.getName(),from,qtd,plugin.Auction);

        JSONObject json;
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < shops.size(); i++) {
            Shop shop = shops.get(i);          
            json = new JSONObject();
            
            json.put("1",JSON("Id",shop.getId()));
            json.put("2",JSON(message.WebItemName,ConvertItemToResult(shop,shop.getType())));
            json.put("3",JSON(message.WebPrice,shop.getPrice()));
            json.put("4",JSON("Price Each",shop.getPrice() * shop.getItemStack().getAmount()));
            json.put("5",JSON("Market Price",Format(MarketPrice(shop, shop.getPrice())) + "%"));
            json.put("6",JSON("Enchant",GetEnchant(shop)));
            json.put("7",JSON("Durability",GetDurability(shop)));
            json.put("8",JSON(message.WebQuantity,shop.getItemStack().getAmount()));
            json.put("9",JSON(message.WebItemCategory,GetSearchType(shop.getItemStack())));
            
            jsonArray.add(json);
        }
        JSONObject jsonresult = new JSONObject();
        jsonresult.put(plugin.dataQueries.getFound(),jsonArray);
        
        Print(jsonresult.toJSONString(),"application/json");
    }
    
    public void Cancel(String url,Map param) {
        int id = Integer.parseInt((String)param.get("ID"));
        
        Shop auction = plugin.dataQueries.getAuction(id);
        
        String player = auction.getPlayerName();
        Integer cancelItemId = auction.getItemStack().getTypeId();
        Short cancelItemDamage = auction.getItemStack().getDurability();
        
        List<Shop> auctions = plugin.dataQueries.getItem(player,cancelItemId,cancelItemDamage, true, plugin.Myitems);
        
        if(!auctions.isEmpty() && cancelItemId != 403) {
            
            Integer newAmount = auction.getItemStack().getAmount() + auctions.get(0).getItemStack().getAmount();
            Integer itemId = auctions.get(0).getId();
            plugin.dataQueries.updateItemQuantity(newAmount,itemId);
            plugin.dataQueries.DeleteAuction(id);
            
            
        }else{
            plugin.dataQueries.updateTable(id, plugin.Myitems);
        }
        Print(message.WebCancelDone,"text/plain");
    }

}
