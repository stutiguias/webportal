/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.settings;

import java.util.List;
import me.stutiguias.webportal.init.WebAuction;
import me.stutiguias.webportal.webserver.Material;

/**
 *
 * @author Daniel
 */
public class TradeSystem {
    
    public WebAuction plugin;
    
    public TradeSystem(WebAuction plugin){
        this.plugin = plugin;
    }
    
    public String Buy(String BuyPlayerName,Auction au,int qtd,String item_name) {
        boolean found = false;
        int StackId = 0;
        int Stackqtd = 0;
        plugin.economy.withdrawPlayer(BuyPlayerName, au.getPrice() * qtd);
        plugin.economy.depositPlayer(au.getPlayerName(), au.getPrice() * qtd);
        plugin.dataQueries.setAlert(au.getPlayerName(), au.getItemStack().getAmount(), au.getPrice(), BuyPlayerName, item_name);
        // wrong player get items
        List<AuctionItem> items = plugin.dataQueries.getPlayerItems(BuyPlayerName);
        for (AuctionItem item:items) {

            String playeritemname =  Material.getItemName(item.getName(),(short)(item.getDamage()));
            if(item_name.equals(playeritemname) && item.getDamage() == au.getItemStack().getDurability())
            {
                if(au.getEnch().equals(item.getEnchantments()))
                {
                    found = true;
                    StackId = item.getId();
                    Stackqtd = item.getQuantity();
                }
            }
        }
        if(found) {
            plugin.dataQueries.updateItemQuantity(Stackqtd + qtd, StackId);
        }else{
            String Type = au.getItemStack().getType().toString();
            String ItemName = Material.getItemName(au.getItemStack().getTypeId(), au.getItemStack().getDurability());
            String searchtype = plugin.getSearchType(ItemName);
            plugin.dataQueries.createItem(au.getItemStack().getTypeId(), au.getItemStack().getDurability() , BuyPlayerName, qtd, 0.0, au.getEnch(), plugin.Myitems,Type,ItemName,searchtype);
        }

        if(au.getItemStack().getAmount() > 0) {
            if((au.getItemStack().getAmount() - qtd) > 0)
            {
                plugin.dataQueries.UpdateItemAuctionQuantity(au.getItemStack().getAmount() - qtd, au.getId());
            }else{
                plugin.dataQueries.DeleteAuction(au.getId());
            }
        }

        int time = (int) ((System.currentTimeMillis() / 1000));
        plugin.dataQueries.LogSellPrice(au.getItemStack().getTypeId(),au.getItemStack().getDurability(),time, BuyPlayerName, au.getPlayerName(), qtd, au.getPrice(), au.getEnch());
        return "You purchased "+ qtd +" " + item_name + " from "+ au.getPlayerName() +" for " + au.getPrice();
    }
}
