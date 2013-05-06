/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.request;

import java.net.Socket;
import java.util.List;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.settings.Auction;
import me.stutiguias.webportal.webserver.Html;
import me.stutiguias.webportal.webserver.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Daniel
 */
public class FillAuction extends Response {
    
    private WebPortal plugin;
    private Html html;
    
    public FillAuction(WebPortal plugin,Socket s) {
        super(plugin, s);
        this.plugin = plugin;
        html = new Html(plugin);
    }
    
    public void fillAuction(String ip,String url,String param)
    {
        if(url.contains("byall")) {
            getAuctionBy(ip, url, param,"nothing");
        }
        if(url.contains("byblock")) {
            getAuctionBy(ip, url, param,"Block");
        }
        if(url.contains("byfood")) {
            getAuctionBy(ip, url, param,"Food");
        }
        if(url.contains("bytools")) {
            getAuctionBy(ip, url, param,"Tools");
        }
        if(url.contains("bycombat")) {
            getAuctionBy(ip, url, param,"Combat");
        }
        if(url.contains("byredstone")) {
            getAuctionBy(ip, url, param,"Redstone");
        }
        if(url.contains("bydecoration")) {
            getAuctionBy(ip, url, param,"Decoration");
        }
        if(url.contains("bytransportation")) {
            getAuctionBy(ip, url, param,"Transportation");
        }
        if(url.contains("bymicellaneous")) {
            getAuctionBy(ip, url, param,"Micellaneous");
        }
        if(url.contains("bymaterials")) {
            getAuctionBy(ip, url, param,"Materials");
        }
        if(url.contains("bybrewing")) {
            getAuctionBy(ip, url, param,"Brewing");
        }
        if(url.contains("byothers")) {
            getAuctionBy(ip, url, param,"Others");
        }
    }
    
    public void getAuctionBy(String ip,String url,String param,String searchtype) {
        int iDisplayStart = Integer.parseInt(getParam("iDisplayStart", param));
        int iDisplayLength = Integer.parseInt(getParam("iDisplayLength", param));
        String search = getParam("sSearch", param);
        search = getConfigKey(search, searchtype);
        int sEcho = Integer.parseInt(getParam("sEcho", param));
        List<Auction> auctions;
        
        if(searchtype.equals("nothing")) {
            auctions = plugin.dataQueries.getAuctions(iDisplayStart,iDisplayLength);
        }else{
            auctions = plugin.dataQueries.getSearchAuctions(iDisplayStart,iDisplayLength,search,searchtype);
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
                    Data.add(ServerAuction(item,searchtype,ip));
                    continue;
                }
           
                tmp_Data = new JSONObject();
                double MakertPercent = MarketPrice(item, item.getPrice());
                tmp_Data.put("DT_RowId","row_" + item.getId() );
                tmp_Data.put("DT_RowClass", Grade(MakertPercent));
                tmp_Data.put("0", ConvertItemToResult(item,searchtype));
                tmp_Data.put("1", "<img width='32' style='max-width:32px' src='http://minotar.net/avatar/"+ item.getPlayerName() +"' /><br />"+ item.getPlayerName());
                tmp_Data.put("2", "Never");
                tmp_Data.put("3", item.getItemStack().getAmount());
                tmp_Data.put("4", "$ " + item.getPrice());
                tmp_Data.put("5", GetEnchant(item));
                tmp_Data.put("6", GetDurability(item));
                tmp_Data.put("7", format(MakertPercent) + "%");
                tmp_Data.put("8", html.HTMLBuy(ip,item.getId()));
                Data.add(tmp_Data);
            }
        }else{
           Data.add(NoAuction());
        }
        Response.put("aaData",Data);
        
        print(Response.toJSONString(),"text/plain");
    }
    
    public void getAuction(String param) {
        int to = 0;
        int from = 0;
        
        try {
            to = Integer.parseInt(getParam("to", param));
            from = Integer.parseInt(getParam("from", param));
        }catch(Exception ex) {
            print("Invalid Call", "text/plain");
        }
        
        if(from < to || from - to > 50 ) {
            print("Invalid Call", "text/plain");
            return;
        }
        
        List<Auction> auctions = plugin.dataQueries.getAuctions(to,from);
        JSONObject json = new JSONObject();
        int count = 0;
        for(Auction item:auctions){
            String[] itemConfig = getItemNameAndImg(item.getItemStack());
            
            JSONObject jsonNameImg = new JSONObject();
            jsonNameImg.put("0", ConvertItemToResult(item,itemConfig[2]));
            jsonNameImg.put("1", "<img width='32' style='max-width:32px' src='http://minotar.net/avatar/"+ item.getPlayerName() +"' /><br />"+ item.getPlayerName());
            jsonNameImg.put("2", "Never");
            jsonNameImg.put("3", item.getItemStack().getAmount());
            jsonNameImg.put("4", "$ " + item.getPrice());
            jsonNameImg.put("5", GetEnchant(item));
            jsonNameImg.put("6", GetDurability(item));
            
            json.put(count,jsonNameImg);
            count++;
        }
        print(json.toJSONString(), "text/plain");
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
        ServerAuction.put("4", "$ " + item.getPrice());
        ServerAuction.put("5", "Infinit");
        ServerAuction.put("6", "0%");
        ServerAuction.put("7", html.HTMLBuy(ip,item.getId()));
        return ServerAuction;
    }
}
