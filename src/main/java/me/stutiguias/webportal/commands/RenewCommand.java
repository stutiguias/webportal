package me.stutiguias.webportal.commands;

import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.model.Shop;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RenewCommand extends CommandHandler {

    public RenewCommand(WebPortal plugin) {
        super(plugin);
    }

    @Override
    public Boolean OnCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        
        if (!plugin.hasPermission(player, "wa.renew")) {
            SendMessage(player, "&cYou don't have permission to renew listings.");
            return true;
        }

        if (args.length < 2) {
            SendMessage(player, "&eUsage: /wa renew <ID>");
            SendMessage(player, "&eExample: /wa renew 123");
            return true;
        }

        int itemId;
        try {
            itemId = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            SendMessage(player, "&cInvalid ID. Use a number.");
            return true;
        }

        // Search item in Sell or Buy
        Shop item = plugin.db.getItemById(itemId, plugin.Sell);
        if (item == null) {
            item = plugin.db.getItemById(itemId, plugin.Buy);
        }

        if (item == null) {
            SendMessage(player, "&cItem with ID " + itemId + " not found.");
            return true;
        }

        if (!item.getPlayerName().equalsIgnoreCase(player.getName())) {
            SendMessage(player, "&cYou can only renew your own items.");
            return true;
        }

        // Renew item by updating timestamp
        long currentTime = System.currentTimeMillis() / 1000;
        boolean renewed = plugin.db.renewItemListing(itemId, (int) currentTime);

        if (renewed) {
            SendMessage(player, "&aItem #" + itemId + " renewed successfully!");
            SendMessage(player, "&7New expiration date: " + plugin.itemExpiryHours + " hours from now.");
        } else {
            SendMessage(player, "&cError renewing item. Please try again.");
        }

        return true;
    }
}
