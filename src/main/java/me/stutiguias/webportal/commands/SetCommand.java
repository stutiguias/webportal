/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.stutiguias.webportal.commands;

import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.model.WebSitePlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Daniel
 */
public class SetCommand extends CommandHandler {

    public SetCommand(WebPortal plugin) {
        super(plugin);
    }

    @Override
    protected Boolean OnCommand(CommandSender sender, String[] args) {
        this.sender = sender;
        
        if(args.length != 4){
            SendMessage("&4 Need to inform the player name, option ( buy,sell,admin ) and yes or no");
            return true;
        }
        
        if (!sender.hasPermission("wa.set")){
            sender.sendMessage(ChatColor.YELLOW + " You do not have permission");
            return true;
        }
        
        String name = args[1];
        String param = args[2];
        String value = args[3];
        
        WebSitePlayer player = plugin.db.getPlayer(name);
        sender.sendMessage("-----------------------------------------------------");
        if(player == null) {
            sender.sendMessage(ChatColor.YELLOW + WebPortal.Messages.WebPlayerNotFound);
            sender.sendMessage("-----------------------------------------------------");
            return false;
        }
        int canBuy = player.getCanBuy();
        int canSell = player.getCanSell();
        int isAdmin = player.getIsAdmin();
        sender.sendMessage(ChatColor.YELLOW + " Player - " + player.getName());
        switch(param){
            case "buy":
                canBuy = ( (value.equalsIgnoreCase("yes"))? 1 : 0 ) ;
                sender.sendMessage(ChatColor.YELLOW + " Can Buy Altered to " + ( (canBuy == 1) ? "YES" : "NO" ) );
                break;
            case "sell":
                canSell = ( (value.equalsIgnoreCase("yes"))? 1 : 0 ) ;
                sender.sendMessage(ChatColor.YELLOW + " Can Sell Altered to " + ( (canSell == 1) ? "YES" : "NO" ) );
                break;
            case "admin":
                isAdmin = ( (value.equalsIgnoreCase("yes"))? 1 : 0 ) ;
                sender.sendMessage(ChatColor.YELLOW + " Is Admin Altered to " + ( (isAdmin == 1) ? "YES" : "NO" ) );
                break;
            default:
                sender.sendMessage(ChatColor.YELLOW + " Invalid Param");

        }
        sender.sendMessage("-----------------------------------------------------");
        plugin.db.updatePlayerPermissions(player.getName(), canBuy, canSell, isAdmin);
        return true;
    }
    
}
