/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.signs;

import java.util.HashMap;
import java.util.List;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.settings.AuctionMail;
import me.stutiguias.webportal.settings.TradeSystem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Stutiguias
 */
public class Mailbox {
    
    private WebPortal plugin;
    private TradeSystem TradeSystem;
    
    public Mailbox(WebPortal plugin)
    {
       this.plugin = plugin;
       TradeSystem = new TradeSystem(plugin);
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
                                TradeSystem.ItemtoStore(stack,player);
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
                                                 TradeSystem.ItemtoStore(notfitstack, player);
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
                       WebPortal.logger.info("Erro on Withdraw");
                       WebPortal.logger.info(e.getMessage());
                }
        } else {
                player.sendMessage(plugin.logPrefix + plugin.parseColor(plugin.Messages.get("NoPermission")));
        }
    }
   
}
