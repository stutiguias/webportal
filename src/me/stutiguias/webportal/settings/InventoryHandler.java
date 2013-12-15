/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.settings;

import java.util.List;
import me.stutiguias.webportal.information.Info;
import me.stutiguias.webportal.init.WebPortal;
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
    WebPortal plugin;
    Player player;
    Info info;
    
    public InventoryHandler(WebPortal plugin,Player player) {
        this.plugin = plugin;
        this.player = player;
        inventory = plugin.getServer().createInventory(this,45,"WebPortal");
        info = new Info(plugin);
    }
    
    @Override
    public Inventory getInventory() {
        List<Shop> items = plugin.dataQueries.getAuctionsLimitbyPlayer(player.getName(), 0, 20,plugin.Myitems);
        
        for(Shop item:items) {
            
            if(plugin.AllowMetaItem){
                String meta = plugin.dataQueries.GetItemInfo(item.getId(),"meta");
                if(!meta.isEmpty()) {
                   item.setItemStack(info.SetItemMeta(item.getItemStack(), meta));
                }
            }
            
            if(item.getItemStack().getMaxStackSize() == 1) {
                ItemStack is = new ItemStack(item.getItemStack());
                is.setAmount(1);
                for(int i=1;i <= item.getItemStack().getAmount();i++)
                {
                    if(inventory.firstEmpty() == -1) break;
                    inventory.addItem(is);    
                }
            }else{
                if(inventory.firstEmpty() ==  -1) break;
                inventory.addItem(item.getItemStack());
            }
        }
        plugin.dataQueries.setLock(player.getName(),"S");
        WebPortal.LockTransact.put(player.getName(), Boolean.TRUE);
        return inventory;
    }
    
}
