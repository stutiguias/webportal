/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.settings;

import java.util.List;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.init.Util;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Daniel
 */
public class TradeSystem extends Util {

    public TradeSystem(WebPortal plugin){
        super(plugin);
    }
    
    public String Buy(String BuyPlayerName,Shop itemSold,int qtd,Boolean ingame) {
        
        String soldItemName = itemSold.getItemStack().getName();
        
        plugin.economy.withdrawPlayer(BuyPlayerName, itemSold.getPrice() * qtd);
        plugin.economy.depositPlayer(itemSold.getPlayerName(), itemSold.getPrice() * qtd);
        
        plugin.db.setAlert(itemSold.getPlayerName(), qtd, itemSold.getPrice(), BuyPlayerName, soldItemName);
        
        GivePlayerItem(BuyPlayerName,itemSold,qtd);
        
        if(itemSold.getPlayerName().equalsIgnoreCase("Server") && itemSold.getItemStack().getAmount() == 9999 ){
            return WebPortal.Messages.WebYouPurchase
                    .replaceAll("%qtd%",String.valueOf(qtd))
                    .replaceAll("%item_name%",soldItemName)
                    .replaceAll("%playerName%",itemSold.getPlayerName())
                    .replaceAll("%price%",String.valueOf(itemSold.getPrice()));
        }
        
        if(itemSold.getItemStack().getAmount() > 0) {
            if((itemSold.getItemStack().getAmount() - qtd) > 0)
            {
                plugin.db.UpdateItemAuctionQuantity(itemSold.getItemStack().getAmount() - qtd, itemSold.getId());
            }else{
                plugin.db.DeleteAuction(itemSold.getId());
            }
        }

        int time = (int) ((System.currentTimeMillis() / 1000));
        plugin.db.LogSellPrice(itemSold.getItemStack().getTypeId(),itemSold.getItemStack().getDurability(),time, BuyPlayerName, itemSold.getPlayerName(), qtd, itemSold.getPrice(), itemSold.getEnchantments());
        
        return WebPortal.Messages.WebYouPurchase
                    .replaceAll("%qtd%",String.valueOf(qtd))
                    .replaceAll("%item_name%",soldItemName)
                    .replaceAll("%playerName%",itemSold.getPlayerName())
                    .replaceAll("%price%",String.valueOf(itemSold.getPrice()));
    }
    
    public String Sell(String sellerPlayerName,Shop itemBuy,int qtd,Boolean ingame) {
        
        plugin.economy.withdrawPlayer(itemBuy.getPlayerName(), itemBuy.getPrice() * qtd);
        plugin.economy.depositPlayer(sellerPlayerName, itemBuy.getPrice() * qtd);
        String item_name = itemBuy.getItemStack().getName();
        
        // TODO: Alert to Withlist
        //plugin.db.setAlert(sellerauction.getPlayerName(), qtd, sellerauction.getPrice(), sellerPlayerName, item_name);
        
        boolean playerHasThatItem = false;    
        List<Shop> shops = plugin.db.getPlayerItems(sellerPlayerName);
        
        for (Shop item:shops) {
            
            if(isItemEquals(itemBuy, item))
            {
                playerHasThatItem = true;
                
                if(item.getQuantity() - qtd > 0)
                    plugin.db.updateItemQuantity(item.getQuantity() - qtd, item.getId() );
                else
                    plugin.db.DeleteAuction(item.getId());
                
                break;
            }
        }
        
        if(!playerHasThatItem) return WebPortal.Messages.WebFailDontHave;
        
        GivePlayerItem(itemBuy.getPlayerName(),itemBuy, qtd);
        
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
                plugin.db.UpdateItemAuctionQuantity(itemBuy.getItemStack().getAmount() - qtd, itemBuy.getId());
            }else{
                plugin.db.DeleteAuction(itemBuy.getId());
            }
        }

        //int time = (int) ((System.currentTimeMillis() / 1000));
        //plugin.db.LogSellPrice(buyauction.getItemStack().getTypeId(),buyauction.getItemStack().getDurability(),time, sellerPlayerName, buyauction.getPlayerName(), qtd, buyauction.getPrice(), buyauction.getEnchantments());
        return WebPortal.Messages.WebYouSell
                    .replaceAll("%qtd%",String.valueOf(qtd))
                    .replaceAll("%item_name%",item_name)
                    .replaceAll("%playerName%",itemBuy.getPlayerName())
                    .replaceAll("%price%",String.valueOf(itemBuy.getPrice()));
    }
        
    public void ItemtoStore(ItemStack item, Player player) {
        ItemtoStore( Convert(item) , player);
    }
    
    public void ItemtoStore(WebItemStack stack,Player player){
        
        int itemDamage = getDurability(stack);
        String enchants = stack.GetEnchants();
        int quantityInt = stack.getAmount();
                
        List<Shop> shops = plugin.db.getItem(player.getName(), stack.getTypeId(), itemDamage, false,plugin.Myitems);
        
        Boolean foundMatch = false;

        for (Shop shop : shops) {

            int itemId = shop.getId();
            
            if( stack.hasItemMeta() && !isMetaEqual(stack, shop) ) continue;
                
            if (isEnchantsEqual(enchants, shop)) {
                int currentQuantity = shop.getQuantity();
                currentQuantity += quantityInt;
                plugin.db.updateItemQuantity(currentQuantity, itemId);
                foundMatch = true;
                break;
            }
        }

        if (foundMatch == true) return;
            
        String type = stack.getType().toString();
        String searchtype = stack.GetSearchType();
        int createdId = plugin.db.createItem(stack.getTypeId(), itemDamage, player.getName(), quantityInt, 0.0,enchants,1,type,searchtype);

        if( WebPortal.AllowMetaItem && stack.hasItemMeta() && stack.getType() != Material.ENCHANTED_BOOK ) {
           String ItemMeta = stack.GetMeta();
           plugin.db.InsertItemInfo(createdId,"meta", ItemMeta);
        }
        
    }
       
    public boolean isMetaEqual(WebItemStack item,Shop shop) {
        String shopMeta = plugin.db.GetItemInfo(shop.getId(),"meta");
        String itemMeta = item.GetMeta();
        return shopMeta.equalsIgnoreCase(itemMeta);
    }

    public int getDurability(ItemStack itemstack) {
        if (itemstack.getDurability() >= 0) {
            return itemstack.getDurability();
        }else{
            return 0;
        }
    }

    public boolean isEnchantsEqual(String enchants,Shop auction) {
        return enchants.equals(auction.getEnchantments()) || ( enchants.isEmpty() && auction.getEnchantments().isEmpty() );
    }

    private boolean isItemEquals(Shop from,Shop to) {
        return  from.getItemStack().getName().equals(to.getItemStack().getName()) 
                && to.getDamage() == from.getItemStack().getDurability()
                && from.getEnchantments().equals(to.getEnchantments());
    }
    
    
    private boolean GivePlayerItem(String player,Shop itemShop,int qtd) {
        
        List<Shop> shops = plugin.db.getPlayerItems(player);
        boolean found = false;
        int existId = 0;
        int existQtd = 0;
        
        for (Shop item:shops) {

            if(isItemEquals(itemShop, item))
            {
                found = true;
                existId = item.getId();
                existQtd = item.getQuantity();
            }
            
        }
        
        boolean ingame = plugin.getServer().getPlayer(player) != null;
        
        if(ingame) {
            AddItemToPlayer(player, itemShop, qtd);
        }else if(found && !ingame) {
            plugin.db.updateItemQuantity(existQtd + qtd, existId);
        }else if(!ingame) {
            String Type = itemShop.getItemStack().getType().toString();
            String searchtype = itemShop.getItemStack().GetSearchType();
            plugin.db.createItem(itemShop.getItemStack().getTypeId(), itemShop.getItemStack().getDurability() , player, qtd, 0.0, itemShop.getEnchantments(), plugin.Myitems,Type,searchtype);
        }
        return true;
        
    }
    
    private boolean AddItemToPlayer(String player,Shop shop,int qtd) {
        Player _player = plugin.getServer().getPlayer(player);
        ItemStack itemstack = new ItemStack(shop.getItemStack());
        itemstack.setAmount(qtd);
        
        if (itemstack.getMaxStackSize() == 1) {
            ItemStack NewStack = new ItemStack(itemstack);
            NewStack.setAmount(1);
            for (int i = 0; i < itemstack.getAmount(); i++) {
                _player.getInventory().addItem(NewStack);
            }
        } else {
            _player.getInventory().addItem(itemstack);
        }
        
        _player.updateInventory();
        return true;
    }
}
