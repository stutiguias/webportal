/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.model;

import me.stutiguias.webportal.init.WebPortal;

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
        String result = WebPortal.materials.getConfig().getString("Enchant." + enchant );
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
}
