/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.settings;

import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Daniel
 */
public class Transact {
    	
        private int id;
        private String itemName;
        private String type;
        private String time;
	private WebItemStack itemStack;
	private String buyer;
        private String seller;
	private double price;
	private int quantity;
	
        
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

	public WebItemStack getItemStack() {
		return itemStack;
	}

	public void setItemStack(WebItemStack itemStack) {
		this.itemStack = itemStack;
	}

	public String getBuyer() {
		return buyer;
	}

	public void setBuyer(String buyer) {
		this.buyer = buyer;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getSeller() {
		return seller;
	}

	public void setSeller(String seller) {
		this.seller = seller;
	}
	
	public String getTime(){
		return time;
	}
	
	public void setTime(String time){
		this.time = time;
	}
	
	public int getQuantity(){
		return quantity;
	}
	
	public void setQuantity(int quantity){
		this.quantity = quantity;
	}
        
}
