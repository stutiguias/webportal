/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.signs;

import java.util.HashMap;
import java.util.List;
import me.stutiguias.webportal.information.Info;
import me.stutiguias.webportal.init.Messages;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.settings.WebSiteMail;
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
    private Messages message;
    
    public Mailbox(WebPortal plugin)
    {
       this.plugin = plugin;
       TradeSystem = new TradeSystem(plugin);
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
        if (plugin.permission.has(player.getWorld(),player.getName(), "wa.use.deposit.items")) {
                ItemStack stack = player.getItemInHand();
                if (stack != null) {
                        if (stack.getTypeId() != 0) {
                                TradeSystem.ItemtoStore(stack,player);
                                player.sendMessage(Format(message.SignStackStored));
                        }else{
                                player.sendMessage(Format(message.SignHoldHelp));						
                        }
                }
               player.setItemInHand(null);
        }
    }
    
    private void MailBoxWithdraw(Player player) {
        if (plugin.permission.has(player.getWorld(),player.getName(), "wa.use.withdraw.items")) {
                try {
                        List<WebSiteMail> auctionMail = plugin.dataQueries.getMail(player.getName());
                        boolean invFull = true;
                        boolean gotMail = false;
                        for (WebSiteMail mail : auctionMail) {
                                if (player.getInventory().firstEmpty() != -1) {
                                        ItemStack stack = mail.getItemStack();
                                        if(plugin.AllowMetaItem) {
                                            String MetaCSV = plugin.dataQueries.GetItemInfo(mail.getId(),"meta");
                                            if(!MetaCSV.isEmpty())
                                            mail.setItemStack(new Info(plugin).SetItemMeta(stack, MetaCSV));
                                        }
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
                                                 player.sendMessage(Format(message.SignInventoryFull));
                                                 TradeSystem.ItemtoStore(notfitstack, player);
                                               }
                                           }
                                        }
                                        player.updateInventory();
                                        gotMail = true;
                                        invFull = false;
                                } else {
                                        player.sendMessage(Format(message.SignInventoryFull));
                                        invFull = true;
                                }
                                if (invFull == true) {
                                        break;
                                }
                        }
                        if (gotMail){
                           player.sendMessage(Format(message.SignMailRetrieved));
                        }else if (!invFull) {
                           player.sendMessage(Format(message.SignNoMailRetrieved));
                        }
                        if (auctionMail.isEmpty()){	
                           player.sendMessage(Format(message.SignNoMailRetrieved));
                        }
                } catch (Exception e) {
                       WebPortal.logger.info("Erro on Withdraw");
                       WebPortal.logger.info(e.getMessage());
                }
        } else {
                player.sendMessage(Format(message.SignNoPermission));
        }
    }
   
    public String Format(String msg) {
        return plugin.logPrefix + plugin.parseColor(msg);
    }
}
