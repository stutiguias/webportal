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
public class SaveCommand extends CommandHandler {

    public SaveCommand(WebPortal plugin) {
        super(plugin);
    }

    @Override
    protected Boolean OnCommand(CommandSender sender, String[] args) {
        this.sender = sender;
        
        if (!sender.hasPermission("wa.save")){
                 SendMessage("&e You do not have permission");
                 return false;
        }
        SendMessage("&e Saving config...");
        plugin.saveConfig();
        SendMessage("&e Config Saved");
        return true;
    }


}
