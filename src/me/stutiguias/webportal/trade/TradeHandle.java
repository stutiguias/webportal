/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.trade;

import java.util.List;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.init.Util;
import me.stutiguias.webportal.model.Shop;
import me.stutiguias.webportal.model.WebItemStack;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Daniel
 */
public class TradeHandle extends Util {

    public TradeHandle(WebPortal plugin){
        super(plugin);
    }
    
    public void Store(ItemStack item, Player player) {
        Store( Convert(item) , player);
    }
    
    public void Store(WebItemStack stack,Player player){
        
        int itemDamage = getDurability(stack);
        String enchants = stack.GetEnchants();
        int quantityInt = stack.getAmount();
                
        List<Shop> shops = plugin.db.getItem(player.getName(), stack.getTypeId(), itemDamage, false,plugin.Myitems);
        
        Boolean foundMatch = false;

        for (Shop shop : shops) {

            int itemId = shop.getId();
            
            if( stack.hasItemMeta() && !isMetaEqual(stack, shop) ) continue;
                
            if (isEnchantsEqual(enchants, shop)) {
                int currentQuantity = shop.getQuantity();
                currentQuantity += quantityInt;
                plugin.db.updateItemQuantity(currentQuantity, itemId);
                foundMatch = true;
                break;
            }
        }

        if (foundMatch == true) return;
            
        String type = stack.getType().toString();
        String searchtype = stack.GetSearchType();
        int createdId = plugin.db.CreateItem(stack.getTypeId(), itemDamage, player.getName(), quantityInt, 0.0,enchants,1,type,searchtype);

        if( WebPortal.AllowMetaItem && stack.hasItemMeta() && stack.getType() != Material.ENCHANTED_BOOK ) {
           String ItemMeta = stack.GetMeta();
           plugin.db.InsertItemInfo(createdId,"meta", ItemMeta);
        }
        
    }
       
    public boolean isMetaEqual(WebItemStack item,Shop shop) {
        String shopMeta = plugin.db.GetItemInfo(shop.getId(),"meta");
        String itemMeta = item.GetMeta();
        return shopMeta.equalsIgnoreCase(itemMeta);
    }
    
    public boolean isEnchantsEqual(String enchants,Shop auction) {
        return enchants.equals(auction.getEnchantments()) || ( enchants.isEmpty() && auction.getEnchantments().isEmpty() );
    }
    
    public int getDurability(ItemStack itemstack) {
        if (itemstack.getDurability() >= 0) {
            return itemstack.getDurability();
        }else{
            return 0;
        }
    }

}
