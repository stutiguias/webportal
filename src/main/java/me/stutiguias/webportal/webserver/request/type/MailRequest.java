/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.request.type;

import java.util.List;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.settings.AuctionMail;
import me.stutiguias.webportal.webserver.HttpResponse;

/**
 *
 * @author Daniel
 */
public class MailRequest extends HttpResponse {
    
    private WebPortal plugin;
    
    public MailRequest(WebPortal plugin) {
        super(plugin);
        this.plugin = plugin;
    }
    
    public void GetMails(String ip) {
        String player = WebPortal.AuthPlayers.get(ip).AuctionPlayer.getName();
        List<AuctionMail> mails = plugin.dataQueries.getMail(player);
        
        
        Print("","application/json");
    }
    
}
