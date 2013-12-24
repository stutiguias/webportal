/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.stutiguias.webportal.information;

import java.util.Map;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Daniel
 */
public class Converters {
    
    public String ItemMetaToString(ItemStack item) {
        
        ItemMeta meta = item.getItemMeta();
        StringBuilder ItemName = new StringBuilder();
        if( meta.hasDisplayName() )  {
            ItemName.append("N[#$]").append(meta.getDisplayName());
        }
        
        if( meta.hasLore() ) {
            for (int i = 0; i < meta.getLore().size(); i++) {
                ItemName.append(",").append("L[#$]").append(meta.getLore().get(i));
            }
        }
        
        return ItemName.toString();
    }
    
    public String EnchantsToString(ItemStack itemstack) {
        Map<Enchantment, Integer> itemEnchantments;
        String enchants = "";
        
        if(itemstack.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta bookmeta = (EnchantmentStorageMeta)itemstack.getItemMeta();
            itemEnchantments = bookmeta.getStoredEnchants();
        }else{
            itemEnchantments = itemstack.getEnchantments();
        }
        
        for (Map.Entry<Enchantment, Integer> entry : itemEnchantments.entrySet()) {
            int enchId = entry.getKey().getId();
            int level = entry.getValue();
            enchants += enchId + "," + level + ":";
        }
        return enchants;
    }
    
}
