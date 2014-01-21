/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.stutiguias.webportal.commands;

import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.inventory.InventoryHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Daniel
 */
public class MailboxCommand extends CommandHandler {

    public MailboxCommand(WebPortal plugin) {
        super(plugin);
    }

    @Override
    protected Boolean OnCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("wa.command.vbox")){
            sender.sendMessage(ChatColor.YELLOW + " You do not have permission");
            return false;
        }
            
        if(sender instanceof Player) {
            Player pl = (Player)sender;
            InventoryHandler inventory = new InventoryHandler(plugin,pl);
            pl.openInventory(inventory.getInventory());
        }
        return true;
    }
    
}
