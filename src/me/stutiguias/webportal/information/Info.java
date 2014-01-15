/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.information;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.settings.Shop;
import me.stutiguias.webportal.settings.WebSiteMail;
import me.stutiguias.webportal.settings.Enchant;
import me.stutiguias.webportal.settings.WebItemStack;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

/**
 *
 * @author Daniel
 */
public class Info {
    
    private final WebPortal plugin;
    
    public Info(WebPortal plugin)
    {
        this.plugin = plugin;
    }
    
    public Boolean isAdmin(String sessionId) {
        return WebPortal.AuthPlayers.get(sessionId).WebSitePlayer.getIsAdmin() == 1;
    }
    
    public String Format(double x) {  
        return String.format("%.2f", x);  
    } 
    
    public String ConvertItemToResult(int itemId,WebItemStack item,String type) {

        String itemName = item.getName();
        String itemImage = item.getImage();
        
        String metaCSV = plugin.db.GetItemInfo(itemId,"meta");
        item.SetMetaItemName(metaCSV);
        
        if(!itemImage.contains("http") || !itemImage.contains("www"))
            itemImage = String.format("images/%s",itemImage);
        
        return String.format("<div class='itemTableName'><img src='%s' style='max-height:32px;max-width:32px;' /> %s</div>",itemImage,itemName);
        
    }
    
    public String ConvertItemToResult(Shop item,String type) {
        return ConvertItemToResult(item.getId(),item.getItemStack(), type);
    }

    public String GetEnchant(Shop item) {
        StringBuilder enchant = new StringBuilder();
        for (Map.Entry<Enchantment, Integer> entry : item.getItemStack().getEnchantments().entrySet()) {
            int enchId = entry.getKey().getId();
            int level = entry.getValue();
            enchant.append(new Enchant().getEnchantName(enchId, level)).append("<br />");
        }
        if(item.getItemStack().getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta bookmeta = (EnchantmentStorageMeta)item.getItemStack().getItemMeta();
            for (Map.Entry<Enchantment, Integer> entry : bookmeta.getStoredEnchants().entrySet()) {
                int enchId = entry.getKey().getId();
                int level = entry.getValue();
                enchant.append(new Enchant().getEnchantName(enchId, level)).append("<br />");
            }
        }
        return enchant.toString();
    }
    
    public String GetDurability(Shop item) {
        Short dmg = item.getItemStack().getDurability();
        Short maxdur = item.getItemStack().getType().getMaxDurability();
        String Durability = "";
        if(!item.getItemStack().getType().isBlock() && !item.getItemStack().isPotion() && maxdur != 0) {
            Durability = dmg + "/" + maxdur;
        }
        return Durability;
    }


     
    public double MarketPrice(Shop item,Double price) {
           double mprice = plugin.db.GetMarketPriceofItem(item.getItemStack().getTypeId(),item.getItemStack().getDurability());
           if(mprice == 0.0) {
             return 0.0;
           }
           return (( price * 100 ) / mprice);
    }
 


}
