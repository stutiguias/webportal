/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.request;

import java.net.Socket;
import java.util.List;
import java.util.Map;
import me.stutiguias.webportal.init.WebAuction;
import me.stutiguias.webportal.settings.Auction;
import me.stutiguias.webportal.settings.Enchant;
import me.stutiguias.webportal.webserver.Html;
import me.stutiguias.webportal.webserver.Response;
import org.bukkit.enchantments.Enchantment;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Daniel
 */
public class FillAuction extends Response {
    
    private WebAuction plugin;
    private Html html;
    
    public FillAuction(WebAuction plugin,Socket s) {
        super(plugin, s);
        this.plugin = plugin;
        html = new Html();
    }
    
    public void fillAuction(String ip,String url,String param)
    {
        if(url.contains("byall"))  getAuctionByAll(ip, url, param);
        if(url.contains("byblock")) getAuctionByBlock(ip, url, param);
    }
    
    public void getAuctionByAll(String ip,String url,String param) {
        int iDisplayStart = Integer.parseInt(getParam("iDisplayStart", param));
        int iDisplayLength = Integer.parseInt(getParam("iDisplayLength", param));
        String search = getParam("sSearch", param);
        List<Auction> la = plugin.dataQueries.getSearchAuctions(iDisplayStart,iDisplayLength,search,"nothing");
        int sEcho = Integer.parseInt(getParam("sEcho", param));
        int iTotalRecords = plugin.dataQueries.getFound();
        int iTotalDisplayRecords = plugin.dataQueries.getFound();
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
                jsonTwo.put("0",ConvertItemToResult(item));
                jsonTwo.put("1", "<img width='32' src='http://minotar.net/avatar/"+ item.getPlayerName() +"' /><br />"+ item.getPlayerName());
                jsonTwo.put("2", "Never");
                jsonTwo.put("3", item.getItemStack().getAmount());
                jsonTwo.put("4", "$ " + item.getPrice());
                jsonTwo.put("5", "$ " + item.getPrice() * item.getItemStack().getAmount());
                jsonTwo.put("6", "N/A");
                jsonTwo.put("7", html.HTMLBuy(ip,item.getId()));
                jsonData.add(jsonTwo);
            }
        }else{
            jsonData.add(NoAuction());
        }
        json.put("aaData",jsonData);
        
        print(json.toJSONString(),"text/plain");   
    }
    
    public void getAuctionByBlock(String ip,String url,String param) {
        int iDisplayStart = Integer.parseInt(getParam("iDisplayStart", param));
        int iDisplayLength = Integer.parseInt(getParam("iDisplayLength", param));
        String search = getParam("sSearch", param);
        int sEcho = Integer.parseInt(getParam("sEcho", param));
        
        List<Auction> la = plugin.dataQueries.getSearchAuctions(iDisplayStart,iDisplayLength,search,"block");
        int iTotalRecords = plugin.dataQueries.getFound();
        int iTotalDisplayRecords = plugin.dataQueries.getFound();
        
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
                jsonTwo.put("0", ConvertItemToResult(item));
                jsonTwo.put("1", "<img width='32' src='http://minotar.net/avatar/"+ item.getPlayerName() +"' /><br />"+ item.getPlayerName());
                jsonTwo.put("2", "Never");
                jsonTwo.put("3", item.getItemStack().getAmount());
                jsonTwo.put("4", "$ " + item.getPrice());
                jsonTwo.put("5", "$ " + item.getPrice() * item.getItemStack().getAmount());
                jsonTwo.put("6", "N/A");
                jsonTwo.put("7", html.HTMLBuy(ip,item.getId()));
                jsonData.add(jsonTwo);
            }
        }else{
           jsonData.add(NoAuction());
        }
        json.put("aaData",jsonData);
        
        print(json.toJSONString(),"text/plain");
    }
    
    public JSONObject NoAuction() {
            JSONObject jsonTwo = new JSONObject();
            jsonTwo.put("DT_RowId","row_0" );
            jsonTwo.put("DT_RowClass", "gradeU");
            jsonTwo.put("0", "");
            jsonTwo.put("1", "");
            jsonTwo.put("2", "");
            jsonTwo.put("3", "No Auction");
            jsonTwo.put("4", "");
            jsonTwo.put("5", "");
            jsonTwo.put("6", "");
            jsonTwo.put("7", "");
            return jsonTwo;
    }
    
    public String ConvertItemToResult(Auction item) {
        String item_name;
        Short dmg = item.getItemStack().getDurability();
        String Durability = "";
        
        // Not is a block ( have durability )
        if(!item.getItemStack().getType().isBlock()) {
            Durability = (!dmg.equals(Short.valueOf("0"))) ? "Dur.: " + dmg + "%" : "";
        }
        item_name = "lang_" + item.getItemStack().getTypeId();
        
        // its a bloco and dmg not 0 ( durability = id )
        if(!dmg.equals(Short.valueOf("0")) && item.getItemStack().getType().isBlock())
        {
            item_name += "_" + dmg.toString();
        }
        
        // Enchant if need
        String enchant = "";
        for (Map.Entry<Enchantment, Integer> entry : item.getItemStack().getEnchantments().entrySet()) {
            int enchId = entry.getKey().getId();
            int level = entry.getValue();
            enchant += "<br />" + new Enchant().getEnchantName(enchId, level);
        }
        
        return "<img src='images/"+ item_name.toLowerCase() +".png'><br /><font size='-1'>"+ item_name + "<br />" + Durability + enchant +"</font>";
    }
}
