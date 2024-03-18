/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.stutiguias.webportal.trade;

import java.util.List;
import java.util.UUID;

import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.model.Shop;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Daniel
 */

// TODO : Change logic get all shop from and do equals ( BUY )
// TODO : Change logic get all shop from and do equals ( SELL )
@SuppressWarnings("UnstableApiUsage")
public class Transaction extends TradeHandle {

    public Transaction(WebPortal plugin) {
        super(plugin);
    }

    public String Buy(String BuyPlayerName,Shop itemSold,int qtd) {

        UUID uuidBuyer = UUID.fromString(plugin.db.getPlayer(BuyPlayerName).getUUID());
        OfflinePlayer offlinePlayerBuyer = plugin.getServer().getOfflinePlayer(uuidBuyer);
        plugin.economy.withdrawPlayer(offlinePlayerBuyer, itemSold.getPrice() * qtd);

        if(!itemSold.getPlayerName().equals("Server")) {
            UUID uuidSeller = UUID.fromString(plugin.db.getPlayer(itemSold.getPlayerName()).getUUID());
            OfflinePlayer offlinePlayerSeller = plugin.getServer().getOfflinePlayer(uuidSeller);
            plugin.economy.depositPlayer(offlinePlayerSeller, itemSold.getPrice() * qtd);
            plugin.db.setAlert(itemSold.getPlayerName(), qtd, itemSold.getPrice(), BuyPlayerName, itemSold.getItemStack().getName() );
        }

        GivePlayerItem(BuyPlayerName,itemSold,qtd);
        if(isServerInfinity(itemSold)) return BuyMsg(itemSold, qtd);
        UpdateItemOnShop(itemSold, qtd);

        int time = (int) ((System.currentTimeMillis() / 1000));
        plugin.db.LogSellPrice(itemSold.getItemStack().getType().name(),getDmg(itemSold.getItemStack()),time, BuyPlayerName, itemSold.getPlayerName(), qtd, itemSold.getPrice(), itemSold.getEnchantments());
        
        return BuyMsg(itemSold, qtd);
    }

    public String Sell(String sellerPlayerName,Shop itemBuy,int qtd) {

        UUID uuidBuyer = UUID.fromString(plugin.db.getPlayer(itemBuy.getPlayerName()).getUUID());
        UUID uuidSeller = UUID.fromString(plugin.db.getPlayer(sellerPlayerName).getUUID());
        OfflinePlayer offlinePlayerBuyer = plugin.getServer().getOfflinePlayer(uuidBuyer);
        OfflinePlayer offlinePlayerSeller = plugin.getServer().getOfflinePlayer(uuidSeller);

        plugin.economy.withdrawPlayer(offlinePlayerBuyer, itemBuy.getPrice() * qtd);
        plugin.economy.depositPlayer(offlinePlayerSeller, itemBuy.getPrice() * qtd);

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
        if(isServerInfinity(itemBuy)) return SellMsg(itemBuy, qtd);
        UpdateItemOnShop(itemBuy, qtd);
        return  SellMsg(itemBuy, qtd);
    }

    private void UpdateItemOnShop(Shop item, int qtd) {
        if(item.getItemStack().getAmount() > 0) {
            if((item.getItemStack().getAmount() - qtd) > 0)
            {
                plugin.db.UpdateItemAuctionQuantity(item.getItemStack().getAmount() - qtd, item.getId());
            }else{
                plugin.db.DeleteAuction(item.getId());
                plugin.db.DeleteInfo(item.getId());
            }
        }
    }

    private static boolean isServerInfinity(Shop itemSold) {
        return itemSold.getPlayerName().equalsIgnoreCase("Server")
                && itemSold.getItemStack().getAmount() == 9999;
    }

    private void GivePlayerItem(String player,Shop itemShop,int qtd) {
        
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
        
        boolean online = plugin.getServer().getPlayer(player) != null;
        
        if(online) {
            AddItemToPlayer(player, itemShop, qtd);
            return;
        }

        if(found){
            plugin.db.updateItemQuantity(existQtd + qtd, existId);
            return;
        }

        // if not online and not found item on store
        String Type = itemShop.getItemStack().getType().toString();
        String searchtype = itemShop.getItemStack().GetSearchType();
        String itemName = itemShop.getItemStack().getType().name();
        int newID = plugin.db.CreateItem(itemName, getDmg(itemShop) , player, qtd, 0.0, itemShop.getEnchantments(), plugin.Myitems,Type,searchtype);
        String meta = plugin.db.GetItemInfo(itemShop.getId(), "meta");
        if(meta.isEmpty()) return;
        plugin.db.InsertItemInfo(newID, "meta", meta);
    }
    
    private void AddItemToPlayer(String player,Shop shop,int qtd) {
        Player _player = plugin.getServer().getPlayer(player);
        if(_player == null) return;

        if(WebPortal.AllowMetaItem){
            String meta = plugin.db.GetItemInfo(shop.getId(),"meta");
            if(!meta.isEmpty()) {
               shop.getItemStack().SetMeta(meta);
            }
        }
                
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
    }
        
    protected boolean isItemEquals(Shop from,Shop to) {
        return  from.getItemStack().getName().equals(to.getItemStack().getName())
                && getDmg(from) == getDmg(to)
                && from.getEnchantments().equals(to.getEnchantments());
    }

    private int getDmg(Shop shop) {
        ItemMeta meta = shop.getItemStack().getItemMeta();
        return meta != null ? ((Damageable) meta).getDamage() : 0;
    }

    private int getDmg(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        return meta != null ? ((Damageable) meta).getDamage() : 0;
    }

    private String BuyMsg(Shop shop,int qtd) {
        return WebPortal.Messages.WebYouPurchase
                    .replaceAll("%qtd%",String.valueOf(qtd))
                    .replaceAll("%item_name%",shop.getItemStack().getName())
                    .replaceAll("%playerName%",shop.getPlayerName())
                    .replaceAll("%price%",String.valueOf(shop.getPrice()));
    }
    
    private String SellMsg(Shop shop,int qtd) {
        return WebPortal.Messages.WebYouSell
            .replaceAll("%qtd%",String.valueOf(qtd))
            .replaceAll("%item_name%",shop.getItemStack().getName())
            .replaceAll("%playerName%",shop.getPlayerName())
            .replaceAll("%price%",String.valueOf(shop.getPrice()));
    }
}
