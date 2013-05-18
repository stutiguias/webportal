/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.request.type;

import java.util.List;
import java.util.Map;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.settings.Auction;
import me.stutiguias.webportal.settings.AuctionMail;
import me.stutiguias.webportal.webserver.HttpResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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
    
    public void GetMails(String ip,Map param) {
        Integer to = Integer.parseInt((String)param.get("to"));
        Integer from = Integer.parseInt((String)param.get("from"));
        
        String player = WebPortal.AuthPlayers.get(ip).AuctionPlayer.getName();
        List<AuctionMail> mails = plugin.dataQueries.getMail(player,to,from);
        int founds = plugin.dataQueries.getFound();
        JSONObject json;
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < mails.size(); i++) {
            AuctionMail mail = mails.get(i);
            String[] itemConfig = GetItemConfig(mail.getItemStack());
            json = new JSONObject();
            json.put("Id",mail.getId());
            json.put("Item Name",itemConfig[0]);
            json.put("Quantity",mail.getItemStack().getAmount());
            json.put("Image",itemConfig[1]);
            json.put("Item Category",itemConfig[2]);
            jsonArray.add(json);
        }
        JSONObject jsonresult = new JSONObject();
        jsonresult.put(founds,jsonArray);
        Print(jsonresult.toJSONString(),"application/json");
    }
    
    
    public void SendMail(String ip,String url,Map param) {
        int id = Integer.parseInt((String)param.get("ID"));
        int quantity = Integer.parseInt((String)param.get("Quantity"));
        if(quantity < 0) {
            Print("Invalid Number","text/plain");
            return;
        }
        Auction _Auction = plugin.dataQueries.getAuction(id);
        if(_Auction.getItemStack().getAmount() == quantity) {
            plugin.dataQueries.updateTable(id, plugin.Mail);
        }else if(_Auction.getItemStack().getAmount() < quantity) {
            Print("Not enought items","text/plain");
            return;
        }else if(_Auction.getItemStack().getAmount() > quantity) {
            plugin.dataQueries.updateItemQuantity(_Auction.getItemStack().getAmount() - quantity, id);
            String SearchType = GetSearchType(_Auction.getItemStack());
            //TODO: FIX ITEM META
            plugin.dataQueries.createItem(_Auction.getItemStack().getTypeId(),_Auction.getItemStack().getDurability(),_Auction.getPlayerName(),quantity, _Auction.getPrice(),_Auction.getEnchantments(),plugin.Mail,_Auction.getType() , SearchType );
        }
        Print("Mailt send","text/plain");
    }
        
}
