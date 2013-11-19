/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.request.type;

import java.util.List;
import java.util.Map;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.settings.Shop;
import me.stutiguias.webportal.webserver.HttpResponse;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Daniel
 */
public class AdminShopRequest extends HttpResponse {
    
    WebPortal plugin;
    
    public AdminShopRequest(WebPortal instance) {
        super(instance);
        plugin = instance;
    }
    
    public void AddShop(String ip,String url,Map param){
        
        if(isAdmin(ip)){
            String itemId = (String)param.get("itemId");
            String price = (String)param.get("price");
            String quantity = (String)param.get("quantity");
            
            ItemStack Item = ConvertToItemStack(itemId);
            if(Item == null) Print(message.WebIdNotFound,"text/html");
            Double Price = Double.parseDouble(price);
            Integer Quantity = Integer.parseInt(quantity);
            
            String type = Item.getType().toString();
            String searchtype = GetSearchType(Item);
            plugin.dataQueries.createItem(Item.getTypeId(), Item.getDurability(), "Server", Quantity, Price,"", plugin.Auction, type, searchtype);
            Print("ok","text/html");
        }else{
            Print(message.WebNotAdmin,"text/html");
        }
    }
    
    public void List(String ip,String url,Map param){
        
        if(isAdmin(ip)) {

            int iDisplayStart = Integer.parseInt((String)param.get("DisplayStart"));
            int iDisplayLength = Integer.parseInt((String)param.get("DisplayLength"));
            
            List<Shop> Auctions = plugin.dataQueries.getAuctionsLimitbyPlayer("Server", iDisplayStart, iDisplayLength, plugin.Auction);
            
            int TotalRecords = plugin.dataQueries.getFound();
            
            JSONArray jsonarray = new JSONArray();
            JSONObject jsonresult = new JSONObject();
            JSONObject jsonObjectArray;

            for (int i = 0; i < Auctions.size(); i++) {
                
                    Shop auction = Auctions.get(i);
                    jsonObjectArray = new JSONObject();
                    jsonObjectArray.put(message.WebItemName, ConvertItemToResult(auction,auction.getType()) );
                    jsonObjectArray.put(message.WebQuantity, auction.getItemStack().getAmount());
                    jsonObjectArray.put(message.WebPrice, auction.getPrice());
                    jsonObjectArray.put(message.WebDelete, HTMLDelete(ip,auction.getId()));
                    jsonarray.add(jsonObjectArray);
                 
            }
            jsonresult.put(TotalRecords,jsonarray);
            Print(jsonresult.toJSONString(),"application/json");
            
        }else{
            Print(message.WebNotAdmin,"text/html");
        }
    }
    
    public void Delete(String ip,String url,Map param) {
         if(isAdmin(ip)) {
            Integer id =  Integer.parseInt((String)param.get("ID"));
            plugin.dataQueries.DeleteAuction(id);
            Print(message.WebDeleted,"text/html");
         }else{
            Print(message.WebNotAdmin,"text/html");
         }
    }
    
    public String HTMLDelete(String ip,int ID){
      if(isAdmin(ip)) {
        return "<form class='js-adminShopDelete' onsubmit='return del(this)'>"+
                "<input type='hidden' name='ID' value='"+ID+"' />"+
                "<input type='submit' value='"+ message.WebDelete +"' class='btn' /></form><span id='"+ID+"'></span>";
      }else{
        return message.WebNotAdmin;
      }
    }
    
    public JSONObject NoAuction() {
            JSONObject jsonTwo = new JSONObject();
            jsonTwo.put("DT_RowId","row_0" );
            jsonTwo.put("DT_RowClass", "gradeU");
            jsonTwo.put("0", "");
            jsonTwo.put("1", "");
            jsonTwo.put("2", "");
            jsonTwo.put("3", message.WebNoShop);
            jsonTwo.put("4", "");
            jsonTwo.put("5", "");
            jsonTwo.put("6", "");
            jsonTwo.put("7", "");
            return jsonTwo;
    }
    
    public ItemStack ConvertToItemStack(String ItemId) {
        Integer Name;
        Short Damage;
        if(ItemId.contains(":")) {
            String[] NameDamage = ItemId.split(":");
            Name = Integer.parseInt(NameDamage[0]);
            Damage = Short.parseShort(NameDamage[1]);
        }else{
            Name = Integer.parseInt(ItemId);
            Damage = 0;
        }
        ItemStack item = new ItemStack(Name ,1,Damage);
        return item; 
    }
}
