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
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

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
    
    public Boolean isAdmin(String Hostadress) {
        return WebPortal.AuthPlayers.get(Hostadress).WebSitePlayer.getIsAdmin() == 1;
    }
    
    public String Format(double x) {  
        return String.format("%.2f", x);  
    } 
    
    public String ConvertItemToResult(int itemId,ItemStack item,String type) {
        
        String[] nameAndImg = GetItemConfig(item);
        String item_name = nameAndImg[0];
        String img_name = nameAndImg[1];
        
        if(plugin.AllowMetaItem) {
            item_name = ChangeItemToItemMeta(itemId, item_name);
        }
        
        if(!img_name.contains("http") || !img_name.contains("www"))
            img_name = String.format("images/%s",img_name);
        
        return String.format("<div class='itemTableName'><img src='%s' style='max-height:32px;max-width:32px;' /> %s</div>",img_name,item_name);
        
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
        if(!item.getItemStack().getType().isBlock() && !isPotion(item.getItemStack()) && maxdur != 0) {
            Durability = dmg + "/" + maxdur;
        }
        return Durability;
    }
    
    public Boolean isPotion(ItemStack item) {
        return item.getType() == Material.POTION || item.getType() == Material.INK_SACK;
    }
    
    // Return Name,Image
    public String[] GetItemConfig(ItemStack item) {
        
        String itemId = GetItemId(item);
        String itemConfig;
        
        String SearchType = plugin.getSearchType(itemId);
        
        if(!SearchType.equalsIgnoreCase("Others"))
            itemConfig = GetConfigName(itemId,SearchType);
        else
            itemConfig = "Not Found,Not Found";
        
        return itemConfig.split(",");
    }
    
    public String GetSearchType(ItemStack item) {
        String itemId = GetItemId(item);        
        return plugin.getSearchType(itemId);
    }
    
    public String GetItemId(ItemStack item) {
        
        String itemId;
        Short dmg = item.getDurability();
        if( ( item.getType().isBlock() || isPotion(item) || item.getTypeId() == 322 || item.getTypeId() == 383 ) && !dmg.equals(Short.valueOf("0")) ) 
            itemId = item.getTypeId() + "-" + item.getDurability();
        else
            itemId = String.valueOf(item.getTypeId());
        return itemId;
        
    }
    
        
    public ItemStack SetItemMeta(ItemStack item,String MetaCSV) {
        
        ItemMeta meta = item.getItemMeta();
        
        String[] metas = MetaCSV.split(",");
        List<String> lores = new ArrayList<>();
        for (int i = 0; i < metas.length; i++) {
            if(metas[i].startsWith("N[#$]")) {
                String metad = metas[i].replace("N[#$]","");
                meta.setDisplayName(metad);
            } else {
                String metal = metas[i].replace("L[#$]","");
                lores.add(metal);
            }
        }
        if(lores.size() > 0)
            meta.setLore(lores);
        
        item.setItemMeta(meta);
        
        return item;
    }
    
    private String GetConfigName(String itemId,String type) {
            for (Iterator<String> it = plugin.materials.getConfig().getConfigurationSection(type).getKeys(false).iterator(); it.hasNext();) {
                String key = it.next();
                if(key.equalsIgnoreCase(itemId)) {
                    return plugin.materials.getConfig().getString(type + "." + key);
                }
            }
            return "Not Found,Not Found";
    }
    
    public String GetConfigKey(String Itemname,String type) {
            try {
                for (Iterator<String> it = plugin.materials.getConfig().getConfigurationSection(type).getKeys(false).iterator(); it.hasNext();) {
                    String key = it.next();
                    if(Itemname.equalsIgnoreCase(plugin.materials.getConfig().getString(type + "." + key))) {
                        return key;
                    }
                }
            }catch(NullPointerException ex){
                
            }
            return Itemname;
    }
    
    public double MarketPrice(Shop item,Double price) {
           double mprice = plugin.dataQueries.GetMarketPriceofItem(item.getItemStack().getTypeId(),item.getItemStack().getDurability());
           if(mprice == 0.0) {
             return 0.0;
           }
           return (( price * 100 ) / mprice);
    }
    
    public String ChangeItemToItemMeta(WebSiteMail mail,String item_name) {
        Shop auction = new Shop();
        auction.setItemStack(mail.getItemStack());
        auction.setId(mail.getId());
        return ChangeItemToItemMeta(auction, item_name);
    }
    
    public String ChangeItemToItemMeta(int itemId, String item_name) {
        String meta = plugin.dataQueries.GetItemInfo(itemId,"meta");
        if(!meta.isEmpty()) {
            String[] metas = meta.split(",");
            for (int i = 0; i < metas.length; i++) {
                if(metas[i].startsWith("N[#$]")) {
                    String metad = metas[i].replace("N[#$]","").replaceAll("§\\w","");
                    item_name = metad;
                } else {
                    String metal = metas[i].replace("L[#$]","").replaceAll("§\\w","");
                    item_name += " " + metal;
                }
            }
        }
        return item_name;
    }
    
    public String ChangeItemToItemMeta(Shop item, String item_name) {
        return ChangeItemToItemMeta(item.getId(), item_name);
    }

}
