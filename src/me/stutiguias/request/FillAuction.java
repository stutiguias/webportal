/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.request;

import java.net.Socket;
import java.util.Iterator;
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
        if(url.contains("byall"))  getAuctionBy(ip, url, param,"nothing");
        if(url.contains("byblock")) getAuctionBy(ip, url, param,"Block");
        if(url.contains("byfood")) getAuctionBy(ip, url, param,"Food");
        if(url.contains("bytools")) getAuctionBy(ip, url, param,"Tools");
        if(url.contains("bycombat")) getAuctionBy(ip, url, param,"Combat");
        if(url.contains("byredstone")) getAuctionBy(ip, url, param,"Redstone");
        if(url.contains("bydecoration")) getAuctionBy(ip, url, param,"Decoration");
        if(url.contains("bytransportation")) getAuctionBy(ip, url, param,"Transportation");
        if(url.contains("bymicellaneous")) getAuctionBy(ip, url, param,"Micellaneous");
        if(url.contains("bymaterials")) getAuctionBy(ip, url, param,"Materials");
    }
    
    public void getAuctionBy(String ip,String url,String param,String searchtype) {
        int iDisplayStart = Integer.parseInt(getParam("iDisplayStart", param));
        int iDisplayLength = Integer.parseInt(getParam("iDisplayLength", param));
        String search = getParam("sSearch", param);
        int sEcho = Integer.parseInt(getParam("sEcho", param));
        
        List<Auction> la = plugin.dataQueries.getSearchAuctions(iDisplayStart,iDisplayLength,search,searchtype);
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
                jsonTwo.put("0", ConvertItemToResult(item,searchtype));
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
    
    public String ConvertItemToResult(Auction item,String type) {
        String item_name;
        Short dmg = item.getItemStack().getDurability();
        String Durability = "";
        
        // Not is a block ( have durability )
        if(!item.getItemStack().getType().isBlock()) {
            Durability = (!dmg.equals(Short.valueOf("0"))) ? "Dur.: " + dmg + "%" : "";
        }
        String Item_name = Material.getItemName(item.getItemStack().getTypeId(),item.getItemStack().getDurability());
        item_name = getConfigName(Item_name,type);
                
        // Enchant if need
        String enchant = "";
        for (Map.Entry<Enchantment, Integer> entry : item.getItemStack().getEnchantments().entrySet()) {
            int enchId = entry.getKey().getId();
            int level = entry.getValue();
            enchant += "<br />" + new Enchant().getEnchantName(enchId, level);
        }
        
        return "<img src='images/"+ item_name.replace(" ","_").toLowerCase() +".png'><br /><font size='-1'>"+ item_name + "<br />" + Durability + enchant +"</font>";
    }
    
    public String getConfigName(String Itemname,String type) {
            try {
                for (Iterator<String> it = plugin.materials.getConfig().getConfigurationSection(type).getKeys(false).iterator(); it.hasNext();) {
                    String key = it.next();
                    if(key.equalsIgnoreCase(Itemname)) return plugin.materials.getConfig().getString(type + "." + key);
                }
            }catch(NullPointerException ex){
                WebAuction.log.warning("Unable to search by item type "+ type);
                ex.getMessage();
            }
            return Itemname;
    }
}
