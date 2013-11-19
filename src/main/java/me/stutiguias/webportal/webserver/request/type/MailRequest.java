/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.request.type;

import java.util.List;
import java.util.Map;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.settings.Shop;
import me.stutiguias.webportal.settings.WebSiteMail;
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
        List<WebSiteMail> mails = plugin.dataQueries.getMail(player,to,from);
        int founds = plugin.dataQueries.getFound();
        JSONObject json;
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < mails.size(); i++) {
            WebSiteMail mail = mails.get(i);
            String[] itemConfig = GetItemConfig(mail.getItemStack());
            
            if(plugin.AllowMetaItem) {
                itemConfig[0] = ChangeItemToItemMeta(mail, itemConfig[0]);
            }
            
            json = new JSONObject();
            json.put("Id",mail.getId());
            json.put(message.WebItemName,itemConfig[0]);
            json.put(message.WebQuantity,mail.getItemStack().getAmount());
            json.put(message.WebImage,itemConfig[1]);
            json.put(message.WebItemCategory,GetSearchType(mail.getItemStack()));
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
            Print(message.WebInvalidNumber,"text/plain");
            return;
        }
        Shop _Auction = plugin.dataQueries.getAuction(id);
        if(_Auction.getItemStack().getAmount() == quantity) {
            plugin.dataQueries.updateTable(id, plugin.Mail);
        }else if(_Auction.getItemStack().getAmount() < quantity) {
            Print(message.WebNotEnought,"text/plain");
            return;
        }else if(_Auction.getItemStack().getAmount() > quantity) {
            plugin.dataQueries.updateItemQuantity(_Auction.getItemStack().getAmount() - quantity, id);
            String SearchType = GetSearchType(_Auction.getItemStack());
            plugin.dataQueries.createItem(_Auction.getItemStack().getTypeId(),_Auction.getItemStack().getDurability(),_Auction.getPlayerName(),quantity, _Auction.getPrice(),_Auction.getEnchantments(),plugin.Mail,_Auction.getType() , SearchType );
        }
        Print(message.WebMailSend,"text/plain");
    }
        
}
