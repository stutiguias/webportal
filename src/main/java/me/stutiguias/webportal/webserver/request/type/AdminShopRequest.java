/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.request.type;

import java.util.List;
import java.util.Map;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.settings.Auction;
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
            String price = (String)param.get("Price");
            String quantity = (String)param.get("quantity");
            
            ItemStack Item = ConvertToItemStack(itemId);
            if(Item == null) Print("Item ID not found","text/html");
            Double Price = Double.parseDouble(price);
            Integer Quantity = Integer.parseInt(quantity);
            
            String type = Item.getType().toString();
            String[] itemConfig = GetItemConfig(Item);
            String ItemName = itemConfig[0];
            String searchtype = itemConfig[2];
            plugin.dataQueries.createItem(Item.getTypeId(), Item.getDurability(), "Server", Quantity, Price,"", plugin.Auction, type, ItemName, searchtype );
            Print("ok","text/html");
            
        }else{
            Print("You r not admin","text/html");
        }
    }
    
    public void List(String ip,String url,Map param){
        if(isAdmin(ip)) {

            int iDisplayStart = Integer.parseInt((String)param.get("iDisplayStart"));
            int iDisplayLength = Integer.parseInt((String)param.get("iDisplayLength"));
            String search = (String)param.get("sSearch");
            int sEcho =  Integer.parseInt((String)param.get("sEcho"));

            List<Auction> Auctions = plugin.dataQueries.getAuctionsLimitbyPlayer("Server", iDisplayStart, iDisplayLength, plugin.Auction);
            
            int iTotalRecords = plugin.dataQueries.getFound();
            int iTotalDisplayRecords = plugin.dataQueries.getFound();

            JSONObject Response = new JSONObject();
            JSONArray Data = new JSONArray();
            JSONObject tmp_Data;

            Response.put("sEcho", sEcho);
            Response.put("iTotalRecords", iTotalRecords);
            Response.put("iTotalDisplayRecords", iTotalDisplayRecords);

            if(iTotalRecords > 0) {
                 for (int i = 0; i < Auctions.size(); i++) {
                    Auction auction = Auctions.get(i);
                    tmp_Data = new JSONObject();
                    tmp_Data.put("DT_RowId","row_" + auction.getId() );
                    tmp_Data.put("DT_RowClass", "A");
                    tmp_Data.put("0", ConvertItemToResult(auction,auction.getType()));
                    tmp_Data.put("1", "Server");
                    tmp_Data.put("2", "Never");
                    tmp_Data.put("3", auction.getItemStack().getAmount());
                    tmp_Data.put("4", "$ " + auction.getPrice());
                    tmp_Data.put("5", "");
                    tmp_Data.put("6", "");
                    tmp_Data.put("7", HTMLDelete(ip,auction.getId()));
                    Data.add(tmp_Data);
                 }
            }else{
                    Data.add(NoAuction());
            }
            Response.put("aaData",Data);

            Print(Response.toJSONString(),"text/plain");
        }else{
            Print("You r not admin","text/html");
        }
    }
    
    public void Delete(String ip,String url,Map param) {
         if(isAdmin(ip)) {
            Integer id =  Integer.parseInt((String)param.get("ID"));
            plugin.dataQueries.DeleteAuction(id);
            Print("Deleted","text/html");
         }else{
            Print("You r not admin","text/html");
         }
    }
    
    public String HTMLDelete(String ip,int ID){
      if(isAdmin(ip)) {
        return "<form class='js-adminShopDelete' onsubmit='return del(this)'>"+
                "<input type='hidden' name='ID' value='"+ID+"' />"+
                "<input type='submit' value='Delete' class='button' /></form><span id='"+ID+"'></span>";
      }else{
        return "Your r not admin";
      }
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
