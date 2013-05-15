package me.stutiguias.webportal.commands;

import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.settings.AuctionPlayer;
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
                        if(params != 2) {
                            sender.sendMessage(ChatColor.RED + " You need to inform your password");
                            break;
                        }
                        return Password(sender,args[1]);
                    case "view":
                        if(params != 2){
                            sender.sendMessage(ChatColor.RED + " Need to infor the player name");
                            break;
                        }
                        return View(sender, args[1]);
                    default:
                        return CommandNotFound(sender);
                }
		return false;
	}
        
        private boolean CommandNotFound(CommandSender sender) {
            sender.sendMessage(ChatColor.YELLOW + plugin.logPrefix + " Command not found try /wa help ");
            return true;
        }
        
        public boolean View(CommandSender sender,String name){
            if (!sender.hasPermission("wa.view")){
                sender.sendMessage(ChatColor.YELLOW + plugin.logPrefix + " You do not have permission");
                return false;
            }
            AuctionPlayer player = plugin.dataQueries.getPlayer(name);
            sender.sendMessage("-----------------------------------------------------");
            if(player == null) {
                sender.sendMessage(ChatColor.YELLOW + " Player Not Found");
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
                        plugin.dataQueries.createPlayer(sender.getName(), newPass, 0.0d, canBuy, canSell, isAdmin);
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
                sender.sendMessage(ChatColor.YELLOW + plugin.logPrefix + " You do not have permission");
                return false;
            }
            sender.sendMessage(ChatColor.YELLOW + plugin.logPrefix + "Reloading..");
            plugin.onReload();
            sender.sendMessage(ChatColor.YELLOW + plugin.logPrefix + "Finished reloading");
            return true;
        }
        
        public boolean Save(CommandSender sender){
            if (!sender.hasPermission("wa.save")){
                     sender.sendMessage(ChatColor.YELLOW + plugin.logPrefix + " You do not have permission");
                     return false;
            }
            sender.sendMessage(ChatColor.YELLOW + plugin.logPrefix + "Saving config...");
            plugin.saveConfig();
            sender.sendMessage(ChatColor.YELLOW + plugin.logPrefix + "Config Saved");
            return true;
        }

    private boolean help(CommandSender sender) {
        sender.sendMessage("-----------------------------------------------------");
        if (sender.hasPermission("wa.save")){
            sender.sendMessage(ChatColor.RED + "/wa save | Save the All Config File");
        }
        if (sender.hasPermission("wa.reload")){
            sender.sendMessage(ChatColor.RED + "/wa reload | Reload All Config File");
        }
        if (sender.hasPermission("wa.command.vbox")) {
            sender.sendMessage(ChatColor.YELLOW + "/wa mailbox | Use Mailbox Inventory");
        }
        sender.sendMessage(ChatColor.YELLOW + "/wa password <password> | Change/Create Your Password");
        sender.sendMessage("-----------------------------------------------------");
        sender.sendMessage(ChatColor.YELLOW + "YELLOW : Normal Comands");
        sender.sendMessage(ChatColor.RED + "RED : Admin Comands");
        sender.sendMessage("-----------------------------------------------------");
        return true;
    }
    
    private boolean inv(CommandSender sender) {
        if (!sender.hasPermission("wa.command.vbox")){
            sender.sendMessage(ChatColor.YELLOW + plugin.logPrefix + " You do not have permission");
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