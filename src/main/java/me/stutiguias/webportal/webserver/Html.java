/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver;

import me.stutiguias.webportal.init.WebAuction;

/**
 *
 * @author Daniel
 */
public class Html {
    
    public String HTMLBuy(String ip,int ID){
      if(WebAuction.AuthPlayer.get(ip).AuctionPlayer.getCanBuy() == 1) {
        return "<form action='buy/item' method='GET' onsubmit='return buy(this)'>"+
                "<input type='text' name='Quantity' onKeyPress='return numbersonly(this, event);' class='input' />"+
                "<input type='hidden' name='ID' value='"+ID+"' />"+
                "<input type='submit' value='Buy' class='button' /></form><span id='"+ID+"'></span>";
      }else{
        return "Can't Buy";
      }
    }
    
    public String HTMLCancel(String ip,int ID){
        return "<form action='cancel/auction' method='GET' onsubmit='return cancel(this)'>"+
                "<input type='hidden' name='ID' value='"+ID+"' />"+
                "<input type='submit' value='Cancel' class='button' /></form><span id='C"+ID+"'></span>";
    }
    
    public String HTMLAuctionCreate(String ip,int ID) {
      if(WebAuction.AuthPlayer.get(ip).AuctionPlayer.getCanSell() == 1) {
        return "<form action='web/postauction' method='GET' onsubmit='return postauction(this)'>"+
                "Quantity: <input type='text' name='Quantity' onKeyPress='return numbersonly(this, event);' class='input' /><br />"+
                "<input type='hidden' name='ID' value='"+ID+"' />"+
                "Price: <input name='Price' type='text' onKeyPress='return numbersonly(this, event);' class='input' size='10' /><br />"+
                "<input type='submit' value='Create Auction' class='button' /></form><span id='"+ID+"'></span>";
      }else{
        return "Can't Sell";
      }
    }
    
    public String HTMLAuctionMail(String ip,int ID) {
        return "<form action='web/mail' method='GET' onsubmit='return mail(this)'>"+
                "<input type='hidden' name='ID' value='"+ID+"' />"+
                "<input type='submit' value='Mail it' class='button' /></form><span id='M"+ID+"'></span>";
    }
}
