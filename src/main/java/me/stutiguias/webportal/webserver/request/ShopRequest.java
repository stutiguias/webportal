/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.request;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.init.json.JSONArray;
import me.stutiguias.webportal.init.json.JSONObject;
import me.stutiguias.webportal.model.Shop;
import me.stutiguias.webportal.trade.TradeHandle;
import me.stutiguias.webportal.model.WebSitePlayer;
import me.stutiguias.webportal.webserver.HttpResponse;
import org.bukkit.OfflinePlayer;

/**
 *
 * @author Daniel
 */
@SuppressWarnings("unchecked")
public class ShopRequest extends HttpResponse {
    
    TradeHandle tr;

    public ShopRequest(WebPortal plugin) {
        super(plugin);
        tr = new TradeHandle(plugin);
    }
        

        
    public void RequestShopBy(String ip,String url,Map param)
    {
        if(url.contains("byall")) {
            GetShopBy(ip, url, param,"nothing");
        }
        if(url.contains("byblock")) {
            GetShopBy(ip, url, param,"Block");
        }
        if(url.contains("byfood")) {
            GetShopBy(ip, url, param,"Food");
        }
        if(url.contains("bytools")) {
            GetShopBy(ip, url, param,"Tools");
        }
        if(url.contains("bycombat")) {
            GetShopBy(ip, url, param,"Combat");
        }
        if(url.contains("byredstone")) {
            GetShopBy(ip, url, param,"Redstone");
        }
        if(url.contains("bydecoration")) {
            GetShopBy(ip, url, param,"Decoration");
        }
        if(url.contains("bytransportation")) {
            GetShopBy(ip, url, param,"Transportation");
        }
        if(url.contains("bymicellaneous")) {
            GetShopBy(ip, url, param,"Micellaneous");
        }
        if(url.contains("bymaterials")) {
            GetShopBy(ip, url, param,"Materials");
        }
        if(url.contains("bybrewing")) {
            GetShopBy(ip, url, param,"Brewing");
        }
        if(url.contains("byothers")) {
            GetShopBy(ip, url, param,"Others");
        }
    }
    
    public void GetShopBy(String sessionId,String url,Map param,String searchtype) {
        
        Integer qtd = Integer.parseInt((String)param.get("qtd"));
        Integer from = Integer.parseInt((String)param.get("from"));

        List<Shop> shops;
        
        if(searchtype.equals("nothing")) {
            shops = plugin.db.getAuctions(from,qtd);
        }else{
            shops = plugin.db.getSearchAuctions(from,qtd,searchtype);
        }

        JSONObject json;
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < shops.size(); i++) {
            Shop shop = shops.get(i);          
            json = new JSONObject();
                            
            if(shop.getPlayerName().equalsIgnoreCase("Server")){
                    jsonArray.add(ServerShop(shop,searchtype,sessionId));
                    continue;
            }
            
            double MakertPercent = MarketPrice(shop, shop.getPrice());
            json.put("1",JSON("Id",shop.getId()));
            json.put("2",JSON(message.WebType,GetType(shop)));
            json.put("3",JSON(message.WebItemName,ConvertItemToResult(shop.getItemStack())));
            json.put("4",JSON(message.WebOwner,"<img width='32' style='max-width:32px' src='" + plugin.Avatarurl + shop.getPlayerName() +"' /><br />"+ shop.getPlayerName()));
            json.put("5",JSON(message.WebExpire, message.WebNever));
            json.put("6",JSON(message.WebQuantity,shop.getItemStack().getAmount()));
            json.put("7",JSON(message.WebPriceEach,shop.getPrice()));
            json.put("8",JSON(message.WebEnchant,GetEnchant(shop)));
            json.put("9",JSON(message.WebDurability,GetDurability(shop)));
            json.put("10",JSON(message.WebMarketPrice,Format(MakertPercent) + "%"));
            jsonArray.add(json);
        }
        JSONObject jsonresult = new JSONObject();
        jsonresult.put(plugin.db.getFound(),jsonArray);
        
        Print(jsonresult.toJSONString(),"application/json");
    }
    
    public void GetShopWithoutLogin(Map param) {
        int from;
        int qtd;
        
        try {
            from = Integer.parseInt((String)param.get("from"));
            qtd = Integer.parseInt((String)param.get("qtd"));
        }catch(NumberFormatException ex) {
            Print("Invalid Call", "text/plain");
            return;
        }
       
        List<Shop> auctions = plugin.db.getAuctions(from,qtd);

        JSONArray jsonArray = new JSONArray();
        JSONObject json;
        
        for(Shop item:auctions){
            String searchtype = item.getItemStack().GetSearchType();
            
            json = new JSONObject();
            json.put("1",JSON(message.WebType,GetType(item)));
            json.put("2",JSON(message.WebItemName,ConvertItemToResult(item.getItemStack())));
            json.put("3",JSON(message.WebOwner,getName(item)));
            json.put("4",JSON(message.WebExpire, message.WebNever));
            if(item.getItemStack().getAmount() == 9999) {
                json.put("5",JSON(message.WebQuantity,message.WebInfinit));
            }else{
                json.put("5",JSON(message.WebQuantity,item.getItemStack().getAmount()));
            }
            json.put("6",JSON(message.WebPriceEach,item.getPrice()));
            json.put("7",JSON(message.WebEnchant,GetEnchant(item)));
            json.put("8",JSON(message.WebDurability,GetDurability(item)));
            jsonArray.add(json);
        }
        JSONObject jsonresult = new JSONObject();
        jsonresult.put(plugin.db.getFound(),jsonArray);
        
        Print(jsonresult.toJSONString(),"application/json");
    }

    public JSONObject ServerShop(Shop item,String searchtype,String ip){

        JSONObject json = new JSONObject();
        json.put("1",JSON("Id",item.getId()));
        json.put("2",JSON(message.WebType,GetType(item) ));
        json.put("3",JSON(message.WebItemName,ConvertItemToResult(item.getItemStack())));
        json.put("4",JSON(message.WebOwner,item.getPlayerName()));
        json.put("5",JSON(message.WebExpire, message.WebNever));
        
        if(item.getItemStack().getAmount() == 9999) {
            json.put("6",JSON(message.WebQuantity,message.WebInfinit));
        }else{
            json.put("6",JSON(message.WebQuantity,item.getItemStack().getAmount()));
        }
        
        json.put("7",JSON(message.WebPriceEach,item.getPrice()));
        json.put("8",JSON(message.WebEnchant,GetEnchant(item)));
        json.put("9",JSON(message.WebDurability,GetDurability(item)));
        json.put("10",JSON(message.WebMarketPrice,""));
        return json;
    }
    
    public void BuySellShop(String ip,Map param) {
        int id =  Integer.parseInt((String)param.get("ID"));
        int qtd =  Integer.parseInt((String)param.get("quantity"));

        Shop shop = plugin.db.getAuction(id);
        
        if(shop.getTableId() == plugin.Sell) {
            boolean buy = LoggedPlayerBuyer(ip, qtd, shop);
        } else
            LoggedPlayerSeller(ip, qtd, shop);
    }

    private boolean LoggedPlayerBuyer(String ip,int qtd, Shop shop) {
       try { 
           WebSitePlayer ap = WebPortal.AuthPlayers.get(ip).WebSitePlayer;
           OfflinePlayer player = plugin.getServer().getOfflinePlayer(UUID.fromString(ap.getUUID()));

           if(ap.getCanBuy() != 1)                                  return PrintWithReturn(message.WebCantBuy, "text/plain");
           if(qtd <= 0)                                             return PrintWithReturn(message.WebFailQtdGreaterThen,"text/plain");
           if(qtd > shop.getItemStack().getAmount())                return PrintWithReturn(message.WebFailPurchaseMoreThen,"text/plain");
           if(!plugin.economy.has(player,shop.getPrice() * qtd)) return PrintWithReturn(message.WebFailBuyMoney,"text/plain");
           if(ap.getName().equals(shop.getPlayerName()))            return PrintWithReturn(message.WebFailBuyYours,"text/plain");

           return PrintWithReturn(plugin.Buy(ap.getName(),shop, qtd),"text/plain");
       }catch(Exception ex){
           WebPortal.logger.warning(ex.getMessage());
           Error(ex.getMessage());
           return false;
       }
        
    }

    private void LoggedPlayerSeller(String sessionId,int qtd, Shop shop) {
       try { 
           WebSitePlayer ap = plugin.db.getPlayer(shop.getPlayerName());
           OfflinePlayer ownerItemPlayer = plugin.getServer().getOfflinePlayer(UUID.fromString(ap.getUUID()));

           if(ap.getCanBuy() != 1) {
               Print(message.WebCantSell,"text/plain");
           }
           
           if(qtd <= 0)
           {
              Print(message.WebFailQtdGreaterThen,"text/plain");
           } else if(qtd > shop.getItemStack().getAmount())
           {
              Print(message.WebFailSaleMoreThen,"text/plain");
           } else if(!plugin.economy.has(ownerItemPlayer,shop.getPrice() * qtd))
           {
              Print(message.WebFailSaleMoney,"text/plain");
           } else if(ap.getName().equals(shop.getPlayerName())) {
              Print(message.WebFailSellYours,"text/plain");
           } else {
               Print(plugin.Sell(ap.getName(),shop, qtd),"text/plain");
           }
       }catch(NumberFormatException ex){
           WebPortal.logger.warning(ex.getMessage());
       }
    }
    
    private String GetType(Shop item) {
        if(item.getTableId() == plugin.Sell)
            return message.WebBuy;
        else
            return message.WebSell;
    }
    
    public String getName(Shop item) {
      if(!item.getPlayerName().contains("Server"))
          return "<img width='32' style='max-width:32px' src='"+ plugin.Avatarurl + item.getPlayerName() +"' /><br />"+ item.getPlayerName();
      else
          return item.getPlayerName();
    }
}
