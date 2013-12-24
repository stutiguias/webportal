/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.stutiguias.webportal.information;

import me.stutiguias.webportal.init.WebPortal;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Daniel
 */
public class Util extends Converters {
    
    public final WebPortal plugin;
    public CommandSender sender;
    
    public Util(WebPortal plugin) {
        this.plugin = plugin;
    }
    
    public Util(WebPortal plugin,CommandSender sender){
        this.plugin = plugin;
        this.sender = sender;
    }
    
    public static String ToString(Location location) {
        if(location == null)
            return "";
        else
            return String.format("%s,%s,%s,%s,%s,%s", new Object[]{
                location.getX(),location.getY(),location.getZ(),
                location.getYaw(),location.getPitch(),
                location.getWorld().getName()
            });
    }
    
    public static Location toLocation(String location) {
        if(location.isEmpty()) return null;
        String[] loc = location.split(",");
        Double x = Double.parseDouble(loc[0]);
        Double y = Double.parseDouble(loc[1]);
        Double z = Double.parseDouble(loc[2]);
        Float yaw = Float.parseFloat(loc[3]);
        Float pitch = Float.parseFloat(loc[4]);
        World world = Bukkit.getWorld(loc[5]);
        return new Location(world, x, y, z,yaw,pitch);
    }
        
    public void SendMessage(String msg) {
        sender.sendMessage(parseColor(msg));
    }
        
    public void SendMessage(String msg,Object[] args) {
        sender.sendMessage(parseColor(String.format(msg,args)));
    }
    
    public void SendMessage(Player player,String msg) {
        player.sendMessage(parseColor(msg));
    }
        
    public void SendMessage(Player player,String msg,Object[] args) {
        player.sendMessage(parseColor(String.format(msg,args)));
    }
    
    public void BrcstMsg(String msg) {
        plugin.getServer().broadcastMessage(parseColor(msg));
    }
    
    public void BrcstMsg(String msg,Object[] args) {
        plugin.getServer().broadcastMessage(parseColor(String.format(msg,args)));
    }

    public String parseColor(String message) {
         for (ChatColor color : ChatColor.values()) {
            message = message.replaceAll(String.format("&%c", color.getChar()), color.toString());
        }
        return message;
    }
}
