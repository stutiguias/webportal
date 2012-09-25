/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.request;

import java.net.Socket;
import java.util.List;
import java.util.Map;
import me.stutiguias.webportal.init.WebAuction;
import me.stutiguias.webportal.settings.Auction;
import me.stutiguias.webportal.settings.Enchant;
import me.stutiguias.webportal.webserver.Html;
import me.stutiguias.webportal.webserver.Material;
import me.stutiguias.webportal.webserver.Response;
import org.bukkit.enchantments.Enchantment;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Daniel
 */
public class FillMyAuctions extends Response {
    
    private WebAuction plugin;
    private Html html;
    
    public FillMyAuctions(WebAuction plugin,Socket s) {
        super(plugin, s);
        this.plugin = plugin;
        html = new Html(plugin);
    }
    
    public void getMyAuctions(String ip,String url,String param) {
        int iDisplayStart = Integer.parseInt(getParam("iDisplayStart", param));
        int iDisplayLength = Integer.parseInt(getParam("iDisplayLength", param));
        List<Auction> la = plugin.dataQueries.getAuctionsLimitbyPlayer(WebAuction.AuthPlayer.get(ip).AuctionPlayer.getName(),iDisplayStart,iDisplayLength,plugin.Auction);
        int sEcho = Integer.parseInt(getParam("sEcho", param));
        int iTotalRecords = plugin.dataQueries.getFound();
        int iTotalDisplayRecords = iTotalRecords;
        JSONObject json = new JSONObject();
        JSONArray jsonData = new JSONArray();
        JSONObject jsonTwo;
        
        json.put("sEcho", sEcho);
        json.put("iTotalRecords", iTotalRecords);
        json.put("iTotalDisplayRecords", iTotalDisplayRecords);
        
        if(iTotalRecords > 0) {
            for(Auction item:la){
                jsonTwo = new JSONObject();
                jsonTwo.put("DT_RowId","row_" + item.getId() );
                jsonTwo.put("DT_RowClass", "gradeA");
                
                String item_name = item.getItemStack().getType().toString().toLowerCase();
                Short dmg = item.getItemStack().getDurability();
                String Durability = "";
                if(item.getItemStack().getType().isBlock())
                {
                    item_name = Material.getItemName(item.getItemStack().getTypeId() , dmg).toLowerCase();
                }else {
                    Durability = (!dmg.equals(Short.valueOf("0"))) ? "Dur.: " + dmg + "%" : "";
                }
                
                String enchant = "";
                for (Map.Entry<Enchantment, Integer> entry : item.getItemStack().getEnchantments().entrySet()) {
                    int enchId = entry.getKey().getId();
                    int level = entry.getValue();
                    enchant += new Enchant().getEnchantName(enchId, level);
                }

                jsonTwo.put("0", "<img src='images/"+item_name.replace(" ","_") +".png'><br /><font size='-1'>"+ item_name.replace("_"," ") + "<br />" + Durability + enchant +"</font>");
                jsonTwo.put("1", "Never");
                jsonTwo.put("2", item.getItemStack().getAmount());
                jsonTwo.put("3", "$ " + item.getPrice());
                jsonTwo.put("4", "$ " + item.getPrice() * item.getItemStack().getAmount());
                jsonTwo.put("5", "N/A" );
                jsonTwo.put("6", html.HTMLCancel(ip,item.getId()));

                jsonData.add(jsonTwo);
            }
        }else{
                jsonTwo = new JSONObject();
                jsonTwo.put("DT_RowId","row_0" );
                jsonTwo.put("DT_RowClass", "gradeU");
                jsonTwo.put("0", "");
                jsonTwo.put("1", "");
                jsonTwo.put("2", "");
                jsonTwo.put("3", "No Auction");
                jsonTwo.put("4", "");
                jsonTwo.put("5", "");
                jsonTwo.put("6", "");
                jsonData.add(jsonTwo);
        }
        json.put("aaData",jsonData);
        
        print(json.toJSONString(),"text/plain");
    }
}
