/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.request;

import java.net.Socket;
import java.util.List;
import me.stutiguias.webportal.init.WebAuction;
import me.stutiguias.webportal.settings.*;
import me.stutiguias.webportal.webserver.Response;

/**
 *
 * @author Daniel
 */
public class FillAdmin extends Response {
    
    WebAuction plugin;
    AuctionPlayer _AuPlayer;
    List<AuctionItem> _PlayerItems;
    List<AuctionMail> _PlayerMail;
    List<Auction> _PlayerAuction;
    
    public FillAdmin(WebAuction plugin,Socket s) {
        super(plugin,s);
    }
    
    public void ADM(String Hostadress,String param) {
        String name = getParam("name", param);
        String info = getParam("information", param);
        if(info.equalsIgnoreCase("playerinfo")) playerinfo(Hostadress,name);
        if(info.equalsIgnoreCase("playeritems")) _PlayerItems = plugin.dataQueries.getPlayerItems(name);
        if(info.equalsIgnoreCase("playermail")) _PlayerMail = plugin.dataQueries.getMail(name);
        if(info.equalsIgnoreCase("playerauctions")) _PlayerAuction = plugin.dataQueries.getAuctionsLimitbyPlayer(name,0,2000,plugin.Myitems);
        if(info.equalsIgnoreCase("playertransaction")) plugin.dataQueries.GetTransactOfPlayer(name);
        print("","text/plain");
    }
    
    private void playerinfo(String Hostadress,String name) {
        _AuPlayer = plugin.dataQueries.getPlayer(name);
        StringBuilder response = new StringBuilder();
        response.append("<div id='playerinfo'>");
            response.append("<div style=\"text-align:center;\" >Player Info</div><br/>");
            response.append("<table width=\"100%\"><tr>");
            response.append("<td>ID</td><td>").append(_AuPlayer.getId()).append("</td></tr><tr>");
            response.append("<td>IP</td><td>").append(_AuPlayer.getIp()).append("</td></tr><tr>");
            response.append("<td>Name</td><td>").append(_AuPlayer.getName()).append("</td></tr><tr>");
            response.append("<td>CanBuy?</td><td>").append(_AuPlayer.getCanBuy()).append("</td></tr><tr>");
            response.append("<td>CanSell?</td><td>").append(_AuPlayer.getCanSell()).append("</td></tr><tr>");
            response.append("<td>isAdmin?</td><td>").append(_AuPlayer.getIsAdmin()).append("</td></tr><tr>");
            response.append("<td>Banned?</td><td>").append("SOON").append("</td></tr><tr>");
            response.append("<td>BAN</td><td>").append(HTMLBan(Hostadress,_AuPlayer.getId())).append("</td></tr>");
            response.append("</table>");
        response.append("</div>"); 
        print(response.toString(),"text/html");
    }
    
    private String HTMLBan(String ip,int id) {
      if(WebAuction.AuthPlayer.get(ip).AuctionPlayer.getIsAdmin() == 1) {
        return "<form action='ban/player' method='GET' onsubmit='return ban(this)'>"+
                "<input type='hidden' name='ID' value='"+id+"' />"+
                "<input type='submit' value='Ban' class='button' /></form><span id='"+id+"'></span>";
      }else{
        return "Can't Ban";
      }
    }
    
    private void playertransaction(String name) {
        List<Transact> Transacts = plugin.dataQueries.GetTransactOfPlayer(name);
        StringBuilder response = new StringBuilder();
        response.append("<div id='playertransaction'>");
            response.append("<div style=\"text-align:center;\" >Player Info</div><br/>");
            response.append("<table width=\"100%\"><tr>");
            response.append("<td>ID</td><td>").append(_AuPlayer.getId()).append("</td></tr><tr>");
            response.append("<td>IP</td><td>").append(_AuPlayer.getIp()).append("</td></tr><tr>");
            response.append("<td>Name</td><td>").append(_AuPlayer.getName()).append("</td></tr><tr>");
            response.append("<td>CanBuy?</td><td>").append(_AuPlayer.getCanBuy()).append("</td></tr><tr>");
            response.append("<td>CanSell?</td><td>").append(_AuPlayer.getCanSell()).append("</td></tr><tr>");
            response.append("<td>isAdmin?</td><td>").append(_AuPlayer.getIsAdmin()).append("</td></tr><tr>");
            response.append("<td>Banned?</td><td>").append("SOON").append("</td></tr><tr>");
            response.append("<td>BAN</td><td>").append(HTMLBan(Hostadress,_AuPlayer.getId())).append("</td></tr>");
            response.append("</table>");
        response.append("</div>"); 
        print(response.toString(),"text/html");
    }
}
