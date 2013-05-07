package me.stutiguias.webportal.settings;

import org.bukkit.inventory.ItemStack;

public class Auction {

	private int id;
        private int name;
        private int damage;
        private int quantity;
        private String itemName;
        private String type;
	private ItemStack itemStack;
	private String playerName;
	private double price;
	private int created;
	private Boolean allowBids;
	private Double currentBid;
	private String currentWinner;
        private String Enchantments;
        
        public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
        
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

	public ItemStack getItemStack() {
		return itemStack;
	}

	public void setItemStack(ItemStack itemStack) {
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
	
	public Boolean getAllowBids(){
		return allowBids;
	}
	
	public void setAllowBids(Boolean bid){
		this.allowBids = bid;
	}
	
	public Double getCurrentBid(){
		return currentBid;
	}
	
	public void setCurrentBid(Double bid){
		this.currentBid = bid;
	}
	
	public String getCurrentWinner(){
		return currentWinner;
	}
	
	public void setCurrentWinner(String player){
		this.currentWinner = player;
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
}
