/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.settings;

import java.util.List;
import me.stutiguias.webportal.init.WebAuction;
import me.stutiguias.webportal.webserver.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Daniel
 */
public class TradeSystem {
    
    public WebAuction plugin;
    
    public TradeSystem(WebAuction plugin){
        this.plugin = plugin;
    }
    
    public String Buy(String BuyPlayerName,Auction au,int qtd,String item_name,Boolean ingame) {
        boolean found = false;
        int StackId = 0;
        int Stackqtd = 0;
        plugin.economy.withdrawPlayer(BuyPlayerName, au.getPrice() * qtd);
        plugin.economy.depositPlayer(au.getPlayerName(), au.getPrice() * qtd);
        plugin.dataQueries.setAlert(au.getPlayerName(), qtd, au.getPrice(), BuyPlayerName, item_name);
        // wrong player get items
        List<Auction> auctions = plugin.dataQueries.getPlayerItems(BuyPlayerName);
        for (Auction auction:auctions) {

            String playeritemname =  Material.getItemName(auction.getName(),(short)(auction.getDamage()));
            if(item_name.equals(playeritemname) && auction.getDamage() == au.getItemStack().getDurability())
            {
                if(au.getEnchantments().equals(auction.getEnchantments()))
                {
                    found = true;
                    StackId = auction.getId();
                    Stackqtd = auction.getQuantity();
                }
            }
        }
        if(ingame) {
            Player _player = plugin.getServer().getPlayer(BuyPlayerName);
            ItemStack itemstack = new ItemStack(au.getItemStack());
            itemstack.setAmount(qtd);
            _player.getInventory().addItem(itemstack);  
            _player.updateInventory();
        }else if(found && !ingame) {
            plugin.dataQueries.updateItemQuantity(Stackqtd + qtd, StackId);
        }else if(!ingame) {
            String Type = au.getItemStack().getType().toString();
            String ItemName = Material.getItemName(au.getItemStack().getTypeId(), au.getItemStack().getDurability());
            String searchtype = plugin.getSearchType(ItemName);
            plugin.dataQueries.createItem(au.getItemStack().getTypeId(), au.getItemStack().getDurability() , BuyPlayerName, qtd, 0.0, au.getEnchantments(), plugin.Myitems,Type,ItemName,searchtype);
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
        plugin.dataQueries.LogSellPrice(au.getItemStack().getTypeId(),au.getItemStack().getDurability(),time, BuyPlayerName, au.getPlayerName(), qtd, au.getPrice(), au.getEnchantments());
        return "You purchased "+ qtd +" " + item_name + " from "+ au.getPlayerName() +" for " + au.getPrice();
    }
}
