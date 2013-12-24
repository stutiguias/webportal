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

    public MailRequest(WebPortal plugin) {
        super(plugin);
    }
    
    public void GetMails(String ip,Map param) {
        Integer from = Integer.parseInt((String)param.get("from"));
        Integer qtd = Integer.parseInt((String)param.get("qtd"));
        
        String player = WebPortal.AuthPlayers.get(ip).AuctionPlayer.getName();
        List<WebSiteMail> mails = plugin.dataQueries.getMail(player,from,qtd);
        
        JSONObject json;
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < mails.size(); i++) {
            WebSiteMail mail = mails.get(i);
            json = new JSONObject();           
            json.put("1",JSON("Id",mail.getId()));
            json.put("2",JSON(message.WebItemName,ConvertItemToResult(mail.getId(),mail.getItemStack(),mail.getItemStack().getType().toString())));
            json.put("3",JSON(message.WebQuantity,mail.getItemStack().getAmount()));
            json.put("4",JSON(message.WebItemCategory,GetSearchType(mail.getItemStack())));
            jsonArray.add(json);
        }
        JSONObject jsonresult = new JSONObject();
        jsonresult.put(plugin.dataQueries.getFound(),jsonArray);
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
