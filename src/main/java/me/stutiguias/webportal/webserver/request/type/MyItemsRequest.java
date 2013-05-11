/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.request.type;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.settings.Auction;
import me.stutiguias.webportal.webserver.Html;
import me.stutiguias.webportal.webserver.HttpResponse;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Daniel
 */
public class MyItemsRequest extends HttpResponse {
    
    private WebPortal plugin;
    private Html html;
    
    public MyItemsRequest(WebPortal plugin) {
        super(plugin);
        this.plugin = plugin;
        html = new Html(plugin);
    }
        
    public void CreateAuction(String ip,String url,Map param) {
        int qtd;
        Double price;
        int id;
        try {
            price = Double.parseDouble((String)param.get("Price"));
            id = Integer.parseInt((String)param.get("ID"));
            qtd = Integer.parseInt((String)param.get("Quantity"));
        }catch(NumberFormatException ex) {
            Print("Invalid Number","text/plain");
            return;
        }
        Auction auction = plugin.dataQueries.getItemById(id,plugin.Myitems);
        if(auction.getQuantity() == qtd) {
            plugin.dataQueries.setPriceAndTable(id,price);
            Print("You have sucess create Auction","text/plain");
        }else{
            if(auction.getQuantity() > qtd)
            {
              plugin.dataQueries.UpdateItemAuctionQuantity(auction.getQuantity() - qtd, id);
              Short dmg = Short.valueOf(String.valueOf(auction.getDamage()));
              ItemStack stack = new ItemStack(auction.getName(),auction.getQuantity(),dmg);  
              String type =  stack.getType().toString();
              String[] itemConfig = GetItemConfig(stack);
              String ItemName = itemConfig[0];
              String searchtype = itemConfig[2];
              plugin.dataQueries.createItem(auction.getName(),auction.getDamage(),auction.getPlayerName(),qtd,price,auction.getEnchantments(),plugin.Auction,type,ItemName,searchtype);
              Print("You have successfully created an Auction","text/plain");
            }else{
              Print("You not permit to sell more then you have","text/plain");
            }
        }
    }
        
    public void GetMyItems(String ip,String url,Map param) {

        int iDisplayStart = Integer.parseInt((String)param.get("iDisplayStart"));
        int iDisplayLength = Integer.parseInt((String)param.get("iDisplayLength"));
        String search = (String)param.get("sSearch");
        int sEcho =  Integer.parseInt((String)param.get("sEcho"));
        
        List<Auction> auctions = plugin.dataQueries.getAuctionsLimitbyPlayer(WebPortal.AuthPlayers.get(ip).AuctionPlayer.getName(),iDisplayStart,iDisplayLength,plugin.Myitems);
        
        if(CheckError(ip, auctions)) return;
        
        int iTotalRecords = plugin.dataQueries.getFound();
        int iTotalDisplayRecords = iTotalRecords;
        
        JSONObject json = new JSONObject();
        JSONArray jsonData = new JSONArray();
        JSONObject jsonTwo;
        
        json.put("sEcho", sEcho);
        json.put("iTotalRecords", iTotalRecords);
        json.put("iTotalDisplayRecords", iTotalDisplayRecords);
        
        if(iTotalRecords > 0) {
            for(Auction item:auctions){
                double mprice = plugin.dataQueries.GetMarketPriceofItem(item.getItemStack().getTypeId(),item.getItemStack().getDurability());
                jsonTwo = new JSONObject();
                jsonTwo.put("DT_RowId","row_" + item.getId() );
                jsonTwo.put("DT_RowClass", "gradeA");
                jsonTwo.put("0", ConvertItemToResult(item,item.getType()));
                jsonTwo.put("1", item.getItemStack().getAmount());
                jsonTwo.put("2", mprice);
                jsonTwo.put("3", mprice * item.getItemStack().getAmount());
                jsonTwo.put("4", GetEnchant(item));
                jsonTwo.put("5", GetDurability(item));
                
                jsonData.add(jsonTwo);
            }
        }else{
                jsonTwo = new JSONObject();
                jsonTwo.put("DT_RowId","row_0" );
                jsonTwo.put("DT_RowClass", "gradeU");
                jsonTwo.put("0", "");
                jsonTwo.put("1", "");
                jsonTwo.put("2", "");
                jsonTwo.put("3", "No Items");
                jsonTwo.put("4", "");
                jsonTwo.put("5", "");
                    
                jsonData.add(jsonTwo);
        }
        json.put("aaData",jsonData);
        
        Print(json.toJSONString(),"text/plain");
    }
    
    public void GetMyItems(String ip) {
        List<Auction> auctions = plugin.dataQueries.getPlayerItems(WebPortal.AuthPlayers.get(ip).AuctionPlayer.getName());
        JSONObject json = new JSONObject();
        for(Auction item:auctions){
            String[] itemConfig = GetItemConfig(item.getItemStack());
            
            JSONObject jsonNameImg = new JSONObject();
            jsonNameImg.put(itemConfig[0],itemConfig[1]);
            jsonNameImg.put("enchant",GetEnchant(item));
            
            json.put(item.getId(),jsonNameImg);
        }
        Print(json.toJSONString(), "text/plain");
    }
    
    public Boolean CheckError(String ip,List<Auction> auctions) {
        if(WebPortal.AuthPlayers.get(ip).AuctionPlayer.getName() == null) {
            WebPortal.logger.log(Level.WARNING,"Cant determine player name");
            return true;
        }
        if(auctions == null) {
            WebPortal.logger.log(Level.WARNING,"Cant get auctions");
            return true;
        }
        return false;
    }
}
