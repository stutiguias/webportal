/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import me.stutiguias.webportal.init.WebPortal;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Daniel
 */
public class WebItemStack extends ItemStack {

    private String Name;
    private String Image;

    public WebItemStack() {
        GetConfig();
    }
     
    public WebItemStack(Integer type,int amount,Short damage) {
        super(type, amount, damage);
        GetConfig();
    }
    
    public WebItemStack(String MetaCSV) {
        GetConfig();
        if(WebPortal.AllowMetaItem) {
            AddMeta(MetaCSV, Name);
        }
    }
    
    public String GetMeta() {

        ItemMeta meta = getItemMeta();
        StringBuilder ItemName = new StringBuilder();
        if (meta.hasDisplayName()) {
            ItemName.append("N[#$]").append(meta.getDisplayName());
        }

        if (meta.hasLore()) {
            for (int i = 0; i < meta.getLore().size(); i++) {
                ItemName.append(",").append("L[#$]").append(meta.getLore().get(i));
            }
        }

        return ItemName.toString();
    }

    public void SetMeta(String MetaCSV) {

        ItemMeta meta = getItemMeta();

        String[] metas = MetaCSV.split(",");
        List<String> lores = new ArrayList<>();
        for (String meta1 : metas) {
            if (meta1.startsWith("N[#$]")) {
                String metad = meta1.replace("N[#$]", "");
                meta.setDisplayName(metad);
            } else {
                String metal = meta1.replace("L[#$]", "");
                lores.add(metal);
            }
        }
        if (lores.size() > 0) {
            meta.setLore(lores);
        }

        setItemMeta(meta);
    }

    public String GetEnchants() {
        Map<Enchantment, Integer> itemEnchantments;
        String enchants = "";

        if (getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta bookmeta = (EnchantmentStorageMeta) getItemMeta();
            itemEnchantments = bookmeta.getStoredEnchants();
        } else {
            itemEnchantments = getEnchantments();
        }

        for (Map.Entry<Enchantment, Integer> entry : itemEnchantments.entrySet()) {
            int enchId = entry.getKey().getId();
            int level = entry.getValue();
            enchants += enchId + "," + level + ":";
        }
        return enchants;
    }
    
    private void GetConfig() {
        
        String itemId = GetItemId();
        String itemConfig;
        
        String SearchType = WebPortal.GetSearchType(itemId);
        
        if(!SearchType.equalsIgnoreCase("Others"))
            itemConfig = GetConfigName(itemId,SearchType);
        else
            itemConfig = "Not Found,Not Found";
        
        Name = itemConfig.split(",")[0];
        Image = itemConfig.split(",")[1];
    }
            
    private String GetConfigName(String itemId,String type) {
        for (String key : WebPortal.materials.getConfig().getConfigurationSection(type).getKeys(false)) {
            if(key.equalsIgnoreCase(itemId)) {
                return WebPortal.materials.getConfig().getString(type + "." + key);
            }
        }
        return "Not Found,Not Found";
    }
    
    public String GetSearchType() {
        String itemId = GetItemId();        
        return WebPortal.GetSearchType(itemId);
    }
        
    public String GetItemId() {
        
        String itemId;
        Short dmg = getDurability();
        if( ( getType().isBlock() || isPotion() || getTypeId() == 322 || getTypeId() == 383 ) && !dmg.equals(Short.valueOf("0")) ) 
            itemId = getTypeId() + "-" + getDurability();
        else
            itemId = String.valueOf(getTypeId());
        return itemId;
        
    }
    
    public Boolean isPotion() {
        return getType() == Material.POTION || getType() == Material.INK_SACK;
    }

    /**
     * @return the Name
     */
    public String getName() {
        return Name;
    }

    /**
     * @param Name the Name to set
     */
    public void setName(String Name) {
        this.Name = Name;
    }

    /**
     * @return the Image
     */
    public String getImage() {
        return Image;
    }

    /**
     * @param Image the Image to set
     */
    public void setImage(String Image) {
        this.Image = Image;
    }
    
    public void SetMetaItemName(String metaCSV) {
        if(WebPortal.AllowMetaItem) Name = AddMeta(metaCSV, Name);
    }
    
    private String AddMeta(String metaCSV, String itemName) {

        if(metaCSV.isEmpty()) return itemName;
  
        for (String meta : metaCSV.split(",")) {
            if (meta.startsWith("N[#$]")) {
                itemName = meta.replace("N[#$]", "").replaceAll("§\\w", "");
            } else {
                itemName += " " + meta.replace("L[#$]", "").replaceAll("§\\w", "");
            }
        }
        
        return itemName;
    }
       
}
