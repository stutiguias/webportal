/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.signs;

import me.stutiguias.webportal.information.Info;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.settings.Auction;
import me.stutiguias.webportal.settings.TradeSystem;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Daniel
 */
public class wSell {
    
    WebPortal plugin;
    Info info;
    
    public wSell(WebPortal instance) {
        plugin = instance;
        info = new Info(plugin);
    }
    
    public void addwSell(String[] lines,Player player,Block sign,SignChangeEvent event) {
        Integer id;
        try {
            id = Integer.parseInt(lines[1]);
        }catch(Exception ex) {
            player.sendMessage("Invalid ID.");
            event.setCancelled(true);
            return;
        }

        Auction auction = plugin.dataQueries.getItemById(id, plugin.Auction);
        if(auction == null) {
            player.sendMessage("Invalid ID");
            event.setCancelled(true);
            return;
        }
        event.setLine(0, ChatColor.GREEN + "[wSell]" );
        event.setLine(1, info.GetItemConfig(auction.getItemStack())[0]);
        if(lines[2].isEmpty()) {
            event.setLine(2,"1-" + auction.getQuantity() + "-" + auction.getPrice());
        }else{
            int qtd = Integer.parseInt(lines[2]);
            if(qtd <= auction.getQuantity()) {
                event.setLine(2,lines[2] + "-" + auction.getQuantity() + "-" + auction.getPrice());
            }else{
                event.setLine(2, ChatColor.RED + "Invalid Qtd");
            }
        }
        event.setLine(3, "" + auction.getId());
    }
    
           
    public void ClickSign(PlayerInteractEvent event,Sign sign,String[] lines) {
        String[] price = lines[2].split("-");
        int qtdnow,qtdsold;
        try {
            qtdnow = plugin.dataQueries.getItemById(Integer.valueOf(lines[3]), plugin.Auction).getQuantity();
            qtdsold = Integer.parseInt(price[0]);
        }catch(Exception ex) {
            event.getPlayer().sendMessage(plugin.logPrefix + "Error try get line of sign");
            event.setCancelled(true);
            return;
        }
        if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            event.getPlayer().sendMessage(plugin.logPrefix + "You want buy " + price[0] + " " + lines[1] + " for " + price[2] + " each ?");
        }else{
            Auction au = plugin.dataQueries.getAuction(Integer.valueOf(lines[3]));

            if(au == null) {
                event.getPlayer().sendMessage(plugin.logPrefix + "No more itens left here!");
                setSignSold(sign);
            }else{
                if(!plugin.economy.has(event.getPlayer().getName(),au.getPrice() * Integer.valueOf(price[0]))) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(plugin.logPrefix + "You don't have enough money");
                    return;
                }
                TradeSystem ts = new TradeSystem(plugin);
                if(!event.getPlayer().getName().equals(au.getPlayerName())) {
                    event.getPlayer().sendMessage(ts.Buy(event.getPlayer().getName(), au, Integer.valueOf(price[0]), lines[1],true));
                    if(( qtdnow - qtdsold ) <= 0) {
                        setSignSold(sign);
                    }else{
                        sign.setLine(2,price[0]+"-"+(qtdnow-qtdsold)+"-"+au.getPrice());
                        sign.update();
                    }
                }else{
                    event.getPlayer().sendMessage(plugin.logPrefix + "You can't buy from yourself");
                }
            }
            event.setCancelled(true);
        }
    }
        
    public void setSignSold(Sign sign) {
        sign.setLine(0,ChatColor.RED + "[wSell]");
        sign.setLine(2,ChatColor.RED + "**SOLD**");
        sign.update();
    }
}
