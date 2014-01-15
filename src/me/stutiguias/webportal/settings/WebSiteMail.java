package me.stutiguias.webportal.settings;

import org.bukkit.inventory.ItemStack;

public class WebSiteMail {

	private int id;
	private WebItemStack itemStack;
	private String playerName;

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
}
