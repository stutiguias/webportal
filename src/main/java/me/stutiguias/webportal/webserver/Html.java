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
    
    public String HTMLBan(String ip,int id) {
      if(WebPortal.AuthPlayers.get(ip).WebSitePlayer.getIsAdmin() == 1) {
        return "<form onsubmit='return websiteban(this)'>"+
                "<input type='hidden' name='ID' value='"+id+"' />"+
                "<input type='submit' value='Website Ban' class='button' /></form><span id='"+id+"'></span>"+
                "<form onsubmit='return websiteunban(this)'>"+
                "<input type='hidden' name='ID' value='"+id+"' />"+
                "<input type='submit' value='Website UNBan' class='button' /></form><span id='"+id+"'></span>";
      }else{
        return "Can't Ban";
      }
    }

}
