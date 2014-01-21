/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.signs;

import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.inventory.InventoryHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

/**
 *
 * @author Daniel
 */
public class vBox {
    
    WebPortal plugin;
    InventoryHandler inventoryHandler;
    
    public vBox(WebPortal instance) {
        plugin = instance;
    }
    
    public void Open(PlayerInteractEvent event){
        inventoryHandler = new InventoryHandler(plugin,event.getPlayer());
        event.getPlayer().openInventory(inventoryHandler.getInventory());
        event.setCancelled(true);
    }
    
    public void Close(Inventory inventory,Player player) {
        inventoryHandler = new InventoryHandler(plugin,player);
        inventoryHandler.Close(inventory);
    }
  
}
