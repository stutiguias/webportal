package me.stutiguias.webportal.commands;

import me.stutiguias.webportal.init.Util;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.settings.WebSitePlayer;
import me.stutiguias.webportal.settings.InventoryHandler;
import me.stutiguias.webportal.webserver.authentication.Algorithm;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class WebPortalCommands extends Util implements CommandExecutor {

    private String[] args;

    public WebPortalCommands(WebPortal plugin) {
        super(plugin);
    }

    @Override
    @EventHandler(priority = EventPriority.NORMAL)
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

            this.args = args;
            this.sender = sender;
            int params = args.length;

            if(params == 0) {
                return help();
            }

            switch(args[0].toLowerCase())
            {
                case "reload":
                    return Reload();
                case "save":
                    return Save();
                case "help":
                    return help();
                case "mailbox":
                    return inventoryOpen();
                case "password":
                    return Password();
                case "view":
                    return View();
                case "set":
                    return SetPerm();
                default:
                    return CommandNotFound();
            }
    }

    private boolean CommandNotFound() {
        SendMessage("&eCommand not found try /wa help ");
        return true;
    }

    public boolean SetPerm(){
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
        
    public boolean View(){
        
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
        
    public boolean Password() {

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
        
    public boolean Reload(){
        if (!sender.hasPermission("wa.reload")){
            sender.sendMessage(ChatColor.YELLOW + " You do not have permission");
            return false;
        }
        sender.sendMessage(ChatColor.YELLOW + "Reloading..");
        plugin.onReload();
        sender.sendMessage(ChatColor.YELLOW + "Finished reloading");
        return true;
    }

    public boolean Save(){
        if (!sender.hasPermission("wa.save")){
                 sender.sendMessage(ChatColor.YELLOW + " You do not have permission");
                 return false;
        }
        sender.sendMessage(ChatColor.YELLOW + "Saving config...");
        plugin.saveConfig();
        sender.sendMessage(ChatColor.YELLOW + "Config Saved");
        return true;
    }

    private boolean help() {
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
    
    private boolean inventoryOpen() {
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