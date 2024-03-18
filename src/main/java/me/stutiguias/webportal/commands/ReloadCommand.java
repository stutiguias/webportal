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
public class ReloadCommand extends CommandHandler {

    public ReloadCommand(WebPortal plugin) {
        super(plugin);
    }

    @Override
    protected Boolean OnCommand(CommandSender sender, String[] args) {
        this.sender = sender;
        
        if (!sender.hasPermission("wa.reload")){
            SendMessage("&e You do not have permission");
            return false;
        }
        
        SendMessage("&eReloading..");
        plugin.onReload();
        SendMessage("&eFinished reloading");
        return true;
    }


}
