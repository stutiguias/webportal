/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.signs;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.stutiguias.webportal.init.WebAuction;
import me.stutiguias.webportal.settings.Auction;
import me.stutiguias.webportal.settings.Auction;
import me.stutiguias.webportal.settings.AuctionMail;
import me.stutiguias.webportal.settings.AuctionMail;
import me.stutiguias.webportal.webserver.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Stutiguias
 */
public class Mailbox {
    
    private WebAuction plugin;
    
    public Mailbox(WebAuction plugin)
    {
       this.plugin = plugin;
    }
    
    public static double round(double unrounded, int precision, int roundingMode) {
            BigDecimal bd = new BigDecimal(unrounded);
            BigDecimal rounded = bd.setScale(precision, roundingMode);
            return rounded.doubleValue();
    }
 
    public void MailBoxOperationType(Player player,String Operation){
        if(Operation.equalsIgnoreCase("withdraw")) {
                MailBoxWithdraw(player);
        }else {
                MailBoxDeposit(player);
        }
    }
    
    private void MailBoxDeposit(Player player){
        if (plugin.permission.has(player.getWorld(),player.getName(), "wa.use.deposit.items")) {
                ItemStack stack = player.getItemInHand();
                if (stack != null) {
                        if (stack.getTypeId() != 0) {
                                ItemtoStore(stack,player);
                                player.sendMessage(plugin.logPrefix + plugin.parseColor(plugin.Messages.get("StackStored")));
                        }else{
                                player.sendMessage(plugin.logPrefix + plugin.parseColor(plugin.Messages.get("HoldHelp")));						
                        }
                }
               player.setItemInHand(null);
        }
    }
    
    private void MailBoxWithdraw(Player player) {
        if (plugin.permission.has(player.getWorld(),player.getName(), "wa.use.withdraw.items")) {
                try {
                        List<AuctionMail> auctionMail = plugin.dataQueries.getMail(player.getName());
                        boolean invFull = true;
                        boolean gotMail = false;
                        for (AuctionMail mail : auctionMail) {
                                if (player.getInventory().firstEmpty() != -1) {
                                        ItemStack stack = mail.getItemStack();
                                        plugin.dataQueries.deleteMail(mail.getId());
                                        if(stack.getMaxStackSize() == 1)
                                        {
                                            ItemStack is = new ItemStack(stack);
                                            is.setAmount(1);
                                            for(int i=1;i <= stack.getAmount();i++)
                                            {
                                                player.getInventory().addItem(is);    
                                            }
                                        }else{
                                           HashMap<Integer,ItemStack> notfit = player.getInventory().addItem(stack);
                                           if(!notfit.isEmpty()) {
                                               for (ItemStack notfitstack : notfit.values()) {
                                                 player.sendMessage(plugin.logPrefix + plugin.parseColor(plugin.Messages.get("InventoryFull")));
                                                 ItemtoStore(notfitstack, player);
                                               }
                                           }
                                        }
                                        player.updateInventory();
                                        gotMail = true;
                                        invFull = false;
                                } else {
                                        player.sendMessage(plugin.logPrefix + plugin.parseColor(plugin.Messages.get("InventoryFull")));
                                        invFull = true;
                                }
                                if (invFull == true) {
                                        break;
                                }
                        }
                        if (gotMail){
                                player.sendMessage(plugin.logPrefix + plugin.parseColor(plugin.Messages.get("MailRetrieved")));
                        }else{
                            if (!invFull) {
                                player.sendMessage(plugin.logPrefix + plugin.parseColor(plugin.Messages.get("NoMailRetrieved")));
                            }
                        }
                        if (auctionMail.isEmpty()){	
                                player.sendMessage(plugin.logPrefix + plugin.parseColor(plugin.Messages.get("NoMailRetrieved")));
                        }
                } catch (Exception e) {
                       WebAuction.log.info("Erro on Withdraw");
                       WebAuction.log.info(e.getMessage());
                }
        } else {
                player.sendMessage(plugin.logPrefix + plugin.parseColor(plugin.Messages.get("NoPermission")));
        }
    }
    
    public void ItemtoStore(ItemStack stack,Player player){
        int itemDamage = 0;
        if (stack.getDurability() >= 0) {
            itemDamage = stack.getDurability();
        }
        // Get Enchant
        Map<Enchantment, Integer> itemEnchantments = stack.getEnchantments();
        String ench_player = "";
        for (Map.Entry<Enchantment, Integer> entry : itemEnchantments.entrySet()) {
            int enchId = entry.getKey().getId();
            int level = entry.getValue();
            ench_player += enchId + "," + level + ":";
        }
        
        // check if item not already there
        int quantityInt = stack.getAmount();
        List<Auction> auctions = plugin.dataQueries.getItem(player.getName(), stack.getTypeId(), itemDamage, false,plugin.Myitems);
        Boolean foundMatch = false;
        for (Auction auction : auctions) {
                int itemTableIdNumber = auction.getId();

                if ((( ench_player.equals(auction.getEnchantments()) ) || ( (ench_player.isEmpty()) && (auction.getEnchantments().isEmpty()) )) && !foundMatch ) {
                        int currentQuantity = auction.getQuantity();
                        currentQuantity += quantityInt;
                        plugin.dataQueries.updateItemQuantity(currentQuantity, itemTableIdNumber);
                        foundMatch = true;
                }
        }
        
        // if not already there create the item
        if (foundMatch == false) {
                String ench = "";
                for (Map.Entry<Enchantment, Integer> entry : itemEnchantments.entrySet()) {
                        Enchantment key = entry.getKey();
                        int enchId = key.getId();
                        int level = entry.getValue();
                        ench += enchId + "," + level + ":";
                }
                String type = stack.getType().toString();
                String ItemName = Material.getItemName(stack.getTypeId(),stack.getDurability());
                String searchtype = plugin.getSearchType(ItemName);
                plugin.dataQueries.createItem(stack.getTypeId(), itemDamage, player.getName(), quantityInt, 0.0,ench,1,type,ItemName,searchtype);
        }
    }

}
