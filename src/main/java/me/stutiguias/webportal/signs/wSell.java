/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.signs;

import me.stutiguias.webportal.init.Util;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.model.Shop;
import me.stutiguias.webportal.trade.TradeHandle;
import me.stutiguias.webportal.trade.Transaction;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Daniel
 */
public class wSell extends Util {


    public wSell(WebPortal instance) {
        super(instance);
    }
    
    public void addwSell(String[] lines,Player player,Block sign,SignChangeEvent event) {
        Integer id;
        try {
            id = Integer.parseInt(lines[1]);
        }catch(NumberFormatException ex) {
            player.sendMessage("Invalid ID.");
            event.setCancelled(true);
            return;
        }

        Shop auction = plugin.db.getItemById(id, plugin.Sell);
        
        if(auction == null) {
            player.sendMessage("Invalid ID");
            event.setCancelled(true);
            return;
        }
        
        event.setLine(0, ChatColor.GREEN + "[wSell]" );
        event.setLine(1, auction.getItemStack().getName());
        if(lines[2].isEmpty()) {
            event.setLine(2,"1-" + auction.getQuantity() + "-" + auction.getPrice());
        }else{
            int qtd = Integer.parseInt(lines[2]);
            if(qtd <= auction.getQuantity() && qtd > 0) {
                event.setLine(2,lines[2] + "-" + auction.getQuantity() + "-" + auction.getPrice());
            }else{
                event.setLine(2, ChatColor.RED + "Invalid Qtd");
                event.setCancelled(true);
                return;
            }
        }
        event.setLine(3, "" + auction.getId());
    }
    
           
    public void ClickSign(PlayerInteractEvent event,Sign sign,String[] lines) {
        String[] price = lines[2].split("-");
        Shop au = plugin.db.getItemById(Integer.parseInt(lines[3]), plugin.Sell);
        if(au == null) {
            event.getPlayer().sendMessage(plugin.logPrefix + " No more itens left here!");
            setSignSold(sign.getTargetSide(event.getPlayer()));
            sign.update();
            event.setCancelled(true);
            return;
        }
        int qtdnow = plugin.db.getItemById(Integer.parseInt(lines[3]), plugin.Sell).getQuantity();
        int qtdsold = Integer.parseInt(price[0]);
        if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            event.getPlayer().sendMessage(plugin.logPrefix + " You want buy " + price[0] + " " + lines[1] + " for " + price[2] + " each ?");
        }else{

            if(!plugin.economy.has(event.getPlayer(),au.getPrice() * Integer.parseInt(price[0]))) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(plugin.logPrefix + " You don't have enough money");
                return;
            }

            if(!event.getPlayer().getName().equals(au.getPlayerName())) {
                event.getPlayer().sendMessage(new Transaction(plugin).Buy(event.getPlayer().getName(), au, Integer.parseInt(price[0])));
                if(( qtdnow - qtdsold ) <= 0) {
                    setSignSold(sign.getTargetSide(event.getPlayer()));
                }else{
                    sign.getTargetSide(event.getPlayer()).setLine(2,price[0]+"-"+(qtdnow-qtdsold)+"-"+au.getPrice());
                    sign.update();
                }
            }else{
                event.getPlayer().sendMessage(plugin.logPrefix + " You can't buy from yourself");
            }

            event.setCancelled(true);
        }
    }
        
    public void setSignSold(SignSide sign) {
        sign.setLine(0,ChatColor.RED + "[wSell]");
        sign.setLine(2,ChatColor.RED + "**SOLD**");
    }
}
