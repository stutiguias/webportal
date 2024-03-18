/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.signs;

import java.util.HashMap;
import java.util.List;
import me.stutiguias.webportal.init.Util;
import me.stutiguias.webportal.init.Messages;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.model.WebItemStack;
import me.stutiguias.webportal.model.WebSiteMail;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Stutiguias
 */
public class Mailbox extends Util {
    
    private final Messages message;
    
    public Mailbox(WebPortal plugin)
    {
        super(plugin);
       this.message = WebPortal.Messages;
    }
 
    public void MailBoxOperationType(Player player,String Operation){
        if(Operation.equalsIgnoreCase("withdraw")) {
                MailBoxWithdraw(player);
        }else {
                MailBoxDeposit(player);
        }
    }
    
    private void MailBoxDeposit(Player player){
        if (!plugin.permission.has(player, "wa.use.deposit.items")) return;
        ItemStack stack = player.getInventory().getItemInMainHand();
        plugin.Store(stack,player);
        SendMessage(player, message.SignStackStored);
        player.getInventory().setItemInMainHand(null);
    }
    
    private void MailBoxWithdraw(Player player) {
        if (!plugin.permission.has(player, "wa.use.withdraw.items")) {
             SendMessage(player, message.SignNoPermission);
             return;
        }
        try {
            List<WebSiteMail> auctionMail = plugin.db.getMail(player.getName());
            boolean invFull = true;
            boolean gotMail = false;
            for (WebSiteMail mail : auctionMail) {
                if (player.getInventory().firstEmpty() != -1) {
                    WebItemStack stack = mail.getItemStack();
                    if (WebPortal.AllowMetaItem) {
                        String MetaCSV = plugin.db.GetItemInfo(mail.getId(), "meta");
                        if (!MetaCSV.isEmpty()) {
                            stack.SetMeta(MetaCSV);
                            mail.setItemStack(stack);
                        }
                    }
                    plugin.db.deleteMail(mail.getId());
                    if (stack.getMaxStackSize() == 1) {
                        ItemStack is = new ItemStack(stack);
                        is.setAmount(1);
                        for (int i = 1; i <= stack.getAmount(); i++) {
                            player.getInventory().addItem(is);
                        }
                    } else {
                        HashMap<Integer, ItemStack> notfit = player.getInventory().addItem(stack);
                        if (!notfit.isEmpty()) {
                            for (ItemStack notfitstack : notfit.values()) {
                                SendMessage(player, message.SignInventoryFull);
                                plugin.Store(notfitstack, player);
                            }
                        }
                    }
                    player.updateInventory();
                    gotMail = true;
                    invFull = false;
                } else {
                    SendMessage(player, message.SignInventoryFull);
                    invFull = true;
                }
                if (invFull == true) {
                    break;
                }
            }
            
            if (gotMail) {
                SendMessage(player, message.SignMailRetrieved);
            } else if (!invFull) {
                SendMessage(player, message.SignNoMailRetrieved);
            }
            if (auctionMail.isEmpty()) {
                SendMessage(player, message.SignNoMailRetrieved);
            }
        } catch (IllegalArgumentException e) {
            WebPortal.logger.info("Erro on Withdraw");
            WebPortal.logger.info(e.getMessage());
        }
    }
}
