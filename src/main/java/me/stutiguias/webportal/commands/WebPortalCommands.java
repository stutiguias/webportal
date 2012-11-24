package me.stutiguias.webportal.commands;

import java.util.logging.Level;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.settings.Algorithm;
import me.stutiguias.webportal.settings.InventoryHandler;
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

		if (params == 0) {
                        sender.sendMessage(plugin.logPrefix + "Command not found");
			return false;
		} 
                if(params == 1) {
                    if(args[0].equalsIgnoreCase("reload")){
                        Reload(sender);
                    }else if(args[0].equalsIgnoreCase("save")){
                        Save(sender);
                    }else if(args[0].equalsIgnoreCase("help")){
                        help(sender);
                    }else if(args[0].equalsIgnoreCase("mailbox")) {
                        inv(sender);
                    }else{
                        sender.sendMessage(plugin.logPrefix + "Command not found");
                        return false;
                    }
                }else if(params == 2) {
                    if(args[0].equalsIgnoreCase("password")){
                        Password(sender,args[1]);    
                    }else{
                        sender.sendMessage(plugin.logPrefix + "Command not found");
                        return false;
                    }                    
                }else{
                    sender.sendMessage(plugin.logPrefix + "Command not found");
                    return false;
                }
		return false;
	}
        
        public boolean Password(CommandSender sender,String pass) {
            if (pass != null) {
                int canBuy = 0;
                int canSell = 0;
                int isAdmin = 0;
                if (plugin.permission.has(sender.getServer().getWorlds().get(0),sender.getName(), "wa.canbuy")) {
                        canBuy = 1;
                }
                if (plugin.permission.has(sender.getServer().getWorlds().get(0),sender.getName(), "wa.cansell")) {
                        canSell = 1;
                }
                if (plugin.permission.has(sender.getServer().getWorlds().get(0),sender.getName(), "wa.webadmin")) {
                        isAdmin = 1;
                }
                if (null != plugin.dataQueries.getPlayer(sender.getName())) {
                        //no need to create a new account
                        sender.sendMessage(plugin.logPrefix + "Account found.");        
                } else {
                        WebPortal.log.log(Level.INFO, "{0} Player not found, creating account", plugin.logPrefix);
                        plugin.dataQueries.createPlayer(sender.getName(), "Password", 0.0d, canBuy, canSell, isAdmin);
                }
                String newPass = Algorithm.stringHexa(Algorithm.gerarHash(pass,plugin.algorithm));
                plugin.dataQueries.updatePlayerPassword(sender.getName(), newPass);
                sender.sendMessage(plugin.logPrefix + " Password changed");
                return true;
            }else{
                return false;
            }
        }
        
        public boolean Reload(CommandSender sender){
            if (!sender.hasPermission("wa.reload")){
                sender.sendMessage(plugin.logPrefix + "You do not have permission");
                return false;
            }
            sender.sendMessage(plugin.logPrefix + "Reloading..");
            plugin.onReload();
            sender.sendMessage(plugin.logPrefix + "Finished reloading");
            return true;
        }
        
        public boolean Save(CommandSender sender){
            if (!sender.hasPermission("wa.save")){
                     sender.sendMessage(plugin.logPrefix + "You do not have permission");
                     return false;
            }
            sender.sendMessage(plugin.logPrefix + "Saving config..");
            WebPortal.log.log(Level.INFO, "{0} This feature is incomplete", plugin.logPrefix);
            plugin.saveConfig();
            sender.sendMessage(plugin.logPrefix + "Config Saved");
            return true;
        }

    private boolean help(CommandSender sender) {
        if (sender.hasPermission("wa.save")){
            sender.sendMessage("/wa save");
        }
        if (sender.hasPermission("wa.reload")){
            sender.sendMessage("/wa reload");
        }
        if (sender.hasPermission("wa.command.vbox")) {
            sender.sendMessage("/wa mailbox");
        }
        sender.sendMessage("/wa password <password>");
        return true;
    }
    
    private boolean inv(CommandSender sender) {
        if (!sender.hasPermission("wa.command.vbox")) return false;
        if(sender instanceof Player) {
            Player pl = (Player)sender;
            InventoryHandler inventory = new InventoryHandler(plugin,pl);
            pl.openInventory(inventory.getInventory());
        }
        return true;
    }

}