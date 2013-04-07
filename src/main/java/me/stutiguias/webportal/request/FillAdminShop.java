/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.request;

import java.net.Socket;
import java.util.List;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.settings.Auction;
import me.stutiguias.webportal.webserver.Material;
import me.stutiguias.webportal.webserver.Response;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Daniel
 */
public class FillAdminShop extends Response {
    
    WebPortal plugin;
    
    public FillAdminShop(WebPortal instance,Socket s) {
        super(instance,s);
        plugin = instance;
    }
    
    public void AddShop(String ip,String url,String param){
        if(isAdmin(ip)){
            ItemStack Item = ConvertToItemStack(getParam("ItemId", param));
            if(Item == null) print("Item ID not found","text/html");
            Double Price = Double.parseDouble(getParam("Price", param));
            Integer Quantity = Integer.parseInt(getParam("quantity", param));
            
            String type = Item.getType().toString();
            String ItemName = getItemNameAndImg(Item)[0];
            String searchtype = plugin.getSearchType(ItemName);
            plugin.dataQueries.createItem(Item.getTypeId(), Item.getDurability(), "Server", Quantity, Price,"", plugin.Auction, type, ItemName, searchtype );
            print("ok","text/html");
        }else{
            print("You r not admin","text/html");
        }
    }
    
    public void list(String ip,String url,String param){
        if(isAdmin(ip)) {
            
            int iDisplayStart = Integer.parseInt(getParam("iDisplayStart", param));
            int iDisplayLength = Integer.parseInt(getParam("iDisplayLength", param));
            String search = getParam("sSearch", param);
            int sEcho = Integer.parseInt(getParam("sEcho", param));

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

            print(Response.toJSONString(),"text/plain");
        }else{
            print("You r not admin","text/html");
        }
    }
    
    public void Delete(String ip,String url,String param) {
         if(isAdmin(ip)) {
            Integer id = Integer.parseInt(getParam("ID", param));
            plugin.dataQueries.DeleteAuction(id);
            print("Deleted","text/html");
         }else{
            print("You r not admin","text/html");
         }
    }
    
    public String HTMLDelete(String ip,int ID){
      if(isAdmin(ip)) {
        return "<form action='web/delete' method='GET' onsubmit='return del(this)'>"+
                "<input type='hidden' name='ID' value='"+ID+"' />"+
                "<input type='submit' value='Delete' class='button' /></form><span id='"+ID+"'></span>";
      }else{
        return "Can't Buy";
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
