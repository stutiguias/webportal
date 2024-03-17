package me.stutiguias.webportal.model;

import java.util.ArrayList;
import java.util.List;

import me.stutiguias.webportal.init.WebPortal;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Daniel
 */
public class WebItemStack extends ItemStack {

    private String Name;
    private String Image;
    
    public WebItemStack(Material type,int amount,int damage) {
        super(type, amount);
        setItemMeta(Bukkit.getItemFactory().getItemMeta(getType()));
        setItemMetaDamage(damage);
        GetConfig();
    }

    public WebItemStack(Material type,int amount) {
        super(type, amount);
        GetConfig();
    }

    public void setItemMetaDamage(int damage) {
        if(getItemMeta() == null) return;
        ItemMeta meta = this.getItemMeta();
        if(this.getType().getMaxDurability() > 0) ((Damageable) meta).setDamage(damage);
        this.setItemMeta(meta);
    }

    public WebItemStack(String MetaCSV) {
         super();
        GetConfig();
        if(WebPortal.AllowMetaItem) {
            AddMeta(MetaCSV, Name, true);
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
    
    private void GetConfig() {
        Name = GetItemName();
        Image = Name;
    }
             
    public String GetSearchType() {
        //String itemName = GetItemName();
        //return WebPortal.GetSearchType(itemName);
        return GetItemName();
    }
        
    public String GetItemName() {
        
        String itemId;
        Short dmg = getDurability();
        if( isIdWithDurability() && !dmg.equals(Short.valueOf("0")) ) 
            itemId = getType().name() + "-" + getDurability();
        else
            itemId = getType().name();
        return itemId;
        
    }
    
    private boolean isIdWithDurability() {
        return getType().isBlock() 
                || isPotion() 
                || getType() == Material.GOLDEN_APPLE 
               // || getType() == Material.LEGACY_MONSTER_EGGS
                || getType() == Material.LEGACY_RAW_FISH 
                || getType() == Material.LEGACY_COOKED_FISH;
    }
    
    public Boolean isPotion() {
        return getType() == Material.POTION || getType() == Material.INK_SAC;
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
    
    public void SetMetaItemNameForDisplay(String metaCSV,boolean useFullmeta) {
        if(WebPortal.AllowMetaItem) Name = AddMeta(metaCSV, Name,useFullmeta);
    }
    
    private String AddMeta(String metaCSV, String itemName,boolean useFullmeta) {

        if(metaCSV.isEmpty()) return itemName;
  
        for (String meta : metaCSV.split(",")) {
            if (meta.startsWith("N[#$]")) {
                itemName = "[ " + meta.replace("N[#$]", "").replaceAll("§\\w", "") + " ]";
            } else if(useFullmeta) {
                itemName += " " + meta.replace("L[#$]", "").replaceAll("§\\w", "");
            }
        }
        
        return itemName;
    }
       
}
