/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.request.type;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
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
public class MyItemsRequest extends HttpResponse {
    
    private WebPortal plugin;
    
    public MyItemsRequest(WebPortal plugin) {
        super(plugin);
        this.plugin = plugin;
    }
        
    public void CreateSell(String ip,String url,Map param) {
        int qtd;
        Double price;
        int id;
        try {
            price = Double.parseDouble((String)param.get("Price"));
            id = Integer.parseInt((String)param.get("ID"));
            qtd = Integer.parseInt((String)param.get("Quantity"));
            if(qtd < 0) {
                Print(message.WebInvalidNumber,"text/plain");
                return;
            }
        }catch(NumberFormatException ex) {
            Print(message.WebInvalidNumber,"text/plain");
            return;
        }
        Shop auction = plugin.dataQueries.getItemById(id,plugin.Myitems);
        if(auction.getQuantity() == qtd) {
            plugin.dataQueries.setPriceAndTable(id,price);
            Print(message.WebSucessCreateSale,"text/plain");
        }else{
            if(auction.getQuantity() > qtd)
            {
              plugin.dataQueries.UpdateItemAuctionQuantity(auction.getQuantity() - qtd, id);
              Short dmg = Short.valueOf(String.valueOf(auction.getDamage()));
              ItemStack stack = new ItemStack(auction.getName(),auction.getQuantity(),dmg);  
              String type =  stack.getType().toString();
              String searchtype = GetSearchType(stack);
              plugin.dataQueries.createItem(auction.getName(),auction.getDamage(),auction.getPlayerName(),qtd,price,auction.getEnchantments(),plugin.Sell,type,searchtype);
              Print(message.WebSucessCreateSale,"text/plain");
            }else{
              Print(message.WebFailSellMore,"text/plain");
            }
        }
    }
        
    public void GetMyItems(String ip,String url,Map param) {
        
        Integer from = Integer.parseInt((String)param.get("from"));
        Integer qtd = Integer.parseInt((String)param.get("qtd"));

        List<Shop> shops = plugin.dataQueries.getAuctionsLimitbyPlayer(WebPortal.AuthPlayers.get(ip).AuctionPlayer.getName(),from,qtd,plugin.Myitems);
        
        if(CheckError(ip, shops)) return;

        JSONObject json;
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < shops.size(); i++) {
            Shop shop = shops.get(i);          
            json = new JSONObject();
            
            double mprice = plugin.dataQueries.GetMarketPriceofItem(shop.getItemStack().getTypeId(),shop.getItemStack().getDurability());

            json.put("1",JSON("Id",shop.getId()));
            json.put("2",JSON(message.WebItemName,ConvertItemToResult(shop,shop.getType())));
            json.put("3",JSON(message.WebQuantity,shop.getItemStack().getAmount()));
            json.put("4",JSON(message.WebMarketPriceE,mprice));
            json.put("5",JSON(message.WebMarketPriceT,mprice * shop.getItemStack().getAmount()));
            json.put("6",JSON(message.WebEnchant,GetEnchant(shop)));
            json.put("7",JSON(message.WebDurability,GetDurability(shop)));
            
            jsonArray.add(json);
        }
        JSONObject jsonresult = new JSONObject();
        jsonresult.put(plugin.dataQueries.getFound(),jsonArray);
        
        Print(jsonresult.toJSONString(),"application/json");
    }
    
    public void GetMyItems(String ip) {
        List<Shop> auctions = plugin.dataQueries.getPlayerItems(WebPortal.AuthPlayers.get(ip).AuctionPlayer.getName());
        JSONObject json = new JSONObject();
        for(Shop item:auctions){
            String[] itemConfig = GetItemConfig(item.getItemStack());
            
            if(plugin.AllowMetaItem) {
                itemConfig[0] = ChangeItemToItemMeta(item, itemConfig[0]);
            }
            
            JSONObject jsonNameImg = new JSONObject();
            jsonNameImg.put(itemConfig[0],itemConfig[1]);
            jsonNameImg.put("enchant",GetEnchant(item));
            
            json.put(item.getId(),jsonNameImg);
        }
        Print(json.toJSONString(), "text/plain");
    }
    
    public Boolean CheckError(String ip,List<Shop> auctions) {
        if(WebPortal.AuthPlayers.get(ip).AuctionPlayer.getName() == null) {
            WebPortal.logger.log(Level.WARNING,"Cant determine player name");
            return true;
        }
        if(auctions == null) {
            WebPortal.logger.log(Level.WARNING,"Cant get shop sales/buys");
            return true;
        }
        return false;
    }
}
