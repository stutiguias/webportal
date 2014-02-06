package me.stutiguias.webportal.commands;

import java.util.HashMap;
import me.stutiguias.webportal.init.Util;
import me.stutiguias.webportal.init.WebPortal;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class WebPortalCommands extends Util implements CommandExecutor {

    private String[] args;
    private final HashMap<String,CommandHandler> avaibleCommands;
        
    public WebPortalCommands(WebPortal plugin) {
        super(plugin);
        avaibleCommands = new HashMap<>();
        avaibleCommands.put("reload",new ReloadCommand(plugin));
        avaibleCommands.put("save", new SaveCommand(plugin));
        avaibleCommands.put("help", new HelpCommand(plugin));
        avaibleCommands.put("mailbox", new MailboxCommand(plugin));
        avaibleCommands.put("password", new PasswordCommand(plugin));
        avaibleCommands.put("view", new ViewCommand(plugin));
        avaibleCommands.put("set",new SetCommand(plugin));
    }

    @Override
    @EventHandler(priority = EventPriority.NORMAL)
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

            this.args = args;
            this.sender = sender;
            int paramsLength = args.length;
            
            String executedCommand = args[0].toLowerCase();
            
            if(paramsLength == 0) {
                return avaibleCommands.get("help").OnCommand(sender, args);
            }
            
            if(avaibleCommands.containsKey(executedCommand))
                return avaibleCommands.get(executedCommand).OnCommand(sender,args);
            else
                return CommandNotFound();
    }

    private boolean CommandNotFound() {
        SendMessage("&eCommand not found try /wa help ");
        return true;
    }
 
}