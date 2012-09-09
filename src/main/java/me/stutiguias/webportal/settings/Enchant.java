/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.settings;

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
    
    public String getEnchantName(int enchant,int level){
         String result = "";
         switch (enchant) {
            case 0: result = "Protection"; break;
            case 1: result = "Fire Protecion"; break;
            case 2: result = "Feather Falling"; break;
            case 3: result = "Blast Protection"; break;
            case 4: result = "Projectile Protection"; break;
            case 5: result = "Respiration"; break;
            case 6: result = "Aqua Affinity"; break;
            case 16: result = "Sharpness"; break;
            case 17: result = "Smite"; break;
            case 18: result = "Bane of Arthropods"; break;
            case 19: result = "Knockback"; break;
            case 20: result = "Fire Aspect"; break;
            case 21: result = "Looting"; break;
            case 32: result = "Efficiency"; break;
            case 33: result = "SilkTouch"; break;
            case 34: result = "Unbreaking"; break;
            case 35: result = "Fortune"; break;
            case 48: result = "Power"; break;
            case 49: result = "Punch"; break;
            case 50: result = "Flame"; break;
            case -1: result = "Unknown"; break;                                  
            default:
                result = "Unknown";break;
        }
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
            default:
                break;
        }
        
        return result;
    }
}
