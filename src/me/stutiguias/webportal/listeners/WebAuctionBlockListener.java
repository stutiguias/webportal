package me.stutiguias.webportal.listeners;

import me.stutiguias.webportal.init.WebPortal;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;

public class WebAuctionBlockListener implements Listener {

	private final WebPortal plugin;
        
	public WebAuctionBlockListener(WebPortal plugin) {
		this.plugin = plugin;
	}
        
        @EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();
		if ((block.getTypeId() == 63) || (block.getTypeId() == 68)) {
			Sign thisSign = (Sign) block.getState();
			if (thisSign.getLine(0).equals("[WebAuction]")) {
				if (!plugin.permission.has(player, "wa.remove")) {
					event.setCancelled(true);
					player.sendMessage(plugin.logPrefix + "You do not have permission to remove that");
				} else {
					player.sendMessage(plugin.logPrefix + "WebAuction sign removed.");
				}
			}
		}
	}
        
        @EventHandler(priority = EventPriority.NORMAL)
	public void onSignChange(SignChangeEvent event) {
		String[] lines = event.getLines();
		Player player = event.getPlayer();
		Block sign = event.getBlock();

		if (player != null) {
                    if(lines[0].equalsIgnoreCase("[WebAuction]")) WebAuction(lines, player, sign, event);
                    if(lines[0].equalsIgnoreCase("[wSell]")) plugin.wsell.addwSell(lines, player, sign, event);
                }
                
	}
  
        public void WebAuction(String[] lines,Player player,Block sign,SignChangeEvent event)
        {
            Boolean allowEvent = false;
            if (isMailboxSign(lines)) {
                    if (lines[2].equalsIgnoreCase("Deposit")) {
                            if (plugin.permission.has(player.getWorld(),player.getName(),"wa.create.sign.mailbox.deposit")) {
                                    allowEvent = true;
                                    event.setLine(0, ChatColor.GREEN + "[WebAuction]" );
                                    player.sendMessage(plugin.logPrefix + "Deposit Mail Box created");
                            }
                    } else if (plugin.permission.has(player.getWorld(),player.getName(), "wa.create.sign.mailbox.withdraw")) {
                                    allowEvent = true;
                                    event.setLine(0, ChatColor.GREEN + "[WebAuction]" );
                                    player.sendMessage(plugin.logPrefix + "Withdraw Mail Box created");
                    }
            } else if(lines[1].equalsIgnoreCase("vbox")) {
                    if (plugin.permission.has(player.getWorld(),player.getName(), "wa.create.sign.vbox")) {
                            allowEvent = true;
                            event.setLine(0, ChatColor.GREEN + "[WebAuction]" );
                            player.sendMessage(plugin.logPrefix + "Virtual Box created");
                    }
            }
            if (allowEvent == false) CancelEvent(event,player,sign);
        }
        
        private boolean isMailboxSign(String[] lines) {
            if((lines[1].equalsIgnoreCase("MailBox")) || 
               (lines[1].equalsIgnoreCase("Mail Box"))) {
                return true;
            }else{
                return false;
            }
        }
        
        private void CancelEvent(SignChangeEvent event,Player player,Block thisSign) {
                event.setCancelled(true);
                thisSign.setTypeId(0);
                player.sendMessage(plugin.logPrefix + "You do not have permission");
        }
}
