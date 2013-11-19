/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.settings;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.information.Info;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Daniel
 */
public class TradeSystem {
    
    private WebPortal plugin;
    private Info info;
    
    public TradeSystem(WebPortal plugin){
        this.plugin = plugin;
        info = new Info(plugin);
    }
    
    public String Buy(String BuyPlayerName,Shop itemSold,int qtd,String item_name,Boolean ingame) {
        boolean found = false;
        int StackId = 0;
        int Stackqtd = 0;
        plugin.economy.withdrawPlayer(BuyPlayerName, itemSold.getPrice() * qtd);
        plugin.economy.depositPlayer(itemSold.getPlayerName(), itemSold.getPrice() * qtd);
        plugin.dataQueries.setAlert(itemSold.getPlayerName(), qtd, itemSold.getPrice(), BuyPlayerName, item_name);
        // wrong player get items
        List<Shop> auctions = plugin.dataQueries.getPlayerItems(BuyPlayerName);
        for (Shop auction:auctions) {

            String playeritemname =  info.GetItemConfig(auction.getItemStack())[0];
            if(item_name.equals(playeritemname) && auction.getDamage() == itemSold.getItemStack().getDurability())
            {
                if(itemSold.getEnchantments().equals(auction.getEnchantments()))
                {
                    found = true;
                    StackId = auction.getId();
                    Stackqtd = auction.getQuantity();
                }
            }
        }
        if(ingame) {
            Player _player = plugin.getServer().getPlayer(BuyPlayerName);
            ItemStack itemstack = new ItemStack(itemSold.getItemStack());
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
            String Type = itemSold.getItemStack().getType().toString();
            String searchtype = info.GetSearchType(itemSold.getItemStack());
            plugin.dataQueries.createItem(itemSold.getItemStack().getTypeId(), itemSold.getItemStack().getDurability() , BuyPlayerName, qtd, 0.0, itemSold.getEnchantments(), plugin.Myitems,Type,searchtype);
        }
        
        if(itemSold.getPlayerName().equalsIgnoreCase("Server") && itemSold.getItemStack().getAmount() == 9999 ){
            return WebPortal.Messages.WebYouPurchase
                    .replaceAll("%qtd%",String.valueOf(qtd))
                    .replaceAll("%item_name%",item_name)
                    .replaceAll("%playerName%",itemSold.getPlayerName())
                    .replaceAll("%price%",String.valueOf(itemSold.getPrice()));
        }
        
        if(itemSold.getItemStack().getAmount() > 0) {
            if((itemSold.getItemStack().getAmount() - qtd) > 0)
            {
                plugin.dataQueries.UpdateItemAuctionQuantity(itemSold.getItemStack().getAmount() - qtd, itemSold.getId());
            }else{
                plugin.dataQueries.DeleteAuction(itemSold.getId());
            }
        }

        int time = (int) ((System.currentTimeMillis() / 1000));
        plugin.dataQueries.LogSellPrice(itemSold.getItemStack().getTypeId(),itemSold.getItemStack().getDurability(),time, BuyPlayerName, itemSold.getPlayerName(), qtd, itemSold.getPrice(), itemSold.getEnchantments());
        
        return WebPortal.Messages.WebYouPurchase
                    .replaceAll("%qtd%",String.valueOf(qtd))
                    .replaceAll("%item_name%",item_name)
                    .replaceAll("%playerName%",itemSold.getPlayerName())
                    .replaceAll("%price%",String.valueOf(itemSold.getPrice()));
    }
    
    public String Sell(String sellerPlayerName,Shop itemBuy,int qtd,String item_name,Boolean ingame) {
        boolean found = false;
        int StackId = 0;
        int Stackqtd = 0;
        plugin.economy.withdrawPlayer(itemBuy.getPlayerName(), itemBuy.getPrice() * qtd);
        plugin.economy.depositPlayer(sellerPlayerName, itemBuy.getPrice() * qtd);
        
        // TODO: Alert to Withlist
        //plugin.dataQueries.setAlert(sellerauction.getPlayerName(), qtd, sellerauction.getPrice(), sellerPlayerName, item_name);
        
        boolean playerHasThatItem = false;    
        List<Shop> auctions = plugin.dataQueries.getPlayerItems(sellerPlayerName);
        for (Shop auction:auctions) {

            String playeritemname =  info.GetItemConfig(auction.getItemStack())[0];
            if(item_name.equals(playeritemname) && auction.getDamage() == itemBuy.getItemStack().getDurability())
            {
                if(itemBuy.getEnchantments().equals(auction.getEnchantments()) && auction.getQuantity() >= itemBuy.getQuantity())
                {
                    playerHasThatItem = true;
                    StackId = auction.getId();
                    Stackqtd = auction.getQuantity();
                    if(Stackqtd - qtd > 0)
                        plugin.dataQueries.updateItemQuantity(Stackqtd - qtd, StackId);
                    else
                        plugin.dataQueries.DeleteAuction(StackId);
                }
            }
        }
        if(!playerHasThatItem)
            return WebPortal.Messages.WebFailDontHave;
        
        // wrong player get items
        auctions = plugin.dataQueries.getPlayerItems(itemBuy.getPlayerName());
        for (Shop auction:auctions) {

            String playeritemname =  info.GetItemConfig(auction.getItemStack())[0];
            if(item_name.equals(playeritemname) && auction.getDamage() == itemBuy.getItemStack().getDurability())
            {
                if(itemBuy.getEnchantments().equals(auction.getEnchantments()))
                {
                    found = true;
                    StackId = auction.getId();
                    Stackqtd = auction.getQuantity();
                }
            }
        }
        
        if(ingame) {
            Player _player = plugin.getServer().getPlayer(itemBuy.getPlayerName());
            ItemStack itemstack = new ItemStack(itemBuy.getItemStack());
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
            String Type = itemBuy.getItemStack().getType().toString();
            String searchtype = info.GetSearchType(itemBuy.getItemStack());
            plugin.dataQueries.createItem(itemBuy.getItemStack().getTypeId(), itemBuy.getItemStack().getDurability() , itemBuy.getPlayerName(), qtd, 0.0, itemBuy.getEnchantments(), plugin.Myitems,Type,searchtype);
        }
        
        if(itemBuy.getPlayerName().equalsIgnoreCase("Server") && itemBuy.getItemStack().getAmount() == 9999 ){
            return WebPortal.Messages.WebYouSell
                    .replaceAll("%qtd%",String.valueOf(qtd))
                    .replaceAll("%item_name%",item_name)
                    .replaceAll("%playerName%",itemBuy.getPlayerName())
                    .replaceAll("%price%",String.valueOf(itemBuy.getPrice()));
        }
        
        if(itemBuy.getItemStack().getAmount() > 0) {
            if((itemBuy.getItemStack().getAmount() - qtd) > 0)
            {
                plugin.dataQueries.UpdateItemAuctionQuantity(itemBuy.getItemStack().getAmount() - qtd, itemBuy.getId());
            }else{
                plugin.dataQueries.DeleteAuction(itemBuy.getId());
            }
        }

        //int time = (int) ((System.currentTimeMillis() / 1000));
        //plugin.dataQueries.LogSellPrice(buyauction.getItemStack().getTypeId(),buyauction.getItemStack().getDurability(),time, sellerPlayerName, buyauction.getPlayerName(), qtd, buyauction.getPrice(), buyauction.getEnchantments());
        return WebPortal.Messages.WebYouSell
                    .replaceAll("%qtd%",String.valueOf(qtd))
                    .replaceAll("%item_name%",item_name)
                    .replaceAll("%playerName%",itemBuy.getPlayerName())
                    .replaceAll("%price%",String.valueOf(itemBuy.getPrice()));
    }
    
    public static double round(double unrounded, int precision, int roundingMode) {
            BigDecimal bd = new BigDecimal(unrounded);
            BigDecimal rounded = bd.setScale(precision, roundingMode);
            return rounded.doubleValue();
    }
    
    public void ItemtoStore(ItemStack stack,Player player){
        
        int itemDamage = getDurability(stack);
        String enchants = ConvertEnchantsToStringCSV(stack);
        int quantityInt = stack.getAmount();
                
        List<Shop> auctions = plugin.dataQueries.getItem(player.getName(), stack.getTypeId(), itemDamage, false,plugin.Myitems);
        
        Boolean foundMatch = false;

        for (Shop auction : auctions) {

            int itemTableIdNumber = auction.getId();
            
            if( stack.hasItemMeta() && !isMetaEqual(stack, auction) ) continue;
                
            if (isEnchantsEqual(enchants, auction) && !foundMatch) {
                int currentQuantity = auction.getQuantity();
                currentQuantity += quantityInt;
                plugin.dataQueries.updateItemQuantity(currentQuantity, itemTableIdNumber);
                foundMatch = true;
            }
        }

        if (foundMatch == false) {
            
                String type = stack.getType().toString();
                String searchtype = info.GetSearchType(stack);
                int createdId = plugin.dataQueries.createItem(stack.getTypeId(), itemDamage, player.getName(), quantityInt, 0.0,enchants,1,type,searchtype);
                
                if( plugin.AllowMetaItem && stack.hasItemMeta() && stack.getType() != Material.ENCHANTED_BOOK ) {
                   String ItemMeta = ConvertItemMetaToStringCSV(stack);
                   plugin.dataQueries.InsertItemInfo(createdId,"meta", ItemMeta);
                }
        }
        
    }
    
    
    public boolean isMetaEqual(ItemStack item,Shop auction) {
        String auctionMeta = plugin.dataQueries.GetItemInfo(auction.getId(),"meta");
        String itemMeta = ConvertItemMetaToStringCSV(item);
        return auctionMeta.equalsIgnoreCase(itemMeta);
    }
    
    public String ConvertItemMetaToStringCSV(ItemStack item) {
        
        ItemMeta meta = item.getItemMeta();
        StringBuilder ItemName = new StringBuilder();
        if( meta.hasDisplayName() )  {
            ItemName.append("N[#$]").append(meta.getDisplayName());
        }
        
        if( meta.hasLore() ) {
            for (int i = 0; i < meta.getLore().size(); i++) {
                ItemName.append(",").append("L[#$]").append(meta.getLore().get(i));
            }
        }
        
        return ItemName.toString();
    }

    
    public int getDurability(ItemStack itemstack) {
        if (itemstack.getDurability() >= 0) {
            return itemstack.getDurability();
        }else{
            return 0;
        }
    }
    
    public String ConvertEnchantsToStringCSV(ItemStack itemstack) {
        Map<Enchantment, Integer> itemEnchantments;
        String enchants = "";
        
        if(itemstack.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta bookmeta = (EnchantmentStorageMeta)itemstack.getItemMeta();
            itemEnchantments = bookmeta.getStoredEnchants();
        }else{
            itemEnchantments = itemstack.getEnchantments();
        }
        
        for (Map.Entry<Enchantment, Integer> entry : itemEnchantments.entrySet()) {
            int enchId = entry.getKey().getId();
            int level = entry.getValue();
            enchants += enchId + "," + level + ":";
        }
        return enchants;
    }
    
    public boolean isEnchantsEqual(String enchants,Shop auction) {
        if( enchants.equals(auction.getEnchantments()) || 
          ( enchants.isEmpty() && auction.getEnchantments().isEmpty() )) {
           return true;
        }else{
           return false;
        }
    }

}
