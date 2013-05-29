/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.request.type;

import java.util.List;
import java.util.Map;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.settings.Auction;
import me.stutiguias.webportal.settings.AuctionPlayer;
import me.stutiguias.webportal.settings.TradeSystem;
import me.stutiguias.webportal.webserver.Html;
import me.stutiguias.webportal.webserver.HttpResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Daniel
 */
public class AuctionRequest extends HttpResponse {
    
    private WebPortal plugin;
    private Html html;
    TradeSystem tr;
    
    public AuctionRequest(WebPortal plugin) {
        super(plugin);
        this.plugin = plugin;
        html = new Html(plugin);
        tr = new TradeSystem(plugin);
    }
        

        
    public void RequestAuctionBy(String ip,String url,Map param)
    {
        if(url.contains("byall")) {
            GetAuctionBy(ip, url, param,"nothing");
        }
        if(url.contains("byblock")) {
            GetAuctionBy(ip, url, param,"Block");
        }
        if(url.contains("byfood")) {
            GetAuctionBy(ip, url, param,"Food");
        }
        if(url.contains("bytools")) {
            GetAuctionBy(ip, url, param,"Tools");
        }
        if(url.contains("bycombat")) {
            GetAuctionBy(ip, url, param,"Combat");
        }
        if(url.contains("byredstone")) {
            GetAuctionBy(ip, url, param,"Redstone");
        }
        if(url.contains("bydecoration")) {
            GetAuctionBy(ip, url, param,"Decoration");
        }
        if(url.contains("bytransportation")) {
            GetAuctionBy(ip, url, param,"Transportation");
        }
        if(url.contains("bymicellaneous")) {
            GetAuctionBy(ip, url, param,"Micellaneous");
        }
        if(url.contains("bymaterials")) {
            GetAuctionBy(ip, url, param,"Materials");
        }
        if(url.contains("bybrewing")) {
            GetAuctionBy(ip, url, param,"Brewing");
        }
        if(url.contains("byothers")) {
            GetAuctionBy(ip, url, param,"Others");
        }
    }
    
    public void GetAuctionBy(String sessionId,String url,Map param,String searchtype) {
        
        int iDisplayStart = Integer.parseInt((String)param.get("iDisplayStart"));
        int iDisplayLength = Integer.parseInt((String)param.get("iDisplayLength"));
        String search = (String)param.get("sSearch");
        int sEcho =  Integer.parseInt((String)param.get("sEcho"));
        
        search = GetConfigKey(search, searchtype);
        List<Auction> auctions;
 
        if(search == null) search = "%";
        
        if(searchtype.equals("nothing")) {
            auctions = plugin.dataQueries.getAuctions(iDisplayStart,iDisplayLength);
        }else{
            auctions = plugin.dataQueries.getSearchAuctions(iDisplayStart,iDisplayLength,searchtype);
        }
        
        int iTotalRecords = plugin.dataQueries.getFound();
        int iTotalDisplayRecords = plugin.dataQueries.getFound();
        
        JSONObject Response = new JSONObject();
        JSONArray Data = new JSONArray();
        JSONObject tmp_Data;
        
        Response.put("sEcho", sEcho);
        Response.put("iTotalRecords", iTotalRecords);
        Response.put("iTotalDisplayRecords", iTotalDisplayRecords);
        
        if(iTotalRecords > 0) {
            for(Auction item:auctions){
                if(item.getPlayerName().equalsIgnoreCase("Server")){
                    Data.add(ServerAuction(item,searchtype,sessionId));
                    continue;
                }
           
                tmp_Data = new JSONObject();
                double MakertPercent = MarketPrice(item, item.getPrice());
                tmp_Data.put("DT_RowId","row_" + item.getId() );
                tmp_Data.put("DT_RowClass", Grade(MakertPercent));
                tmp_Data.put("0", ConvertItemToResult(item,searchtype));
                tmp_Data.put("1", "<img width='32' style='max-width:32px' src='" + plugin.Avatarurl + item.getPlayerName() +"' /><br />"+ item.getPlayerName());
                tmp_Data.put("2", "Never");
                tmp_Data.put("3", item.getItemStack().getAmount());
                tmp_Data.put("4", item.getPrice());
                tmp_Data.put("5", GetEnchant(item));
                tmp_Data.put("6", GetDurability(item));
                tmp_Data.put("7", Format(MakertPercent) + "%");
                if(item.getTableId() == plugin.Auction)
                    tmp_Data.put("8", html.HTMLBuy(sessionId,item.getId()));
                else
                    tmp_Data.put("8", html.HTMLSell(sessionId,item.getId()));
                
                Data.add(tmp_Data);
            }
        }else{
           Data.add(NoAuction());
        }
        Response.put("aaData",Data);
        
        Print(Response.toJSONString(),"text/plain");
    }
    
    public void GetAuction(Map param) {
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
        
        List<Auction> auctions = plugin.dataQueries.getAuctions(to,from);
        JSONObject json = new JSONObject();
        int count = 0;
        for(Auction item:auctions){
            String seatchtype = GetSearchType(item.getItemStack());
            JSONObject jsonNameImg = new JSONObject();
            jsonNameImg.put("0", ConvertItemToResult(item,seatchtype));
            jsonNameImg.put("1", "<img width='32' style='max-width:32px' src='http://minotar.net/avatar/"+ item.getPlayerName() +"' /><br />"+ item.getPlayerName());
            jsonNameImg.put("2", "Never");
            jsonNameImg.put("3", item.getItemStack().getAmount());
            jsonNameImg.put("4", item.getPrice());
            jsonNameImg.put("5", GetEnchant(item));
            jsonNameImg.put("6", GetDurability(item));
            
            json.put(count,jsonNameImg);
            count++;
        }
        Print(json.toJSONString(), "text/plain");
    }
    
    public JSONObject NoAuction() {
            JSONObject jsonTwo = new JSONObject();
            jsonTwo.put("DT_RowId","row_0" );
            jsonTwo.put("DT_RowClass", "gradeU");
            jsonTwo.put("0", "");
            jsonTwo.put("1", "");
            jsonTwo.put("2", "");
            jsonTwo.put("3", "No Auction");
            jsonTwo.put("4", "");
            jsonTwo.put("5", "");
            jsonTwo.put("6", "");
            jsonTwo.put("7", "");
            jsonTwo.put("8", "");
            return jsonTwo;
    }
    

    
    public String Grade(double percent) {
        if(percent == 100) {
            return "gradeU";
        }
        if(percent > 100) {
            return "gradeX";
        }
        if(percent < 100) {
            return "gradeA";
        }
        return "gradeB";
    }
    
    public JSONObject ServerAuction(Auction item,String searchtype,String ip){
        JSONObject ServerAuction = new JSONObject();
        ServerAuction.put("DT_RowId","row_" + item.getId() );
        ServerAuction.put("DT_RowClass", "0");
        ServerAuction.put("0", ConvertItemToResult(item,searchtype));
        ServerAuction.put("1", item.getPlayerName());
        ServerAuction.put("2", "Never");
        if(item.getItemStack().getAmount() == 9999) {
            ServerAuction.put("3", "Infinit");
        }else{
            ServerAuction.put("3", item.getItemStack().getAmount());
        }
        ServerAuction.put("4", item.getPrice());
        ServerAuction.put("5", GetEnchant(item));
        ServerAuction.put("6", GetDurability(item));
        ServerAuction.put("7", "");
        ServerAuction.put("8", html.HTMLBuy(ip,item.getId()));
        return ServerAuction;
    }
    
    public void Buy(String ip,Map param) {
       try { 
           int qtd =  Integer.parseInt((String)param.get("Quantity"));
           int id =  Integer.parseInt((String)param.get("ID"));
           
           AuctionPlayer ap = WebPortal.AuthPlayers.get(ip).AuctionPlayer;
           Auction au = plugin.dataQueries.getAuction(id);
           String item_name = GetItemConfig(au.getItemStack())[0];
           if(qtd <= 0)
           {
              Print("Quantity greater then 0","text/plain");
           } else if(qtd > au.getItemStack().getAmount())
           {
              Print("You are attempting to purchase more than the maximum available","text/plain");
           } else if(!plugin.economy.has(ap.getName(),au.getPrice() * qtd))
           {
              Print("You do not have enough money.","text/plain");
           } else if(ap.getName().equals(au.getPlayerName())) {
              Print("You cannnot buy your own items.","text/plain");
           } else {
               tr = new TradeSystem(plugin);
               Print(tr.Buy(ap.getName(),au, qtd, item_name, false),"text/plain");
           }
       }catch(Exception ex){
           WebPortal.logger.warning(ex.getMessage());
       }
        
    }
    
    public void Sell(String sessionId,Map param) {
       try { 
           int qtd =  Integer.parseInt((String)param.get("Quantity"));
           int id =  Integer.parseInt((String)param.get("ID"));
           
           AuctionPlayer ap = WebPortal.AuthPlayers.get(sessionId).AuctionPlayer;
           Auction au = plugin.dataQueries.getAuction(id);
           String item_name = GetItemConfig(au.getItemStack())[0];
           if(qtd <= 0)
           {
              Print("Quantity greater then 0","text/plain");
           } else if(qtd > au.getItemStack().getAmount())
           {
              Print("You are attempting to purchase more than the maximum available","text/plain");
           } else if(!plugin.economy.has(au.getPlayerName(),au.getPrice() * qtd))
           {
              Print("The Owner don't have money to buy this.","text/plain");
           } else if(ap.getName().equals(au.getPlayerName())) {
              Print("You cannnot sell your own items.","text/plain");
           } else {
               tr = new TradeSystem(plugin);
               Print(tr.Sell(ap.getName(),au, qtd, item_name, false),"text/plain");
           }
       }catch(Exception ex){
           WebPortal.logger.warning(ex.getMessage());
       }
        
    }
}
