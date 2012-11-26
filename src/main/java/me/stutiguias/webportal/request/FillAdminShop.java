/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.request;

import java.net.Socket;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.webserver.Material;
import me.stutiguias.webportal.webserver.Response;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Daniel
 */
public class FillAdminShop extends Response {
    
    WebPortal plugin;
    
    public FillAdminShop(WebPortal instance,Socket s) {
        super(instance,s);
        plugin = instance;
    }
    
    public void AddShop(String ip,String url,String param){
        if(isAdmin(ip)){
            ItemStack Item = ConvertToItemStack(getParam("ItemId", param));
            if(Item == null) print("Item ID not found","text/html");
            Double Price = Double.parseDouble(getParam("Price", param));
            String type = Item.getType().toString();
            String ItemName = Material.getItemName(Item.getTypeId(),Item.getDurability());
            String searchtype = plugin.getSearchType(ItemName);
            plugin.dataQueries.createItem(Item.getTypeId(), Item.getDurability(), "Server", 9999, Price,"", plugin.Auction, type, ItemName, searchtype );
            print("ok","text/html");
        }else{
            print("You r not admin","text/html");
        }
    }
    
    public void list(String ip,String url,String param){
        if(isAdmin(ip)) {
            
        }
        print("list","text/html");
    }
    
    public ItemStack ConvertToItemStack(String ItemId) {
        Integer Name;
        Short Damage;
        if(ItemId.contains(":")) {
            String[] NameDamage = ItemId.split(":");
            Name = Integer.parseInt(NameDamage[0]);
            Damage = Short.parseShort(NameDamage[1]);
        }else{
            Name = Integer.parseInt(ItemId);
            Damage = 0;
        }
        ItemStack item = new ItemStack(Name ,1,Damage);
        return item; 
    }
}
