/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.request.type;

import java.util.List;
import java.util.Map;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.settings.*;
import me.stutiguias.webportal.webserver.Html;
import me.stutiguias.webportal.webserver.HttpResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Daniel
 */
public class AdminRequest extends HttpResponse {
    
    WebPortal plugin;
    WebSitePlayer _authPlayer;
    List<Shop> _playerItems;
    List<WebSiteMail> _playerMail;
    List<Shop> _PlayerAuction;
    
    public AdminRequest(WebPortal plugin) {
        super(plugin);
        this.plugin = plugin;
    }
    
    public void AdmGetInfo(String Hostadress,Map param) {
        if(isAdmin(Hostadress)) {
            String name = (String)param.get("nick");
            String info = (String)param.get("information");
            if(info.equalsIgnoreCase("playerinfo")) playerinfo(Hostadress,name);
            if(info.equalsIgnoreCase("playeritems")) playeritems(name);
            if(info.equalsIgnoreCase("playermail")) playermails(name);
            if(info.equalsIgnoreCase("playerauctions")) playerauction(name);
            if(info.equalsIgnoreCase("playertransaction")) playertransaction(name);
        }else{
            Print("Your r not admin","text/html");
        }
    }

    public void WebBan(String SessionId,Map param) {
        if(!isAdmin(SessionId)) {
            Print("Your r not admin","text/html");
            return;
        }
        String setplayer = (String)param.get("ID");
        boolean success = plugin.dataQueries.WebSiteBan(setplayer,"Y");
        if(success) {
            Print("Player Banned","text/plain");
        }else{
            Print("Player NOT Banned","text/plain");
        }
    }
        
    public void WebUnBan(String SessionId,Map param) {
        if(!isAdmin(SessionId)) {
            Print("Your r not admin","text/html");
            return;
        }
        String setplayer = (String)param.get("ID");
        boolean success = plugin.dataQueries.WebSiteBan(setplayer,"N");
        if(success) {
            Print("Player DesBanned","text/plain");
        }else{
            Print("Player NOT DesBanned","text/plain");
        }
    }
    
    private void playerinfo(String SessionId,String partialName) {
        if(!isAdmin(SessionId)) {
            Print("Your r not admin","text/html");
            return;
        }
        List<WebSitePlayer> players = plugin.dataQueries.FindAllPlayersWith(partialName);
        if(players == null) {
            Print("Player Not Found","text/html");
        }else{
            JSONArray jsonarray = new JSONArray();
            JSONObject json;
            for (int i = 0; i < players.size(); i++) {
                json = new JSONObject();
                json.put("IP",players.get(i).getIp());
                json.put("Name",players.get(i).getName());
                json.put("Can Buy ?",players.get(i).getCanBuy());
                json.put("Can Sell ?",players.get(i).getCanSell());
                json.put("is Admin ?",players.get(i).getIsAdmin());
                json.put("Banned ?",players.get(i).getWebban());
                json.put("WebSite Ban", new Html(plugin).HTMLBan(SessionId,players.get(i).getId()) );
                jsonarray.add(json);
            }
            Print(jsonarray.toJSONString(),"application/json");
        }
    }
    
    private void playertransaction(String name) {
        List<Transact> Transacts = plugin.dataQueries.GetTransactOfPlayer(name);
        JSONArray jsonarray = new JSONArray();
        JSONObject jsonObjectArray;
        for (int i = 0; i < Transacts.size(); i++) {

            Transact transact = Transacts.get(i);
            String itemname = GetItemConfig(transact.getItemStack())[0];
   
            jsonObjectArray = new JSONObject();
            jsonObjectArray.put("Buyer",transact.getBuyer());
            jsonObjectArray.put("Item Name",itemname);
            jsonObjectArray.put("Price",transact.getPrice());
            jsonObjectArray.put("Quantity",transact.getQuantity());
            jsonObjectArray.put("Seller", transact.getSeller());
            jsonarray.add(jsonObjectArray);
            
        }
        Print(jsonarray.toJSONString(),"application/json");
    }
    
    private void playeritems(String name) {
        _playerItems = plugin.dataQueries.getAuctionsLimitbyPlayer(name,0,2000,plugin.Myitems);

        JSONArray jsonarray = new JSONArray();
        JSONObject jsonObjectArray;
        for (int i = 0; i < _playerItems.size(); i++) {
            Shop _Auction = _playerItems.get(i);
            String itemname = GetItemConfig(_Auction.getItemStack())[0];
            jsonObjectArray = new JSONObject();
            jsonObjectArray.put("Nick",_Auction.getPlayerName());
            jsonObjectArray.put("Item Name",itemname);
            jsonObjectArray.put("Quantity",_Auction.getItemStack().getAmount());
            jsonarray.add(jsonObjectArray);
        }
        Print(jsonarray.toJSONString(),"application/json");
    }
    
    private void playermails(String name) {
        _playerMail = plugin.dataQueries.getMail(name);
        
        JSONArray jsonarray = new JSONArray();
        JSONObject jsonObjectArray;
  
        for (int i = 0; i < _playerMail.size(); i++) {
            WebSiteMail auctionMail = _playerMail.get(i);
            String itemname = GetItemConfig(auctionMail.getItemStack())[0];
            jsonObjectArray = new JSONObject();
            jsonObjectArray.put("Nick",auctionMail.getPlayerName());
            jsonObjectArray.put("Item Name",itemname );
            jsonObjectArray.put("Quantity", auctionMail.getItemStack().getAmount());
            jsonarray.add(jsonObjectArray);
        }

        Print(jsonarray.toJSONString(),"application/json");
    }
        
    private void playerauction(String name) {
        _PlayerAuction = plugin.dataQueries.getAuctionsLimitbyPlayer(name,0,2000,plugin.Auction);
        JSONArray jsonarray = new JSONArray();
        JSONObject jsonObjectArray;

        for (int i = 0; i < _PlayerAuction.size(); i++) {
            Shop auction = _PlayerAuction.get(i);
            String itemname = GetItemConfig(auction.getItemStack())[0];
            jsonObjectArray = new JSONObject();
            jsonObjectArray.put("Nick",auction.getPlayerName() );
            jsonObjectArray.put("Item Name", itemname );
            jsonObjectArray.put("Quantity", auction.getItemStack().getAmount());
            jsonarray.add(jsonObjectArray);
        }
        
        Print(jsonarray.toJSONString(),"application/json");
    }
}
