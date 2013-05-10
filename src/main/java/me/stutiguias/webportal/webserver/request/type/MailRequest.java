/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.request.type;

import java.util.List;
import java.util.Map;
import me.stutiguias.webportal.init.WebPortal;
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
    
}
