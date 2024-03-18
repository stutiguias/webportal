/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.stutiguias.webportal.commands;

import me.stutiguias.webportal.init.Util;
import me.stutiguias.webportal.init.WebPortal;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Daniel
 */
public abstract class CommandHandler extends Util {   

    public CommandHandler(WebPortal plugin) {
        super(plugin);
    }
    
    protected abstract Boolean OnCommand(CommandSender sender, String[] args);
}
