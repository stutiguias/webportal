package me.stutiguias.webportal.commands;

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

public class WebPortalCommands implements CommandExecutor {

	private WebPortal plugin;

	public WebPortalCommands(WebPortal plugin) {
		this.plugin = plugin;
	}
        
	@Override
        @EventHandler(priority = EventPriority.NORMAL)
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		int params = args.length;
                
                if(params == 0) {
                    return help(sender);
                }
                
                switch(args[0].toLowerCase())
                {
                    case "reload":
                        return Reload(sender);
                    case "save":
                        return Save(sender);
                    case "help":
                        return help(sender);
                    case "mailbox":
                        return inv(sender);
                    case "password":
                        if(params != 2 && !sender.getName().equalsIgnoreCase("CONSOLE")) {
                            sender.sendMessage(ChatColor.RED + " You need to inform your password");
                            break;
                        }
                        return Password(sender,args[1]);
                    case "view":
                        if(params != 2){
                            sender.sendMessage(ChatColor.RED + " Need to inform the player name");
                            break;
                        }
                        return View(sender, args[1]);
                    case "set":
                        if(params != 4){
                            sender.sendMessage(ChatColor.RED + " Need to inform the player name, option ( buy,sell,admin ) and yes or no");
                            break;
                        }
                        return SetPerm(sender,args[1],args[2],args[3]);
                    default:
                        return CommandNotFound(sender);
                }
		return false;
	}
        
        private boolean CommandNotFound(CommandSender sender) {
            sender.sendMessage(ChatColor.YELLOW + " Command not found try /wa help ");
            return true;
        }
        
        public boolean SetPerm(CommandSender sender,String name,String param,String value){
            if (!sender.hasPermission("wa.set")){
                sender.sendMessage(ChatColor.YELLOW + " You do not have permission");
                return false;
            }
            WebSitePlayer player = plugin.dataQueries.getPlayer(name);
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
            plugin.dataQueries.updatePlayerPermissions(player.getName(), canBuy, canSell, isAdmin);
            return true;
        }
        
        public boolean View(CommandSender sender,String name){
            if (!sender.hasPermission("wa.view")){
                sender.sendMessage(ChatColor.YELLOW + " You do not have permission");
                return false;
            }
            WebSitePlayer player = plugin.dataQueries.getPlayer(name);
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
        
        public boolean Password(CommandSender sender,String pass) {
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
                
                if (plugin.dataQueries.getPlayer(sender.getName()) != null) {
                        sender.sendMessage(ChatColor.YELLOW + " Exist account found.");
                        plugin.dataQueries.updatePlayerPassword(sender.getName(), newPass);
                        sender.sendMessage(ChatColor.YELLOW + " Password changed");
                } else {
                        sender.sendMessage(ChatColor.YELLOW + " Player not found, creating account");
                        plugin.dataQueries.createPlayer(sender.getName(), newPass, canBuy, canSell, isAdmin);
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
        
        public boolean Reload(CommandSender sender){
            if (!sender.hasPermission("wa.reload")){
                sender.sendMessage(ChatColor.YELLOW + " You do not have permission");
                return false;
            }
            sender.sendMessage(ChatColor.YELLOW + "Reloading..");
            plugin.onReload();
            sender.sendMessage(ChatColor.YELLOW + "Finished reloading");
            return true;
        }
        
        public boolean Save(CommandSender sender){
            if (!sender.hasPermission("wa.save")){
                     sender.sendMessage(ChatColor.YELLOW + " You do not have permission");
                     return false;
            }
            sender.sendMessage(ChatColor.YELLOW + "Saving config...");
            plugin.saveConfig();
            sender.sendMessage(ChatColor.YELLOW + "Config Saved");
            return true;
        }

    private boolean help(CommandSender sender) {
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
    
    private boolean inv(CommandSender sender) {
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