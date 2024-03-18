/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.request;

import java.util.List;
import java.util.Map;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.init.json.JSONArray;
import me.stutiguias.webportal.init.json.JSONObject;
import me.stutiguias.webportal.model.Shop;
import me.stutiguias.webportal.model.WebSiteMail;
import me.stutiguias.webportal.webserver.HttpResponse;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Daniel
 */
@SuppressWarnings("unchecked")
public class MailRequest extends HttpResponse {

    public MailRequest(WebPortal plugin) {
        super(plugin);
    }
    
    public void GetMails(String ip,Map param) {
        int from = Integer.parseInt((String)param.get("from"));
        int qtd = Integer.parseInt((String)param.get("qtd"));
        
        String player = WebPortal.AuthPlayers.get(ip).WebSitePlayer.getName();
        List<WebSiteMail> mails = plugin.db.getMail(player,from,qtd);
        
        JSONObject json;
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < mails.size(); i++) {
            WebSiteMail mail = mails.get(i);
            json = new JSONObject();           
            json.put("1",JSON("Id",mail.getId()));
            json.put("2",JSON(message.WebItemName,ConvertItemToResult(mail.getItemStack())));
            json.put("3",JSON(message.WebQuantity,mail.getItemStack().getAmount()));
            json.put("4",JSON(message.WebItemCategory,mail.getItemStack().GetSearchType()));
            jsonArray.add(json);
        }
        JSONObject jsonresult = new JSONObject();
        jsonresult.put(plugin.db.getFound(),jsonArray);
        Print(jsonresult.toJSONString(),"application/json");
    }
    
    
    public void SendMail(String ip,String url,Map param) {
        int id = Integer.parseInt((String)param.get("ID"));
        int quantity = Integer.parseInt((String)param.get("Quantity"));
        if(quantity < 0) {
            Print(message.WebInvalidNumber,"text/plain");
            return;
        }
        Shop shop = plugin.db.getAuction(id);
        if(shop.getItemStack().getAmount() == quantity) {
            plugin.db.updateTable(id, plugin.Mail);
        }else if(shop.getItemStack().getAmount() < quantity) {
            Print(message.WebNotEnought,"text/plain");
            return;
        }else if(shop.getItemStack().getAmount() > quantity) {
            plugin.db.updateItemQuantity(shop.getItemStack().getAmount() - quantity, id);
            String SearchType = shop.getItemStack().GetSearchType();
            ItemMeta meta = shop.getItemStack().getItemMeta();
            int dmg = meta != null ? ((Damageable) meta).getDamage() : 0;
            plugin.db.CreateItem(shop.getItemStack().getType().name(),dmg,shop.getPlayerName(),quantity, shop.getPrice(),shop.getEnchantments(),plugin.Mail,shop.getType() , SearchType );
        }
        Print(message.WebMailSend,"text/plain");
    }
        
}
