/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.inventory;

import me.stutiguias.webportal.trade.TradeHandle;
import java.util.List;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.model.Shop;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Daniel
 */
public class InventoryHandler implements InventoryHolder {

    Inventory inventory;
    WebPortal plugin;
    Player player;
    
    public InventoryHandler(WebPortal plugin,Player player) {
        this.plugin = plugin;
        this.player = player;
        StartInventory();
    }
    
    private void StartInventory(){
        inventory = plugin.getServer().createInventory(this,45,"WebPortal");
    }
    
    @Override
    public Inventory getInventory() {
        List<Shop> items = plugin.db.getAuctionsLimitbyPlayer(player.getName(), 0, 20,plugin.Myitems);
        
        for(Shop item:items) {
            
            if(WebPortal.AllowMetaItem){
                String meta = plugin.db.GetItemInfo(item.getId(),"meta");
                if(!meta.isEmpty()) {
                   item.getItemStack().SetMeta(meta);
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
            
            plugin.db.DeleteAuction(item.getId());
        }
        plugin.db.setLock(player.getName(),"S");
        WebPortal.LockTransact.put(player.getName(), Boolean.TRUE);
        return inventory;
    }
    
    public void Close(Inventory inventory) {
        for(ItemStack item:inventory.getContents()) {
            if(item == null) continue;
            plugin.Store(item,player);
        }
    }
    
}
