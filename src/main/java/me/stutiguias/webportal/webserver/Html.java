/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver;

import me.stutiguias.webportal.init.WebPortal;

/**
 *
 * @author Daniel
 */
public class Html {
    
    WebPortal plugin;
    
    public Html(WebPortal plugin) {
        this.plugin = plugin;
    }
    
    public String HTMLBuy(String ip,int ID){
      if(WebPortal.AuthPlayers.get(ip).AuctionPlayer.getCanBuy() == 1) {
        return "<form class='js-buyItems' onsubmit='return buy(this)'>"+
                "<input type='text' name='Quantity' onKeyPress='return numbersonly(this, event);' class='input' />"+
                "<input type='hidden' name='ID' value='"+ID+"' />"+
                "<input type='submit' value='" + plugin.Messages.get("Buy") + "' class='btn btn-primary' /></form><span id='"+ID+"'></span>";
      }else{
        return "Can't Buy";
      }
    }
    
    public String HTMLCancel(String ip,int ID){
        return "<form class='js-cancelAuction' onsubmit='return cancel(this)'>"+
                "<input type='hidden' name='ID' value='"+ID+"' />"+
                "<input type='submit' value='" + plugin.Messages.get("Cancel") + "' class='btn btn-primary' /></form><span id='C"+ID+"'></span>";
    }
    
}
