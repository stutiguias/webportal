package me.stutiguias.webportal.listeners;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.settings.*;
import me.stutiguias.webportal.signs.Mailbox;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class WebAuctionPlayerListener implements Listener {

	private final WebPortal plugin;
        
	public WebAuctionPlayerListener(WebPortal plugin) {
		this.plugin = plugin;
	}

	public static double round(double unrounded, int precision, int roundingMode) {
		BigDecimal bd = new BigDecimal(unrounded);
		BigDecimal rounded = bd.setScale(precision, roundingMode);
		return rounded.doubleValue();
	}
        
        @EventHandler(priority = EventPriority.NORMAL)
	public void PlayerQuit(PlayerQuitEvent event){
		plugin.lastSignUse.remove(event.getPlayer().getName());
	}

        @EventHandler(priority = EventPriority.NORMAL)
	public void PlayerJoin(PlayerJoinEvent event) {
		String player = event.getPlayer().getName();
                if (null != plugin.dataQueries.getPlayer(player)) {
                    
                    // Alert player of any new sale alerts
                    if (plugin.showSalesOnJoin == true){
                            List<SaleAlert> saleAlerts = plugin.dataQueries.getNewSaleAlertsForSeller(player);
                            for (SaleAlert saleAlert : saleAlerts) {
                                    event.getPlayer().sendMessage(plugin.logPrefix + "You sold " + saleAlert.getQuantity() + " " + saleAlert.getItem() + " to " + saleAlert.getBuyer() + " for "+ saleAlert.getPriceEach() + " each.");
                                    plugin.dataQueries.markSaleAlertSeen(saleAlert.getId());
                            }
                    }

                    if (plugin.dataQueries.hasMail(player)) {
                            event.getPlayer().sendMessage(plugin.logPrefix + "You have new mail!");
                    }

                    // Determine permissions
                    int canBuy = 0;
                    int canSell = 0;
                    int isAdmin = 0;
                    if (plugin.permission.has(event.getPlayer().getWorld(),event.getPlayer().getName(), "wa.canbuy")) {
                            canBuy = 1;
                    }
                    if (plugin.permission.has(event.getPlayer().getWorld(),event.getPlayer().getName(), "wa.cansell")) {
                            canSell = 1;
                    }
                    if (plugin.permission.has(event.getPlayer().getWorld(),event.getPlayer().getName(), "wa.webadmin")) {
                            isAdmin = 1;
                    }
		
                    WebPortal.logger.log(Level.INFO, "{0} Player - {1} : canbuy = {2} cansell = {3} isAdmin = {4}", new Object[]{plugin.logPrefix, player, canBuy, canSell, isAdmin});
                    // Update permissions
                    plugin.dataQueries.updatePlayerPermissions(player, canBuy, canSell, isAdmin);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event) {
            	
		if (!event.hasItem() && !event.hasBlock()) return;
                Block block;
                try{
		  block = event.getPlayer().getTargetBlock(null, 1);
                }catch(IllegalStateException ex){
                  return;  
                }
		if (null == block || (block.getType() != Material.SIGN_POST && block.getType() != Material.WALL_SIGN)) 	return;
                
		// it's a sign
		Sign sign = (Sign) block.getState();
		String[] lines = sign.getLines();

		if (!lines[0].equals(ChatColor.GREEN + "[WebAuction]")) {
                    if(lines[0].equals(ChatColor.GREEN + "[wSell]")) { 
                        if(isCreative(event, sign)) return;
                        plugin.wsell.ClickSign(event,sign,lines);
                    }else{
			return;
                    }
		}
                if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
		String player = event.getPlayer().getName();
		event.setCancelled(true);

		// Make sure we can use the sign
		if (plugin.lastSignUse.containsKey(player)) {
			long lastSignUse = plugin.lastSignUse.get(player);
			if (lastSignUse + plugin.signDelay > plugin.getCurrentMilli()) {
				event.getPlayer().sendMessage(plugin.logPrefix + "Please wait a bit before using that again");
				return;
			}
		}
		plugin.lastSignUse.put(player, plugin.getCurrentMilli());
                
                if(lines[1].equalsIgnoreCase("mailbox") || lines[1].equalsIgnoreCase("mail box"))
                {
                    plugin.mailbox.MailBoxOperationType(event.getPlayer(), lines[2]);
                }else if(lines[1].equalsIgnoreCase("vbox")) {
                    plugin.vbox.Open(event);
                }
                
	}
                
        @EventHandler(priority = EventPriority.NORMAL)
        public void onWebAuctionLiteInventoryClick(InventoryClickEvent event) {
            if(!event.getInventory().getName().equalsIgnoreCase("WebPortal")) return;
            if(event.getCurrentItem() == null) return;
            if(event.isShiftClick()) {
                event.setCancelled(true);
                return;
            }
            Player pl = (Player)event.getWhoClicked();
            if(event.getRawSlot() <= 44) {
                if(event.getCurrentItem().getType() != Material.AIR && event.getCursor().getType() == Material.AIR) {
                   plugin.vbox.Delete(event, pl);
                }
                if(event.getCursor().getType() != Material.AIR) {
                   plugin.vbox.AddItem(event.getCursor(), pl,event);
                }
            }
        }
        
        @EventHandler(priority = EventPriority.NORMAL)
        public void onWebAuctionLiteInventoryClose(InventoryCloseEvent event) {
            if(!event.getInventory().getName().equalsIgnoreCase("WebPortal")) return;
            Player pl = (Player)event.getPlayer();
            plugin.dataQueries.setLock(pl.getName(),"N");
            WebPortal.LockTransact.put(pl.getName(), Boolean.FALSE);
        }

        public Boolean isCreative(PlayerInteractEvent event,Sign sign) {
            if(event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                event.getPlayer().sendMessage(plugin.logPrefix + " Don't work in creative" );
                event.setCancelled(true);
                sign.update();
                return true;
            }else{
                return false;
            }
        }
}
