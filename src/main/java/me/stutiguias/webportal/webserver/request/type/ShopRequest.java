/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.request.type;

import java.util.List;
import java.util.Map;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.settings.Shop;
import me.stutiguias.webportal.settings.WebSitePlayer;
import me.stutiguias.webportal.settings.TradeSystem;
import me.stutiguias.webportal.webserver.Html;
import me.stutiguias.webportal.webserver.HttpResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Daniel
 */
public class ShopRequest extends HttpResponse {
    
    private WebPortal plugin;
    private Html html;
    TradeSystem tr;

    public ShopRequest(WebPortal plugin) {
        super(plugin);
        this.plugin = plugin;
        html = new Html(plugin);
        tr = new TradeSystem(plugin);
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
        
        // TODO : Implement search
        //search = GetConfigKey(search, searchtype);
        List<Shop> shops;
 
        //if(search == null) search = "%";
        
        if(searchtype.equals("nothing")) {
            shops = plugin.dataQueries.getAuctions(from,qtd);
        }else{
            shops = plugin.dataQueries.getSearchAuctions(from,qtd,searchtype);
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
            
            if(shop.getTableId() == plugin.Auction)
                json.put("2",JSON("Type","Buy"));
            else
                json.put("2",JSON("Type","Sell"));
            
            json.put("3",JSON(message.WebItemName,ConvertItemToResult(shop,searchtype)));
            json.put("4",JSON("Owner","<img width='32' style='max-width:32px' src='" + plugin.Avatarurl + shop.getPlayerName() +"' /><br />"+ shop.getPlayerName()));
            json.put("5",JSON("Expire", message.WebNever));
            json.put("6",JSON(message.WebQuantity,shop.getItemStack().getAmount()));
            json.put("7",JSON("Price Each",shop.getPrice()));
            json.put("8",JSON("Enchant",GetEnchant(shop)));
            json.put("9",JSON("Durability",GetDurability(shop)));
            json.put("10",JSON("Market Price",Format(MakertPercent) + "%"));
            jsonArray.add(json);
        }
        JSONObject jsonresult = new JSONObject();
        jsonresult.put(plugin.dataQueries.getFound(),jsonArray);
        
        Print(jsonresult.toJSONString(),"application/json");
    }
    
    public void GetShop(Map param) {
        int to;
        int from;
        
        try {
            to = Integer.parseInt((String)param.get("to"));
            from = Integer.parseInt((String)param.get("from"));
        }catch(Exception ex) {
            Print("Invalid Call", "text/plain");
            return;
        }
        
        if(from < to || from - to > 50 ) {
            Print("Invalid Call", "text/plain");
            return;
        }
        
        List<Shop> auctions = plugin.dataQueries.getAuctions(to,from);
        JSONObject json = new JSONObject();
        int count = 0;
        for(Shop item:auctions){
            String seatchtype = GetSearchType(item.getItemStack());
            JSONObject jsonNameImg = new JSONObject();
            jsonNameImg.put("0", ConvertItemToResult(item,seatchtype));
            jsonNameImg.put("1", "<img width='32' style='max-width:32px' src='http://minotar.net/avatar/"+ item.getPlayerName() +"' /><br />"+ item.getPlayerName());
            jsonNameImg.put("2", message.WebNever);
            jsonNameImg.put("3", item.getItemStack().getAmount());
            jsonNameImg.put("4", item.getPrice());
            jsonNameImg.put("5", GetEnchant(item));
            jsonNameImg.put("6", GetDurability(item));
            
            json.put(count,jsonNameImg);
            count++;
        }
        Print(json.toJSONString(), "text/plain");
    }

    public JSONObject ServerShop(Shop item,String searchtype,String ip){
        JSONObject json = new JSONObject();
        json.put("1",JSON("Id",item.getId()));
        if(item.getTableId() == plugin.Auction)
            json.put("2",JSON("Type","Buy"));
        else
            json.put("2",JSON("Type","Sell"));
        json.put("3",JSON(message.WebItemName,ConvertItemToResult(item,searchtype)));
        json.put("4",JSON("Owner",item.getPlayerName()));
        json.put("5",JSON("Expire", message.WebNever));
        if(item.getItemStack().getAmount() == 9999) {
            json.put("6",JSON(message.WebQuantity,"Infinit"));
        }else{
            json.put("6",JSON(message.WebQuantity,item.getItemStack().getAmount()));
        }
        json.put("7",JSON("Price Each",item.getPrice()));
        json.put("8",JSON("Enchant",GetEnchant(item)));
        json.put("9",JSON("Durability",GetDurability(item)));
        json.put("10",JSON("Market Price",""));
        return json;
    }
    
    public void BuySellShop(String ip,Map param) {
        int id =  Integer.parseInt((String)param.get("ID"));
        
        Shop shop = plugin.dataQueries.getAuction(id);
        
        if(shop.getTableId() == plugin.Auction)
            Buy(ip,param,shop);
        else
            Sell(ip,param,shop);
    }
    
    
    private void Buy(String ip,Map param,Shop shop) {
       try { 
           int qtd =  Integer.parseInt((String)param.get("quantity"));
           WebSitePlayer ap = WebPortal.AuthPlayers.get(ip).AuctionPlayer;
           String item_name = GetItemConfig(shop.getItemStack())[0];
           if(qtd <= 0)
           {
              Print(message.WebFailQtdGreaterThen,"text/plain");
           } else if(qtd > shop.getItemStack().getAmount())
           {
              Print(message.WebFailPurchaseMoreThen,"text/plain");
           } else if(!plugin.economy.has(ap.getName(),shop.getPrice() * qtd))
           {
              Print(message.WebFailBuyMoney,"text/plain");
           } else if(ap.getName().equals(shop.getPlayerName())) {
              Print(message.WebFailBuyYours,"text/plain");
           } else {
               tr = new TradeSystem(plugin);
               Print(tr.Buy(ap.getName(),shop, qtd, item_name, false),"text/plain");
           }
       }catch(Exception ex){
           WebPortal.logger.warning(ex.getMessage());
       }
        
    }
    
    private void Sell(String sessionId,Map param,Shop shop) {
       try { 
           int qtd =  Integer.parseInt((String)param.get("quantity"));
           
           WebSitePlayer ap = WebPortal.AuthPlayers.get(sessionId).AuctionPlayer;
           
           String item_name = GetItemConfig(shop.getItemStack())[0];
           if(qtd <= 0)
           {
              Print(message.WebFailQtdGreaterThen,"text/plain");
           } else if(qtd > shop.getItemStack().getAmount())
           {
              Print(message.WebFailSaleMoreThen,"text/plain");
           } else if(!plugin.economy.has(shop.getPlayerName(),shop.getPrice() * qtd))
           {
              Print(message.WebFailSaleMoney,"text/plain");
           } else if(ap.getName().equals(shop.getPlayerName())) {
              Print(message.WebFailSellYours,"text/plain");
           } else {
               tr = new TradeSystem(plugin);
               Print(tr.Sell(ap.getName(),shop, qtd, item_name, false),"text/plain");
           }
       }catch(Exception ex){
           WebPortal.logger.warning(ex.getMessage());
       }
        
    }
}
