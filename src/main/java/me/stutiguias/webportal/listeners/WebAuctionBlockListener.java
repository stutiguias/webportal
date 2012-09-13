package me.stutiguias.webportal.listeners;

import java.util.List;
import me.stutiguias.webportal.init.WebAuction;
import me.stutiguias.webportal.settings.AuctionItem;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;

public class WebAuctionBlockListener implements Listener {

	private final WebAuction plugin;

	public WebAuctionBlockListener(WebAuction plugin) {
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
		Boolean allowEvent = false;
		if (player != null) {
                    if(lines[0].equalsIgnoreCase("[WebAuction]")) WebAuction(lines, player, allowEvent, sign, event);
                    if(lines[0].equalsIgnoreCase("[wSell]")) wSell(lines, player, allowEvent, sign, event);
                }
                

	}
        
        public void wSell(String[] lines,Player player,Boolean allowEvent,Block sign,SignChangeEvent event) {
               
               List<AuctionItem> AuctionItemList = plugin.dataQueries.getItemsByName(player.getName(),lines[1], true, plugin.Auction);
               if(AuctionItemList.size() > 0) {
                   event.setLine(0, ChatColor.GREEN + "[wSell]" );
                   event.setLine(1, AuctionItemList.get(0).getItemName());
                   if(lines[2].isEmpty()) {
                      event.setLine(2,"1 - " + AuctionItemList.get(0).getPrice());
                   }else{
                      int qtd = Integer.parseInt(lines[2]);
                      if(qtd <= AuctionItemList.get(0).getQuantity()) {
                        event.setLine(2, lines[2] + " - E" + AuctionItemList.get(0).getPrice());
                      }else{
                        event.setLine(2, "Invalid Qtd");
                      }
                   }
                   event.setLine(3, AuctionItemList.get(0).getPlayerName());
               }
        }
        
        public void WebAuction(String[] lines,Player player,Boolean allowEvent,Block sign,SignChangeEvent event)
        {
            if ((lines[1].equalsIgnoreCase("MailBox")) || (lines[1].equalsIgnoreCase("Mail Box"))) {
                    if (lines[2].equalsIgnoreCase("Deposit")) {
                            if (plugin.permission.has(player, "wa.create.sign.mailbox.deposit")) {
                                    allowEvent = true;
                                    event.setLine(0, ChatColor.GREEN + "[WebAuction]" );
                                    player.sendMessage(plugin.logPrefix + "Deposit Mail Box created");
                            }
                    } else if (plugin.permission.has(player, "wa.create.sign.mailbox.withdraw")) {
                                    allowEvent = true;
                                    event.setLine(0, ChatColor.GREEN + "[WebAuction]" );
                                    player.sendMessage(plugin.logPrefix + "Withdraw Mail Box created");
                    }
            } else if(lines[1].equalsIgnoreCase("vbox")) {
                    if (plugin.permission.has(player, "wa.create.sign.vbox")) {
                            allowEvent = true;
                            event.setLine(0, ChatColor.GREEN + "[WebAuction]" );
                            player.sendMessage(plugin.logPrefix + "Virtual Box created");
                    }
            }
            if (allowEvent == false) {
                    event.setCancelled(true);
                    sign.setTypeId(0);
                    player.sendMessage(plugin.logPrefix + "You do not have permission");
            }
        }
}
