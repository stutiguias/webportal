package me.stutiguias.webportal.settings;

public class Shop {

    private int id;
    private int name;
    private int damage;
    private int quantity;
    private String type;
    private WebItemStack itemStack;
    private String playerName;
    private double price;
    private int created;
    private String Enchantments;
    private int tableId;
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public WebItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(WebItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public String getEnchantments() {
        return Enchantments;
    }

    public void setEnchantments(String Enchantments) {
        this.Enchantments = Enchantments;
    }

    /**
     * @return the name
     */
    public int getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(int name) {
        this.name = name;
    }

    /**
     * @return the damage
     */
    public int getDamage() {
        return damage;
    }

    /**
     * @param damage the damage to set
     */
    public void setDamage(int damage) {
        this.damage = damage;
    }

    /**
     * @return the quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * @param quantity the quantity to set
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * @return the tableId
     */
    public int getTableId() {
        return tableId;
    }

    /**
     * @param tableId the tableId to set
     */
    public void setTableId(int tableId) {
        this.tableId = tableId;
    }
    
}
