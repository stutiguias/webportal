/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.request.type;

import java.util.List;
import java.util.Map;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.settings.*;
import me.stutiguias.webportal.webserver.HttpResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Daniel
 */
public class AdminRequest extends HttpResponse {
    
    WebPortal plugin;
    AuctionPlayer _authPlayer;
    List<Auction> _playerItems;
    List<AuctionMail> _playerMail;
    List<Auction> _PlayerAuction;
    
    public AdminRequest(WebPortal plugin) {
        super(plugin);
        this.plugin = plugin;
    }
    
    public void ADM(String Hostadress,Map param) {
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
    
    private void playerinfo(String Hostadress,String name) {
        _authPlayer = plugin.dataQueries.getPlayer(name);
        if(_authPlayer == null) {
            Print("Player Not Found","text/html");
        }else{
            JSONArray jsonarray = new JSONArray();
            JSONObject json = new JSONObject();
            json.put("IP",_authPlayer.getIp());
            json.put("Name",_authPlayer.getName());
            json.put("Can Buy ?",_authPlayer.getCanBuy());
            json.put("Can Sell ?",_authPlayer.getCanSell());
            json.put("is Admin ?",_authPlayer.getIsAdmin());
            json.put("Banned ?","");
            json.put("WebSite Ban", HTMLBan(Hostadress,_authPlayer.getId()) );
            jsonarray.add(json);
            Print(jsonarray.toJSONString(),"application/json");
        }
    }
    
    private String HTMLBan(String ip,int id) {
      if(WebPortal.AuthPlayers.get(ip).AuctionPlayer.getIsAdmin() == 1) {
        return "<form action='ban/player' onsubmit='return ban(this)'>"+
                "<input type='hidden' name='ID' value='"+id+"' />"+
                "<input type='submit' value='Ban' class='button' /></form><span id='"+id+"'></span>";
      }else{
        return "Can't Ban";
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
            Auction _Auction = _playerItems.get(i);
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
            AuctionMail auctionMail = _playerMail.get(i);
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
            Auction auction = _PlayerAuction.get(i);
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
