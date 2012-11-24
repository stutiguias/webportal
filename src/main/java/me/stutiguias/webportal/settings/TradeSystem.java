/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.settings;

import java.util.List;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.webserver.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Daniel
 */
public class TradeSystem {
    
    public WebPortal plugin;
    
    public TradeSystem(WebPortal plugin){
        this.plugin = plugin;
    }
    
    public String Buy(String BuyPlayerName,Auction sellerauction,int qtd,String item_name,Boolean ingame) {
        boolean found = false;
        int StackId = 0;
        int Stackqtd = 0;
        plugin.economy.withdrawPlayer(BuyPlayerName, sellerauction.getPrice() * qtd);
        plugin.economy.depositPlayer(sellerauction.getPlayerName(), sellerauction.getPrice() * qtd);
        plugin.dataQueries.setAlert(sellerauction.getPlayerName(), qtd, sellerauction.getPrice(), BuyPlayerName, item_name);
        // wrong player get items
        List<Auction> auctions = plugin.dataQueries.getPlayerItems(BuyPlayerName);
        for (Auction auction:auctions) {

            String playeritemname =  Material.getItemName(auction.getName(),(short)(auction.getDamage()));
            if(item_name.equals(playeritemname) && auction.getDamage() == sellerauction.getItemStack().getDurability())
            {
                if(sellerauction.getEnchantments().equals(auction.getEnchantments()))
                {
                    found = true;
                    StackId = auction.getId();
                    Stackqtd = auction.getQuantity();
                }
            }
        }
        if(ingame) {
            Player _player = plugin.getServer().getPlayer(BuyPlayerName);
            ItemStack itemstack = new ItemStack(sellerauction.getItemStack());
            itemstack.setAmount(qtd);
            _player.getInventory().addItem(itemstack);  
            _player.updateInventory();
        }else if(found && !ingame) {
            plugin.dataQueries.updateItemQuantity(Stackqtd + qtd, StackId);
        }else if(!ingame) {
            String Type = sellerauction.getItemStack().getType().toString();
            String ItemName = Material.getItemName(sellerauction.getItemStack().getTypeId(), sellerauction.getItemStack().getDurability());
            String searchtype = plugin.getSearchType(ItemName);
            plugin.dataQueries.createItem(sellerauction.getItemStack().getTypeId(), sellerauction.getItemStack().getDurability() , BuyPlayerName, qtd, 0.0, sellerauction.getEnchantments(), plugin.Myitems,Type,ItemName,searchtype);
        }

        if(sellerauction.getItemStack().getAmount() > 0) {
            if((sellerauction.getItemStack().getAmount() - qtd) > 0)
            {
                plugin.dataQueries.UpdateItemAuctionQuantity(sellerauction.getItemStack().getAmount() - qtd, sellerauction.getId());
            }else{
                plugin.dataQueries.DeleteAuction(sellerauction.getId());
            }
        }

        int time = (int) ((System.currentTimeMillis() / 1000));
        plugin.dataQueries.LogSellPrice(sellerauction.getItemStack().getTypeId(),sellerauction.getItemStack().getDurability(),time, BuyPlayerName, sellerauction.getPlayerName(), qtd, sellerauction.getPrice(), sellerauction.getEnchantments());
        return "You purchased "+ qtd +" " + item_name + " from "+ sellerauction.getPlayerName() +" for " + sellerauction.getPrice();
    }
}
