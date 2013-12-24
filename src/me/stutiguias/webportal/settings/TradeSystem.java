/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.settings;

import java.util.List;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.information.Info;
import me.stutiguias.webportal.information.Util;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Daniel
 */
public class TradeSystem extends Util {
  
    private final Info info;
    
    public TradeSystem(WebPortal plugin){
        super(plugin);
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
    
    public void ItemtoStore(ItemStack stack,Player player){
        
        int itemDamage = getDurability(stack);
        String enchants = EnchantsToString(stack);
        int quantityInt = stack.getAmount();
                
        List<Shop> shops = plugin.dataQueries.getItem(player.getName(), stack.getTypeId(), itemDamage, false,plugin.Myitems);
        
        Boolean foundMatch = false;

        for (Shop shop : shops) {

            int itemId = shop.getId();
            
            if( stack.hasItemMeta() && !isMetaEqual(stack, shop) ) continue;
                
            if (isEnchantsEqual(enchants, shop)) {
                int currentQuantity = shop.getQuantity();
                currentQuantity += quantityInt;
                plugin.dataQueries.updateItemQuantity(currentQuantity, itemId);
                foundMatch = true;
                break;
            }
        }

        if (foundMatch == false) {
            
                String type = stack.getType().toString();
                String searchtype = info.GetSearchType(stack);
                int createdId = plugin.dataQueries.createItem(stack.getTypeId(), itemDamage, player.getName(), quantityInt, 0.0,enchants,1,type,searchtype);
                
                if( plugin.AllowMetaItem && stack.hasItemMeta() && stack.getType() != Material.ENCHANTED_BOOK ) {
                   String ItemMeta = ItemMetaToString(stack);
                   plugin.dataQueries.InsertItemInfo(createdId,"meta", ItemMeta);
                }
        }
        
    }
       
    public boolean isMetaEqual(ItemStack item,Shop shop) {
        String shopMeta = plugin.dataQueries.GetItemInfo(shop.getId(),"meta");
        String itemMeta = ItemMetaToString(item);
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

}
