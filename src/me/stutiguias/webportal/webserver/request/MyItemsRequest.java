/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.request;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.init.json.JSONArray;
import me.stutiguias.webportal.init.json.JSONObject;
import me.stutiguias.webportal.model.Shop;
import me.stutiguias.webportal.model.WebItemStack;
import me.stutiguias.webportal.webserver.HttpResponse;
import org.bukkit.Material;

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
        Shop auction = plugin.db.getItemById(id,plugin.Myitems);
        if(auction.getQuantity() == qtd) {
            plugin.db.setPriceAndTable(id,price);
            Print(message.WebSucessCreateSale,"text/plain");
        }else{
            if(auction.getQuantity() > qtd)
            {
              plugin.db.UpdateItemAuctionQuantity(auction.getQuantity() - qtd, id);
              Short dmg = Short.valueOf(String.valueOf(auction.getDamage()));
              Material material = Material.getMaterial(auction.getName(),false);
              WebItemStack stack = new WebItemStack(material,auction.getQuantity(),dmg);  
              String type =  stack.getType().toString();
              String searchtype = stack.GetSearchType();
              plugin.db.CreateItem(auction.getName(),auction.getDamage(),auction.getPlayerName(),qtd,price,auction.getEnchantments(),plugin.Sell,type,searchtype);
              Print(message.WebSucessCreateSale,"text/plain");
            }else{
              Print(message.WebFailSellMore,"text/plain");
            }
        }
    }
        
    public void GetMyItems(String ip,String url,Map param) {
        
        Integer from = Integer.parseInt((String)param.get("from"));
        Integer qtd = Integer.parseInt((String)param.get("qtd"));

        List<Shop> shops = plugin.db.getAuctionsLimitbyPlayer(WebPortal.AuthPlayers.get(ip).WebSitePlayer.getName(),from,qtd,plugin.Myitems);
        
        if(CheckError(ip, shops)) return;

        JSONObject json;
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < shops.size(); i++) {
            Shop item = shops.get(i);          
            json = new JSONObject();
            
            double mprice = plugin.db.GetMarketPriceofItem(item.getItemStack().getType().name(),item.getItemStack().getDurability());
            
//            String metaCSV = plugin.db.GetItemInfo(item.getId(),"meta");
//            item.getItemStack().SetMetaItemNameForDisplay(metaCSV,true);
            try {
                json.put("1",JSON("Id",item.getId()));
                json.put("2",JSON(message.WebItemName,ConvertItemToResult(item.getItemStack())));
                json.put("3",JSON(message.WebQuantity,item.getItemStack().getAmount()));
                json.put("4",JSON(message.WebMarketPriceE,mprice));
                json.put("5",JSON(message.WebMarketPriceT,mprice * item.getItemStack().getAmount()));
                json.put("6",JSON(message.WebEnchant,GetEnchant(item)));
                json.put("7",JSON(message.WebDurability,GetDurability(item)));  
            }catch(Exception ex){
                ex.printStackTrace();
                return;
            }

            
            jsonArray.add(json);
        }
        JSONObject jsonresult = new JSONObject();
        jsonresult.put(plugin.db.getFound(),jsonArray);
        
        Print(jsonresult.toJSONString(),"application/json");
    }
    
    public void GetMyItemsForSelectBox(String ip) {
        List<Shop> auctions = plugin.db.getPlayerItems(WebPortal.AuthPlayers.get(ip).WebSitePlayer.getName());
        JSONObject json = new JSONObject();
        for(Shop item:auctions){
            
            String metaCSV = plugin.db.GetItemInfo(item.getId(),"meta");
            item.getItemStack().SetMetaItemNameForDisplay(metaCSV,false);
            
            JSONObject jsonNameImg = new JSONObject();
            jsonNameImg.put(item.getItemStack().getName(),item.getItemStack().getImage());
            jsonNameImg.put("enchant",GetEnchant(item));
            
            json.put(item.getId(),jsonNameImg);
        }
        Print(json.toJSONString(), "text/plain");
    }
    
    public Boolean CheckError(String ip,List<Shop> auctions) {
        if(WebPortal.AuthPlayers.get(ip).WebSitePlayer.getName() == null) {
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
