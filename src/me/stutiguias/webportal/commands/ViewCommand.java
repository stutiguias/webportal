/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.stutiguias.webportal.commands;

import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.settings.WebSitePlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Daniel
 */
public class ViewCommand extends CommandHandler {

    public ViewCommand(WebPortal plugin) {
        super(plugin);
    }

    @Override
    protected Boolean OnCommand(CommandSender sender, String[] args) {
                
        if(args.length != 2){
            SendMessage("&4 Need to inform the player name");
            return true;
        }
        
        if (!sender.hasPermission("wa.view")){
            SendMessage("&e You do not have permission");
            return true;
        }
        
        String name = args[1];
        
        WebSitePlayer player = plugin.db.getPlayer(name);
        sender.sendMessage("-----------------------------------------------------");
        if(player == null) {
            sender.sendMessage(ChatColor.YELLOW + " Player Not Found");
            sender.sendMessage("-----------------------------------------------------");
            return false;
        }
        sender.sendMessage(ChatColor.YELLOW + " Player - " + player.getName());
        sender.sendMessage(ChatColor.YELLOW + " Can Buy ?" + ( (player.getCanBuy() == 1) ? "YES" : "NO" ) );
        sender.sendMessage(ChatColor.YELLOW + " Can Sell ?" + ( (player.getCanSell() == 1) ? "YES" : "NO" ) );
        sender.sendMessage(ChatColor.YELLOW + " Is Admin ?" + ( (player.getIsAdmin() == 1) ? "YES" : "NO" ) );
        sender.sendMessage("-----------------------------------------------------");
        return true;
    }
    
}
