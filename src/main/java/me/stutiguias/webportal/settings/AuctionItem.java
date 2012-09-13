package me.stutiguias.webportal.settings;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class AuctionItem {

    private int id;
    private int name;
    private int damage;
    private String playerName;
    private int quantity;
    private String Enchantments;
    private String price;
    private String ItemName;
    
    public String getItemName() {
        return ItemName;
    }
    
    public void setItemName(String ItemName) {
        this.ItemName = ItemName;
    }
    
    public String getPrice() {
        return price;
    }
    
    public void setPrice(String price) {
        this.price = price;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getEnchantments() {
        return Enchantments;
    }

    public void setEnchantments(String Enchantments) {
        this.Enchantments = Enchantments;
    }
    
    public ItemStack Chant(String ench,ItemStack stack) {
            if(!ench.equals(""))
            {
                String[] enchs = ench.split(":");
                for (String enchant:enchs) {
                    if(!enchant.equals("")) 
                    {
                        String[] number_level = enchant.split(",");
                        stack.addEnchantment(Enchantment.getById(Integer.parseInt(number_level[0])),Integer.parseInt(number_level[1]));
                    }
                }
            }
            return stack;
    }
}
