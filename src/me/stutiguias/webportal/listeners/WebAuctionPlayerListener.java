package me.stutiguias.webportal.listeners;

import java.util.List;
import java.util.logging.Level;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.model.SaleAlert;
import me.stutiguias.webportal.model.WebSitePlayer;
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
        
        @EventHandler(priority = EventPriority.NORMAL)
	public void PlayerQuit(PlayerQuitEvent event){
		plugin.lastUse.remove(event.getPlayer().getName());
	}

        @EventHandler(priority = EventPriority.NORMAL)
	public void PlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
                
                if(plugin.UpdaterNotify && plugin.hasPermission( event.getPlayer(),"wa.update") && WebPortal.update)
                {
                    player.sendMessage(plugin.parseColor("&6An update is available: " + WebPortal.name + ", a " + WebPortal.type + " for " + WebPortal.version + " available at " + WebPortal.link));
                }

                WebSitePlayer auplayer = plugin.db.getPlayer(player.getName());
           
                if (auplayer == null) return;
 
                if (plugin.showSalesOnJoin == true){
                        List<SaleAlert> saleAlerts = plugin.db.getNewSaleAlertsForSeller(player.getName());
                        for (SaleAlert saleAlert : saleAlerts) {
                                player.sendMessage("You sold " + saleAlert.getQuantity() + " " + saleAlert.getItem() + " to " + saleAlert.getBuyer() + " for "+ saleAlert.getPriceEach() + " each.");
                                plugin.db.markSaleAlertSeen(saleAlert.getId());
                        }
                }

                if (plugin.db.hasMail(player.getName()))player.sendMessage("You have new mail!");

                if(plugin.OnJoinCheckPermission){
                    int canBuy = 0;
                    int canSell = 0;
                    int isAdmin = 0;
                    if (plugin.permission.has(event.getPlayer(), "wa.canbuy"))
                            canBuy = 1;
                    if (plugin.permission.has(event.getPlayer(), "wa.cansell"))
                            canSell = 1;
                    if (plugin.permission.has(event.getPlayer(), "wa.webadmin"))
                            isAdmin = 1;
                    plugin.db.updatePlayerPermissions(player.getName(), canBuy, canSell, isAdmin);
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
		if (block == null || (block.getType() != Material.SIGN_POST && block.getType() != Material.WALL_SIGN)) 	return;

		Sign sign = (Sign) block.getState();
		String[] lines = sign.getLines();
                
                if(!lines[0].contains("[WebAuction]")) return;
                
            	if ( ( lines[0].equals(ChatColor.GREEN + "[WebAuction]") || lines[0].equals(ChatColor.GREEN + "[wSell]") ) && isCreative(event)) {
                    event.getPlayer().sendMessage(plugin.logPrefix + " Don't work in creative" );
                    return;
                }
                
		if ( lines[0].equals(ChatColor.GREEN + "[wSell]")) {
                    plugin.wsell.ClickSign(event,sign,lines);
                    return;
                }
                
                if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
		Player player = event.getPlayer();
		event.setCancelled(true);
 
                if(!isDelayExpire(player, plugin.signDelay)) {
                     event.setCancelled(true);
                     return;
                } 
                
		plugin.lastUse.put(player.getName(), plugin.getCurrentMilli());
                
                if(lines[1].equalsIgnoreCase("mailbox") || lines[1].equalsIgnoreCase("mail box"))
                {
                    plugin.mailbox.MailBoxOperationType(event.getPlayer(), lines[2]);
                }else if(lines[1].equalsIgnoreCase("vbox")) {
                    if(!event.getPlayer().hasPermission("wa.vbox"))
                    {
                        event.getPlayer().sendMessage(plugin.logPrefix + "You don't have permission to use vbox");
                        return;
                    }
                    plugin.vbox.Open(event);
                }
                
	}
        
        @EventHandler(priority = EventPriority.NORMAL)
        public void onWebAuctionLiteInventoryClose(InventoryCloseEvent event) {
            if(!event.getInventory().getName().equalsIgnoreCase("WebPortal")) return;
            Player pl = (Player)event.getPlayer();
            
            plugin.vbox.Close(event.getInventory(),pl);
            
            plugin.db.setLock(pl.getName(),"N");
            WebPortal.LockTransact.put(pl.getName(), Boolean.FALSE);
        }

        private Boolean isCreative(PlayerInteractEvent event) {
            return event.getPlayer().getGameMode() == GameMode.CREATIVE && plugin.blockcreative;
        }
        
        private boolean isDelayExpire(Player player,int Delay) {
            if (!plugin.lastUse.containsKey(player.getName())) return true;
            long lastUse = plugin.lastUse.get(player.getName());
            if (lastUse + Delay > plugin.getCurrentMilli()) {
                    player.sendMessage(plugin.logPrefix + "Please wait a bit before using that again");
                    return false;
            }
            return true;
        }
}
