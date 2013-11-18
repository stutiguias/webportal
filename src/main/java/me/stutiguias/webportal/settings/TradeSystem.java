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
    
    public String Buy(String BuyPlayerName,Shop sellerauction,int qtd,String item_name,Boolean ingame) {
        boolean found = false;
        int StackId = 0;
        int Stackqtd = 0;
        plugin.economy.withdrawPlayer(BuyPlayerName, sellerauction.getPrice() * qtd);
        plugin.economy.depositPlayer(sellerauction.getPlayerName(), sellerauction.getPrice() * qtd);
        plugin.dataQueries.setAlert(sellerauction.getPlayerName(), qtd, sellerauction.getPrice(), BuyPlayerName, item_name);
        // wrong player get items
        List<Shop> auctions = plugin.dataQueries.getPlayerItems(BuyPlayerName);
        for (Shop auction:auctions) {

            String playeritemname =  info.GetItemConfig(auction.getItemStack())[0];
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
            String searchtype = info.GetSearchType(sellerauction.getItemStack());
            plugin.dataQueries.createItem(sellerauction.getItemStack().getTypeId(), sellerauction.getItemStack().getDurability() , BuyPlayerName, qtd, 0.0, sellerauction.getEnchantments(), plugin.Myitems,Type,searchtype);
        }
        
        if(sellerauction.getPlayerName().equalsIgnoreCase("Server") && sellerauction.getItemStack().getAmount() == 9999 ){
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
    
    public String Sell(String sellerPlayerName,Shop buyauction,int qtd,String item_name,Boolean ingame) {
        boolean found = false;
        int StackId = 0;
        int Stackqtd = 0;
        plugin.economy.withdrawPlayer(buyauction.getPlayerName(), buyauction.getPrice() * qtd);
        plugin.economy.depositPlayer(sellerPlayerName, buyauction.getPrice() * qtd);
        
        // TODO: Alert to Withlist
        //plugin.dataQueries.setAlert(sellerauction.getPlayerName(), qtd, sellerauction.getPrice(), sellerPlayerName, item_name);
        
        boolean playerHasThatItem = false;    
        List<Shop> auctions = plugin.dataQueries.getPlayerItems(sellerPlayerName);
        for (Shop auction:auctions) {

            String playeritemname =  info.GetItemConfig(auction.getItemStack())[0];
            if(item_name.equals(playeritemname) && auction.getDamage() == buyauction.getItemStack().getDurability())
            {
                if(buyauction.getEnchantments().equals(auction.getEnchantments()) && auction.getQuantity() >= buyauction.getQuantity())
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
            return "You don't have that item or quantity";
        
        // wrong player get items
        auctions = plugin.dataQueries.getPlayerItems(buyauction.getPlayerName());
        for (Shop auction:auctions) {

            String playeritemname =  info.GetItemConfig(auction.getItemStack())[0];
            if(item_name.equals(playeritemname) && auction.getDamage() == buyauction.getItemStack().getDurability())
            {
                if(buyauction.getEnchantments().equals(auction.getEnchantments()))
                {
                    found = true;
                    StackId = auction.getId();
                    Stackqtd = auction.getQuantity();
                }
            }
        }
        
        if(ingame) {
            Player _player = plugin.getServer().getPlayer(buyauction.getPlayerName());
            ItemStack itemstack = new ItemStack(buyauction.getItemStack());
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
            String Type = buyauction.getItemStack().getType().toString();
            String searchtype = info.GetSearchType(buyauction.getItemStack());
            plugin.dataQueries.createItem(buyauction.getItemStack().getTypeId(), buyauction.getItemStack().getDurability() , buyauction.getPlayerName(), qtd, 0.0, buyauction.getEnchantments(), plugin.Myitems,Type,searchtype);
        }
        
        if(buyauction.getPlayerName().equalsIgnoreCase("Server") && buyauction.getItemStack().getAmount() == 9999 ){
            return "You sell "+ qtd +" " + item_name + " to "+ buyauction.getPlayerName() +" for " + buyauction.getPrice();
        }
        
        if(buyauction.getItemStack().getAmount() > 0) {
            if((buyauction.getItemStack().getAmount() - qtd) > 0)
            {
                plugin.dataQueries.UpdateItemAuctionQuantity(buyauction.getItemStack().getAmount() - qtd, buyauction.getId());
            }else{
                plugin.dataQueries.DeleteAuction(buyauction.getId());
            }
        }

        //int time = (int) ((System.currentTimeMillis() / 1000));
        //plugin.dataQueries.LogSellPrice(buyauction.getItemStack().getTypeId(),buyauction.getItemStack().getDurability(),time, sellerPlayerName, buyauction.getPlayerName(), qtd, buyauction.getPrice(), buyauction.getEnchantments());
        return "You sell "+ qtd +" " + item_name + " to "+ buyauction.getPlayerName() +" for " + buyauction.getPrice();
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
