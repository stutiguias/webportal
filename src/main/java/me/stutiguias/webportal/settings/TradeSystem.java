/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.settings;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.webserver.Material;
import org.bukkit.enchantments.Enchantment;
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
            if(itemstack.getMaxStackSize() == 1) {
                ItemStack NewStack = new ItemStack(itemstack);
                NewStack.setAmount(1);
                for (int i = 0; i < itemstack.getAmount(); i++) {
                   _player.getInventory().addItem(NewStack);
                }
            }else{
                _player.getInventory().addItem(itemstack);  
            }
            _player.updateInventory();
        }else if(found && !ingame) {
            plugin.dataQueries.updateItemQuantity(Stackqtd + qtd, StackId);
        }else if(!ingame) {
            String Type = sellerauction.getItemStack().getType().toString();
            String ItemName = Material.getItemName(sellerauction.getItemStack().getTypeId(), sellerauction.getItemStack().getDurability());
            String searchtype = plugin.getSearchType(ItemName);
            plugin.dataQueries.createItem(sellerauction.getItemStack().getTypeId(), sellerauction.getItemStack().getDurability() , BuyPlayerName, qtd, 0.0, sellerauction.getEnchantments(), plugin.Myitems,Type,ItemName,searchtype);
        }
        
        if(sellerauction.getPlayerName().equalsIgnoreCase("Server")){
            return "You purchased "+ qtd +" " + item_name + " from "+ sellerauction.getPlayerName() +" for " + sellerauction.getPrice();
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
    
    public static double round(double unrounded, int precision, int roundingMode) {
            BigDecimal bd = new BigDecimal(unrounded);
            BigDecimal rounded = bd.setScale(precision, roundingMode);
            return rounded.doubleValue();
    }
    
    public void ItemtoStore(ItemStack stack,Player player){
        
        int itemDamage = getDurability(stack);
        String enchants = getEnchants(stack);
        int quantityInt = stack.getAmount();
                
        List<Auction> auctions = plugin.dataQueries.getItem(player.getName(), stack.getTypeId(), itemDamage, false,plugin.Myitems);
        
        Boolean foundMatch = false;
        
        for (Auction auction : auctions) {
            
                int itemTableIdNumber = auction.getId();

                if (isEnchantsEqual(enchants,auction) && !foundMatch ) {
                        int currentQuantity = auction.getQuantity();
                        currentQuantity += quantityInt;
                        plugin.dataQueries.updateItemQuantity(currentQuantity, itemTableIdNumber);
                        foundMatch = true;
                }
        }

        if (foundMatch == false) {
                String type = stack.getType().toString();
                String ItemName = Material.getItemName(stack.getTypeId(),stack.getDurability());
                String searchtype = plugin.getSearchType(ItemName);
                plugin.dataQueries.createItem(stack.getTypeId(), itemDamage, player.getName(), quantityInt, 0.0,enchants,1,type,ItemName,searchtype);
        }
        
    }
    
    public int getDurability(ItemStack itemstack) {
        if (itemstack.getDurability() >= 0) {
            return itemstack.getDurability();
        }else{
            return 0;
        }
    }
    
    public String getEnchants(ItemStack itemstack) {
        Map<Enchantment, Integer> itemEnchantments = itemstack.getEnchantments();
        String enchants = "";
        for (Map.Entry<Enchantment, Integer> entry : itemEnchantments.entrySet()) {
            int enchId = entry.getKey().getId();
            int level = entry.getValue();
            enchants += enchId + "," + level + ":";
        }
        return enchants;
    }
    
    public boolean isEnchantsEqual(String enchants,Auction auction) {
        if( enchants.equals(auction.getEnchantments()) || 
          ( enchants.isEmpty() && auction.getEnchantments().isEmpty() )) {
           return true;
        }else{
           return false;
        }
    }

}
