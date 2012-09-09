/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.settings;

import java.util.List;
import me.stutiguias.webportal.init.WebAuction;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Daniel
 */
public class InventoryHandler implements InventoryHolder {

    final Inventory inventory;
    WebAuction plugin;
    Player player;
    
    public InventoryHandler(WebAuction plugin,Player player) {
        this.plugin = plugin;
        this.player = player;
        inventory = plugin.getServer().createInventory(this,45,"WebAuctionLite");
    }
    
    @Override
    public Inventory getInventory() {
        List<Auction> items = plugin.dataQueries.getAuctionsLimitbyPlayer(player.getName(), 0, 20,plugin.Myitems);
        for(Auction i:items) {
            inventory.addItem(i.getItemStack());
        }
        WebAuction.LockTransact.put(player.getName(), Boolean.TRUE);
        return inventory;
    }
    
}
