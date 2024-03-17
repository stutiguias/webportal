/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.model;

import java.util.Map;
import me.stutiguias.webportal.init.WebPortal;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

/**
 *
 * @author Daniel
 */
public class Enchant {
    
    private int id;
    private String enchName;
    private int enchId;
    private int level;
    
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the enchName
     */
    public String getEnchName() {
        return enchName;
    }

    /**
     * @param enchName the enchName to set
     */
    public void setEnchName(String enchName) {
        this.enchName = enchName;
    }

    /**
     * @return the enchId
     */
    public int getEnchId() {
        return enchId;
    }

    /**
     * @param enchId the enchId to set
     */
    public void setEnchId(int enchId) {
        this.enchId = enchId;
    }

    /**
     * @return the level
     */
    public int getLevel() {
        return level;
    }

    /**
     * @param level the level to set
     */
    public void setLevel(int level) {
        this.level = level;
    }
    
    public String getEnchantName(String enchant,int level){
        String result = enchant;
        result += " ";
        switch (level) {
            case 1: result += "I"; break;
            case 2: result += "II"; break;
            case 3: result += "III"; break;
            case 4: result += "IV"; break;
            case 5: result += "V"; break;
            case 6: result += "VI"; break;
            case 7: result += "VII"; break;
            case 8: result += "VIII"; break;
            case 9: result += "IX"; break;
            case 10: result += "X"; break;
            case 40: result += "XL"; break;
            case 50: result += "L"; break;
            case 90: result += "XC"; break;
            case 100: result += "C"; break;
            case 400: result += "CD"; break;
            case 500: result += "D"; break;
            case 900: result += "CM"; break;
            case 1000: result += "M"; break;
            default: result += level; break;
        }
        return result;
    }
    
    public static String GetEnchants(ItemStack item) {
        Map<Enchantment, Integer> itemEnchantments;
        String enchants = "";

        if (item.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta bookmeta = (EnchantmentStorageMeta) item.getItemMeta();
            itemEnchantments = bookmeta.getStoredEnchants();
        } else {
            itemEnchantments = item.getEnchantments();
        }

        for (Map.Entry<Enchantment, Integer> entry : itemEnchantments.entrySet()) {
            String enchId = entry.getKey().getKey().toString();
            int level = entry.getValue();
            enchants += enchId + "," + level + ":";
        }
        return enchants;
    }
        
    public static WebItemStack EnchantItem(String ench, WebItemStack stack) {
        if(ench.isEmpty()) return stack;
        ench = ench.replace("minecraft:", "");
        String[] enchs = ench.split(":");

        for (String enchantString:enchs) {
            if(enchantString.equals("")) continue;
            String[] number_level = enchantString.split(",");
            Enchantment enchant = Enchantment.getByKey(NamespacedKey.minecraft(number_level[0]));
            int level = Integer.parseInt(number_level[1]);

            if(stack.getType() == Material.ENCHANTED_BOOK) {
                EnchantmentStorageMeta bookmeta = (EnchantmentStorageMeta)stack.getItemMeta();
                bookmeta.addStoredEnchant(enchant, level, true);
                stack.setItemMeta(bookmeta);
            }else{
                try{
                    stack.addEnchantment(enchant,level);
                }catch(IllegalArgumentException ex) {
                    stack.addUnsafeEnchantment(enchant, level);
                }
            }
        }
        return stack;
    }
}
