/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.stutiguias.webportal.commands;

import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.webserver.authentication.Algorithm;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Daniel
 */
public class PasswordCommand extends CommandHandler {

    public PasswordCommand(WebPortal plugin) {
        super(plugin);
    }

    @Override
    protected Boolean OnCommand(CommandSender sender, String[] args) {
              
        if(sender.getName().equalsIgnoreCase("CONSOLE")) {
            SendMessage("&4You need to be player");
            return true;                
        }

        if(args.length != 2) {
            SendMessage("&4You need to inform your password");
            return true;
        }

        String pass = args[1];

        if (pass != null) {
            int canBuy = 0;
            int canSell = 0;
            int isAdmin = 0;
            if (plugin.permission.has(sender, "wa.canbuy")) {
                    canBuy = 1;
            }
            if (plugin.permission.has(sender, "wa.cansell")) {
                    canSell = 1;
            }
            if (plugin.permission.has(sender, "wa.webadmin")) {
                    isAdmin = 1;
            }
            sender.sendMessage("-----------------------------------------------------");
            if(canBuy == 0 && canSell == 0 && isAdmin == 0) {
                sender.sendMessage(ChatColor.YELLOW + " you don't have any permission.");        
                return false;
            }

            String newPass = Algorithm.stringHexa(Algorithm.gerarHash(pass,plugin.algorithm));

            if (plugin.db.getPlayer(sender.getName()) != null) {
                    sender.sendMessage(ChatColor.YELLOW + " Exist account found.");
                    plugin.db.updatePlayerPassword(sender.getName(), newPass);
                    sender.sendMessage(ChatColor.YELLOW + " Password changed");
            } else {
                    sender.sendMessage(ChatColor.YELLOW + " Player not found, creating account");
                    plugin.db.createPlayer(sender.getName(), newPass, canBuy, canSell, isAdmin);
                    sender.sendMessage(ChatColor.YELLOW + " Can Buy ?" + ( (canBuy == 1) ? "YES" : "NO" ) );
                    sender.sendMessage(ChatColor.YELLOW + " Can Sell ?" + ( (canSell == 1) ? "YES" : "NO" ) );
                    sender.sendMessage(ChatColor.YELLOW + " Is Admin ?" + ( (isAdmin == 1) ? "YES" : "NO" ) );
                    sender.sendMessage(ChatColor.YELLOW + " Account Created!");
            }
            sender.sendMessage("-----------------------------------------------------");
            return true;
        }else{
            return false;
        }
    }
    
}
