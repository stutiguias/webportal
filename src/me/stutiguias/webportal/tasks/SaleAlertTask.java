package me.stutiguias.webportal.tasks;

import java.util.List;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.model.SaleAlert;
import org.bukkit.entity.Player;

public class SaleAlertTask implements Runnable {

	private final WebPortal plugin;

	public SaleAlertTask(WebPortal plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		Player[] playerList = plugin.getServer().getOnlinePlayers();
		for (Player player : playerList) {
			List<SaleAlert> newSaleAlerts = plugin.db.getNewSaleAlertsForSeller(player.getName());
			for (SaleAlert saleAlert : newSaleAlerts) {
				String formattedPrice = plugin.economy.format(saleAlert.getPriceEach());
				player.sendMessage("You sold " + saleAlert.getQuantity() + " " + saleAlert.getItem() + " to " + saleAlert.getBuyer()	+ " for " + formattedPrice + " each.");
				plugin.db.markSaleAlertSeen(saleAlert.getId());
			}
		}
	}
}
