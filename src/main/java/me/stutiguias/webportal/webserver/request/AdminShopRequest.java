/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.request;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.init.json.JSONArray;
import me.stutiguias.webportal.init.json.JSONObject;
import me.stutiguias.webportal.model.Shop;
import me.stutiguias.webportal.model.WebItemStack;
import me.stutiguias.webportal.webserver.HttpResponse;
import org.bukkit.inventory.meta.Damageable;

/**
 *
 * @author Daniel
 */
@SuppressWarnings("unchecked")
public class AdminShopRequest extends HttpResponse {
  
    public AdminShopRequest(WebPortal instance) {
        super(instance);
    }
    
    public void AddShop(String ip,String url,Map param){
        
        if(isAdmin(ip)){
            try{
                String itemId = (String)param.get("itemId");
                Double price = Double.parseDouble((String)param.get("price"));
                int quantity = Integer.parseInt((String)param.get("quantity"));

                WebItemStack Item = ConvertInputToWebItemStack(itemId);
                if(Item == null) Print(message.WebIdNotFound,"text/html");

                String type = Objects.requireNonNull(Item).getType().toString();
                plugin.db.CreateItem(Item.getType().name(),((Damageable) Objects.requireNonNull(Item.getItemMeta())).getDamage(), "Server", quantity, price,"", plugin.Sell, type, Item.GetSearchType());
                Print("ok","text/html");
            }catch (Exception e){
                Print("Invalid Material","text/html");
            }

        }else{
            Print(message.WebNotAdmin,"text/html");
        }
    }
    
    public void List(String ip,String url,Map param){
        
        if(isAdmin(ip)) {

            int iDisplayStart = Integer.parseInt((String)param.get("DisplayStart"));
            int iDisplayLength = Integer.parseInt((String)param.get("DisplayLength"));
            
            List<Shop> Auctions = plugin.db.getAuctionsLimitbyPlayer("Server", iDisplayStart, iDisplayLength, plugin.Sell);
            
            int TotalRecords = plugin.db.getFound();
            
            JSONArray jsonarray = new JSONArray();
            JSONObject jsonresult = new JSONObject();
            JSONObject jsonObjectArray;

            for (int i = 0; i < Auctions.size(); i++) {
                
                    Shop auction = Auctions.get(i);
                    jsonObjectArray = new JSONObject();
                    jsonObjectArray.put(message.WebItemName, ConvertItemToResult(auction.getItemStack()) );
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
            plugin.db.DeleteAuction(id);
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
    
}
