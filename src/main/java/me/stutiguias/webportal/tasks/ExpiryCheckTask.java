package me.stutiguias.webportal.tasks;

import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.model.Shop;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.logging.Level;

public class ExpiryCheckTask implements Runnable {
    private final WebPortal plugin;

    public ExpiryCheckTask(WebPortal plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (plugin.itemExpiryHours <= 0) return;
        
        try {
            List<Shop> expiredItems = plugin.db.getExpiredItems(plugin.itemExpiryHours);
            
            for (Shop item : expiredItems) {
                OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(item.getPlayerName());
                
                // Retorna item ao mailbox
                if (offlinePlayer != null && offlinePlayer.getPlayer() != null) {
                    plugin.Store(item.getItemStack(), offlinePlayer.getPlayer());
                }
                
                // Deleta o item da listagem
                plugin.db.DeleteAuction(item.getId());
                
                // Notifica o jogador se estiver online
                Player onlinePlayer = Bukkit.getPlayerExact(item.getPlayerName());
                if (onlinePlayer != null && onlinePlayer.isOnline()) {
                    String itemName = item.getItemStack().getType().name();
                    int quantity = item.getItemStack().getAmount();
                    onlinePlayer.sendMessage(plugin.parseColor("&e[WebPortal] &7Your item &e" + itemName + " x" + quantity + " &7(ID: &e#" + item.getId() + "&7) has expired and was sent to your mailbox."));
                    onlinePlayer.sendMessage(plugin.parseColor("&7Use &e/wa renew <ID> &7to renew listings before they expire."));
                }
                
                WebPortal.logger.log(Level.INFO, "{0} Expired item: ID {1} from player {2} returned to mailbox", 
                    new Object[]{plugin.logPrefix, item.getId(), item.getPlayerName()});
            }
            
            if (!expiredItems.isEmpty()) {
                WebPortal.logger.log(Level.INFO, "{0} Total of {1} expired items processed", 
                    new Object[]{plugin.logPrefix, expiredItems.size()});
            }
        } catch (Exception e) {
            WebPortal.logger.log(Level.WARNING, "{0} Error processing expired items: {1}", 
                new Object[]{plugin.logPrefix, e.getMessage()});
        }
    }
}
