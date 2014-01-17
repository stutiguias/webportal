/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.stutiguias.webportal.commands;

import me.stutiguias.webportal.init.WebPortal;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Daniel
 */
public class HelpCommand extends CommandHandler {

    public HelpCommand(WebPortal plugin) {
        super(plugin);
    }

    @Override
    protected Boolean OnCommand(CommandSender sender, String[] args) 
    {
        this.sender = sender;
        
        sender.sendMessage("-----------------"+ChatColor.GOLD+"Help"+ChatColor.WHITE+"---------------------------");
        sender.sendMessage(ChatColor.YELLOW + "All /wa commands");
        if (sender.hasPermission("wa.command.vbox")) {
            sender.sendMessage(ChatColor.YELLOW + "mailbox: Use Mailbox Inventory");
        }
        if (sender.hasPermission("wa.save")){
            sender.sendMessage(ChatColor.RED + "save: Save the All Config File");
        }
        if (sender.hasPermission("wa.reload")){
            sender.sendMessage(ChatColor.RED + "reload: Reload All Config File");
        }
        if (sender.hasPermission("wa.view")) {
            sender.sendMessage(ChatColor.RED + "view <player>: View Player Stats");
        }
        if (sender.hasPermission("wa.set")) {
            sender.sendMessage(ChatColor.RED + "set <player> <option(buy,sell,admin)> <yes/no>: Set Player Perm");
        }
        sender.sendMessage(ChatColor.YELLOW + "password <password>: Change/Create Your Password");
        sender.sendMessage("-----------------------------------------------------");
        sender.sendMessage(ChatColor.YELLOW + "YELLOW: Normal Commands");
        sender.sendMessage(ChatColor.RED + "RED: Admin Commands");
        sender.sendMessage("-----------------------------------------------------");
        return true;
    }
    
}
