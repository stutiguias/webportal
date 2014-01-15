/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.request;

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

    WebSitePlayer _authPlayer;
    List<Shop> _playerItems;
    List<WebSiteMail> _playerMail;
    List<Shop> _PlayerAuction;
    
    public AdminRequest(WebPortal plugin) {
        super(plugin);
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
            Print(message.WebNotAdmin,"text/html");
        }
    }

    public void WebBan(String SessionId,Map param) {
        if(!isAdmin(SessionId)) {
            Print(message.WebNotAdmin,"text/html");
            return;
        }
        String setplayer = (String)param.get("ID");
        boolean success = plugin.dataQueries.WebSiteBan(setplayer,"Y");
        if(success) {
            Print(message.WebPlayerBanned,"text/plain");
        }else{
            Print(message.WebPlayerNotBanned,"text/plain");
        }
    }
        
    public void WebUnBan(String SessionId,Map param) {
        if(!isAdmin(SessionId)) {
            Print(message.WebNotAdmin,"text/html");
            return;
        }
        String setplayer = (String)param.get("ID");
        boolean success = plugin.dataQueries.WebSiteBan(setplayer,"N");
        if(success) {
            Print(message.WebPlayerDesBanned,"text/plain");
        }else{
            Print(message.WebPlayerNotDesBanned,"text/plain");
        }
    }
    
    private void playerinfo(String SessionId,String partialName) {
        if(!isAdmin(SessionId)) {
            Print(message.WebNotAdmin,"text/html");
            return;
        }
        List<WebSitePlayer> players = plugin.dataQueries.FindAllPlayersWith(partialName);
        if(players == null) {
            Print(message.WebPlayerNotFound,"text/html");
        }else{
            JSONArray jsonarray = new JSONArray();
            JSONObject json;
            for (int i = 0; i < players.size(); i++) {
                json = new JSONObject();
                json.put("Ip",players.get(i).getIp());
                json.put(message.WebName,players.get(i).getName());
                json.put(message.WebCanBuy,players.get(i).getCanBuy());
                json.put(message.WebCanSell,players.get(i).getCanSell());
                json.put(message.WebisAdmin,players.get(i).getIsAdmin());
                json.put(message.WebBanned,players.get(i).getWebban());
                json.put(message.WebWebSiteBan, new Html(plugin).HTMLBan(SessionId,players.get(i).getId()) );
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
            jsonObjectArray.put(message.WebBuyer,transact.getBuyer());
            jsonObjectArray.put(message.WebItemName,itemname);
            jsonObjectArray.put(message.WebPrice,transact.getPrice());
            jsonObjectArray.put(message.WebQuantity,transact.getQuantity());
            jsonObjectArray.put(message.WebSeller, transact.getSeller());
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
            jsonObjectArray.put(message.WebName,_Auction.getPlayerName());
            jsonObjectArray.put(message.WebItemName,itemname);
            jsonObjectArray.put(message.WebQuantity,_Auction.getItemStack().getAmount());
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
            jsonObjectArray.put(message.WebName,auctionMail.getPlayerName());
            jsonObjectArray.put(message.WebItemName,itemname );
            jsonObjectArray.put(message.WebQuantity, auctionMail.getItemStack().getAmount());
            jsonarray.add(jsonObjectArray);
        }

        Print(jsonarray.toJSONString(),"application/json");
    }
        
    private void playerauction(String name) {
        _PlayerAuction = plugin.dataQueries.getAuctionsLimitbyPlayer(name,0,2000,plugin.Sell);
        JSONArray jsonarray = new JSONArray();
        JSONObject jsonObjectArray;

        for (int i = 0; i < _PlayerAuction.size(); i++) {
            Shop auction = _PlayerAuction.get(i);
            String itemname = GetItemConfig(auction.getItemStack())[0];
            jsonObjectArray = new JSONObject();
            jsonObjectArray.put(message.WebName,auction.getPlayerName() );
            jsonObjectArray.put(message.WebItemName, itemname );
            jsonObjectArray.put(message.WebQuantity, auction.getItemStack().getAmount());
            jsonarray.add(jsonObjectArray);
        }
        
        Print(jsonarray.toJSONString(),"application/json");
    }
}
