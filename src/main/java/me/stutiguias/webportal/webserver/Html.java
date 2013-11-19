/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver;

import me.stutiguias.webportal.init.Messages;
import me.stutiguias.webportal.init.WebPortal;

/**
 *
 * @author Daniel
 */
public class Html {
    
    WebPortal plugin;
    private Messages message;
    
    public Html(WebPortal plugin) {
        this.plugin = plugin;
        this.message = WebPortal.Messages;
    }
    
    public String HTMLBan(String ip,int id) {
      if(WebPortal.AuthPlayers.get(ip).AuctionPlayer.getIsAdmin() == 1) {
        return "<form onsubmit='return websiteban(this)'>"+
                "<input type='hidden' name='ID' value='"+id+"' />"+
                "<input type='submit' value='Ban' class='button' /></form><span id='"+id+"'></span>"+
                "<form onsubmit='return websiteunban(this)'>"+
                "<input type='hidden' name='ID' value='"+id+"' />"+
                "<input type='submit' value='UNBan' class='button' /></form><span id='"+id+"'></span>";
      }else{
        return "Can't Ban";
      }
    }
        
    public String HTMLBuy(String ip,int ID){
      if(WebPortal.AuthPlayers.get(ip).AuctionPlayer.getCanBuy() == 1) {
        return "<form class='js-buyItems' onsubmit='return buy(this)'>"+
                "<input type='text' name='Quantity' onKeyPress='return numbersonly(this, event);' class='input' />"+
                "<input type='hidden' name='ID' value='"+ID+"' />"+
                "<input type='submit' value='" + message.WebBuy + "' class='btn btn-primary' /></form><span id='"+ID+"'></span>";
      }else{
        return "Can't Buy";
      }
    }
    
    public String HTMLSell(String ip,int ID){
      if(WebPortal.AuthPlayers.get(ip).AuctionPlayer.getCanBuy() == 1) {
        return "<form class='js-buyItems' onsubmit='return sell(this)'>"+
                "<input type='text' name='Quantity' onKeyPress='return numbersonly(this, event);' class='input' />"+
                "<input type='hidden' name='ID' value='"+ID+"' />"+
                "<input type='submit' value='"+ message.WebSell +"' class='btn btn-primary' /></form><span id='"+ID+"'></span>";
      }else{
        return "Can't Sell";
      }
    }
    
    public String HTMLCancel(String ip,int ID){
        return "<form class='js-cancelAuction' onsubmit='return cancel(this)'>"+
                "<input type='hidden' name='ID' value='"+ID+"' />"+
                "<input type='submit' value='" + message.WebCancel + "' class='btn btn-primary' /></form><span id='C"+ID+"'></span>";
    }
    
}
