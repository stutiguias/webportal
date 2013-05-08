/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.request.type;

import java.util.List;
import java.util.Map;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.settings.Auction;
import me.stutiguias.webportal.settings.AuctionPlayer;
import me.stutiguias.webportal.settings.TradeSystem;
import me.stutiguias.webportal.webserver.HttpResponse;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Daniel
 */
public class OperationsRequest extends HttpResponse {
        
    private WebPortal plugin;
    TradeSystem tr;
    
    public OperationsRequest(WebPortal plugin) {
        super(plugin);
        this.plugin = plugin;
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
    
    public void Mail(String ip,String url,Map param) {
        int id = Integer.parseInt((String)param.get("ID"));
        int quantity = Integer.parseInt((String)param.get("Quantity"));
        Auction _Auction = plugin.dataQueries.getAuction(id);
        if(_Auction.getItemStack().getAmount() == quantity) {
            plugin.dataQueries.updateTable(id, plugin.Mail);
        }else if(_Auction.getItemStack().getAmount() < quantity) {
            Print("Not enought items","text/plain");
            return;
        }else if(_Auction.getItemStack().getAmount() > quantity) {
            plugin.dataQueries.updateItemQuantity(_Auction.getItemStack().getAmount() - quantity, id);
            String[] ItemConfig = GetItemConfig(_Auction.getItemStack());
            String itemName = ItemConfig[0];
            String SearchType = ItemConfig[2];
            plugin.dataQueries.createItem(_Auction.getItemStack().getTypeId(),_Auction.getItemStack().getDurability(),_Auction.getPlayerName(),quantity, _Auction.getPrice(),_Auction.getEnchantments(),plugin.Mail,_Auction.getType(), itemName , SearchType );
        }
        Print("Mailt send","text/plain");
    }
    
    public void Cancel(String ip,String url,Map param) {
        int id = Integer.parseInt((String)param.get("ID"));
        
        Auction auction = plugin.dataQueries.getAuction(id);
        
        String player = auction.getPlayerName();
        Integer cancelItemId = auction.getItemStack().getTypeId();
        Short cancelItemDamage = auction.getItemStack().getDurability();
        
        List<Auction> auctions = plugin.dataQueries.getItem(player,cancelItemId,cancelItemDamage, true, plugin.Myitems);
        
        if(!auctions.isEmpty()) {
            
            Integer newAmount = auction.getItemStack().getAmount() + auctions.get(0).getItemStack().getAmount();
            Integer itemId = auctions.get(0).getId();
            plugin.dataQueries.updateItemQuantity(newAmount,itemId);
            plugin.dataQueries.DeleteAuction(id);
            
            
        }else{
            plugin.dataQueries.updateTable(id, plugin.Myitems);
        }
        Print("Cancel Done.","text/plain");
    }
    
    public void Buy(String ip,String url,Map param) {
       try { 
           int qtd =  Integer.parseInt((String)param.get("Quantity"));
           int id =  Integer.parseInt((String)param.get("ID"));
           
           AuctionPlayer ap = WebPortal.AuthPlayers.get(ip).AuctionPlayer;
           Auction au = plugin.dataQueries.getAuction(id);
           String item_name = GetItemConfig(au.getItemStack())[0];
           if(qtd <= 0)
           {
              Print("Quantity greater then 0","text/plain");
           } else if(qtd > au.getItemStack().getAmount())
           {
              Print("You are attempting to purchase more than the maximum available","text/plain");
           } else if(!plugin.economy.has(ap.getName(),au.getPrice() * qtd))
           {
              Print("You do not have enough money.","text/plain");
           } else if(ap.getName().equals(au.getPlayerName())) {
              Print("You cannnot buy your own items.","text/plain");
           } else {
               tr = new TradeSystem(plugin);
               Print(tr.Buy(ap.getName(),au, qtd, item_name, false),"text/plain");
           }
       }catch(Exception ex){
           WebPortal.logger.warning(ex.getMessage());
       }
        
    }
}
