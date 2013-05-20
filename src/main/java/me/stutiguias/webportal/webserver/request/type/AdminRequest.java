/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.request.type;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.settings.*;
import me.stutiguias.webportal.webserver.Html;
import me.stutiguias.webportal.webserver.HttpResponse;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Daniel
 */
public class AdminRequest extends HttpResponse {
    
    WebPortal plugin;
    AuctionPlayer _authPlayer;
    List<Auction> _playerItems;
    List<AuctionMail> _playerMail;
    List<Auction> _PlayerAuction;
    
    public AdminRequest(WebPortal plugin) {
        super(plugin);
        this.plugin = plugin;
    }
    
    public void AdmGetInfo(String Hostadress,Map param) {
        if(isAdmin(Hostadress)) {
            String name = (String)param.get("nick");
            String info = (String)param.get("information");
            if(info.equalsIgnoreCase("playerinfo")) playerinfo(Hostadress,name);
            if(info.equalsIgnoreCase("playeritems")) playeritems(name);
            if(info.equalsIgnoreCase("playermail")) playermails(name);
            if(info.equalsIgnoreCase("playerauctions")) playerauction(name);
            if(info.equalsIgnoreCase("playertransaction")) playertransaction(name);
        }else{
            Print("Your r not admin","text/html");
        }
    }

    public void AdmGetServerInfo(String Hostadress) {
        if(!isAdmin(Hostadress)) {
            Print("Your r not admin","text/html");
            return;
        }
        JSONArray jsonarray = new JSONArray();
        JSONObject json = new JSONObject();
        Server server = plugin.getServer();
        json.put("IP",server.getIp() + ":" + server.getPort());
        json.put("Name",server.getServerName());
        json.put("Server Bukkit Version",server.getBukkitVersion());
        json.put("Server Version",server.getVersion());
        json.put("Online Players",server.getOnlinePlayers().length);
        json.put("Operators",server.getOperators().size());
        json.put("Mem. used/max", String.valueOf((Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/ 1024 / 1024) + " / " + String.valueOf(Runtime.getRuntime().maxMemory() / 1024 / 1024) );
        json.put("Mem. free/total", String.valueOf(Runtime.getRuntime().freeMemory() / 1024 / 1024) + " / " + String.valueOf(Runtime.getRuntime().totalMemory() / 1024 / 1024) );
        jsonarray.add(json);
        Print(jsonarray.toJSONString(),"application/json");
    }
    
    public void PlayersOnline(String Hostaddress) {
        if(!isAdmin(Hostaddress)) {
            Print("Your r not admin","text/html");
            return;
        }
        JSONArray jsonarray = new JSONArray();
        JSONObject json;
        Player[] players = plugin.getServer().getOnlinePlayers();
        for (int i = 0; i < players.length; i++) {
            json = new JSONObject();
            json.put("Name",players[i].getName());
            Location loc = players[i].getLocation();
            json.put("Location",loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ());
            json.put("GameMode",players[i].getGameMode().toString());
            json.put("is OP ?",String.valueOf(players[i].isOp()));
            json.put("Ip",String.valueOf(players[i].getAddress()));
            json.put("Kick","<button type=\"button\" onclick=\"return kick('"+players[i].getName()+"')\" class=\"btn\" style=\"margin:10px\">Kick</button>");
            json.put("Ban","<button type=\"button\" onclick=\"return ban('"+players[i].getName()+"')\" class=\"btn\" style=\"margin:10px\">Ban</button>");
            jsonarray.add(json);
        }
        Print(jsonarray.toJSONString(),"application/json");
    }
    
    public void BanList(String Hostaddress) {
        if(!isAdmin(Hostaddress)) {
            Print("Your r not admin","text/html");
            return;
        }
        JSONArray jsonarray = new JSONArray();
        JSONObject json;
        Set<OfflinePlayer> players = plugin.getServer().getBannedPlayers();
        for (OfflinePlayer offlinePlayer : players) {
            json = new JSONObject();
            json.put("Name",offlinePlayer.getName());
            json.put("UnBan","<button type=\"button\" onclick=\"return uban('"+offlinePlayer.getName()+"')\" class=\"btn\" style=\"margin:10px\">UnBan</button>");
            jsonarray.add(json);
        }
        Print(jsonarray.toJSONString(),"application/json");
    }
    
    public void Kick(String Hostaddress,Map param) {
        if(!isAdmin(Hostaddress)) {
            Print("Your r not admin","text/html");
            return;
        }
        String name = WebPortal.AuthPlayers.get(Hostaddress).AuctionPlayer.getName();
        String setplayer = (String)param.get("player");
        Player player = plugin.getServer().getPlayer(setplayer);
        player.kickPlayer("Kick by Admin " + name);
        Print("Player kicked","text/plain");
    }
    
    public void Ban(String Hostaddress,Map param) {
        if(!isAdmin(Hostaddress)) {
            Print("Your r not admin","text/html");
            return;
        }
        String setplayer = (String)param.get("player");
        Player player = plugin.getServer().getPlayer(setplayer);
        player.setBanned(true);
        String name = WebPortal.AuthPlayers.get(Hostaddress).AuctionPlayer.getName();
        player.kickPlayer("Banned by Admin " + name);
        Print("Player Banned","text/plain");
    }
    
    public void UnBan(String Hostaddress,Map param) {
        if(!isAdmin(Hostaddress)) {
            Print("Your r not admin","text/html");
            return;
        }
        String setplayer = (String)param.get("player");
        OfflinePlayer player = plugin.getServer().getOfflinePlayer(setplayer);
        player.setBanned(false);
        Print("Player UnBanned","text/plain");
    }
    
    public void AdmViewPlugins(String Hostaddress) {
        if(!isAdmin(Hostaddress)) {
            Print("Your r not admin","text/html");
            return;
        }
        try {
            Server server = plugin.getServer();
            Plugin[] plugins = server.getPluginManager().getPlugins();
            JSONArray jsonarray = new JSONArray();
            JSONObject jsonObjectArray;
            for (int i = 0; i < server.getPluginManager().getPlugins().length; i++) {
                jsonObjectArray = new JSONObject();
                jsonObjectArray.put("Plugin Name",plugins[i].getDescription().getName());
                jsonObjectArray.put("Version",plugins[i].getDescription().getVersion());
                jsonObjectArray.put("isEnable ?",plugins[i].isEnabled());
                if(plugins[i].getDescription().getDepend() != null) {
                    for(int x = 0;x < plugins[i].getDescription().getDepend().size(); x++) {
                        jsonObjectArray.put("Depend",plugins[i].getDescription().getDepend().get(x).toString());
                    }
                }else{
                    jsonObjectArray.put("Depend","");
                }
                if(plugins[i].getDescription().getSoftDepend() != null) {
                    for(int x = 0;x < plugins[i].getDescription().getSoftDepend().size(); x++) {
                        jsonObjectArray.put("Soft Depend",plugins[i].getDescription().getSoftDepend().get(x).toString());
                    }
                }else{
                    jsonObjectArray.put("Soft Depend","");
                }
                jsonarray.add(jsonObjectArray);
            }
            Print(jsonarray.toJSONString(),"application/json");
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void SendMsg(String Hostaddress,Map params){
        if(!isAdmin(Hostaddress)) {
            Print("Your r not admin","text/html");
            return;
        }
        String msg = (String)params.get("msg");
        String name = WebPortal.AuthPlayers.get(Hostaddress).AuctionPlayer.getName();
        plugin.getServer().broadcastMessage(ChatColor.YELLOW + name + " : " + msg);
        Print(msg +  " Message Send","text/plain");
    }
    
    public void ShutDown(String Hostaddress){
        if(!isAdmin(Hostaddress)) {
            Print("Your r not admin","text/html");
            return;
        }
        Print("ShutDown Send","text/plain");
        plugin.getServer().shutdown();
    }
    
    public void Reload(String Hostaddress) {
        if(!isAdmin(Hostaddress)) {
            Print("Your r not admin","text/html");
            return;
        }
        Print("Reload Send","text/plain");
        plugin.getServer().reload();
    }
    
    public void Command(String Hostaddress,Map params) {
        if(!isAdmin(Hostaddress)) {
            Print("Your r not admin","text/html");
            return;
        }
        String line = "";
        JSONArray console = new JSONArray();
        try
        { 
            String cmd = (String)params.get("cmd");
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),cmd);
            File f = new File("server.log");
            RandomAccessFile randomFile = new RandomAccessFile(f, "r");
            long numberOfLines = Long.valueOf(14).longValue();
            long fileLength = randomFile.length();
            long startPosition = fileLength - (numberOfLines * 100);
            if(startPosition < 0)  startPosition = 0;
            randomFile.seek(startPosition);

            while( ( line = randomFile.readLine() ) != null ){
                    console.add(line);
            }
            randomFile.close();
        }catch(Exception e){ 
            e.printStackTrace();
        }
        Print(console.toJSONString(),"text/plain");
    }
    
    public void SeeConsole(String Hostaddress) {
        if(!isAdmin(Hostaddress)) {
            Print("Your r not admin","text/html");
            return;
        }
        String line;
        JSONArray console = new JSONArray();
        try
        { 
            File f = new File("server.log");
            RandomAccessFile randomFile = new RandomAccessFile(f, "r");
            long numberOfLines = Long.valueOf(14).longValue();
            long fileLength = randomFile.length();
            long startPosition = fileLength - (numberOfLines * 100);
            if(startPosition < 0)  startPosition = 0;
            randomFile.seek(startPosition);

            while( ( line = randomFile.readLine() ) != null ){
                    console.add(line);
            }
            randomFile.close();
        }catch(Exception e){ 
            e.printStackTrace();
        }
        Print(console.toJSONString(),"text/plain");
    }
    
    private void playerinfo(String SessionId,String partialName) {
        if(!isAdmin(SessionId)) {
            Print("Your r not admin","text/html");
            return;
        }
        List<AuctionPlayer> players = plugin.dataQueries.FindAllPlayersWith(partialName);
        if(players == null) {
            Print("Player Not Found","text/html");
        }else{
            JSONArray jsonarray = new JSONArray();
            JSONObject json;
            for (int i = 0; i < players.size(); i++) {
                json = new JSONObject();
                json.put("IP",players.get(i).getIp());
                json.put("Name",players.get(i).getName());
                json.put("Can Buy ?",players.get(i).getCanBuy());
                json.put("Can Sell ?",players.get(i).getCanSell());
                json.put("is Admin ?",players.get(i).getIsAdmin());
                json.put("Banned ?","");
                json.put("WebSite Ban", new Html(plugin).HTMLBan(SessionId,players.get(i).getId()) );
                jsonarray.add(json);
            }
            Print(jsonarray.toJSONString(),"application/json");
        }
    }
    
    private void playertransaction(String name) {
        List<Transact> Transacts = plugin.dataQueries.GetTransactOfPlayer(name);
        JSONArray jsonarray = new JSONArray();
        JSONObject jsonObjectArray;
        for (int i = 0; i < Transacts.size(); i++) {

            Transact transact = Transacts.get(i);
            String itemname = GetItemConfig(transact.getItemStack())[0];
   
            jsonObjectArray = new JSONObject();
            jsonObjectArray.put("Buyer",transact.getBuyer());
            jsonObjectArray.put("Item Name",itemname);
            jsonObjectArray.put("Price",transact.getPrice());
            jsonObjectArray.put("Quantity",transact.getQuantity());
            jsonObjectArray.put("Seller", transact.getSeller());
            jsonarray.add(jsonObjectArray);
            
        }
        Print(jsonarray.toJSONString(),"application/json");
    }
    
    private void playeritems(String name) {
        _playerItems = plugin.dataQueries.getAuctionsLimitbyPlayer(name,0,2000,plugin.Myitems);

        JSONArray jsonarray = new JSONArray();
        JSONObject jsonObjectArray;
        for (int i = 0; i < _playerItems.size(); i++) {
            Auction _Auction = _playerItems.get(i);
            String itemname = GetItemConfig(_Auction.getItemStack())[0];
            jsonObjectArray = new JSONObject();
            jsonObjectArray.put("Nick",_Auction.getPlayerName());
            jsonObjectArray.put("Item Name",itemname);
            jsonObjectArray.put("Quantity",_Auction.getItemStack().getAmount());
            jsonarray.add(jsonObjectArray);
        }
        Print(jsonarray.toJSONString(),"application/json");
    }
    
    private void playermails(String name) {
        _playerMail = plugin.dataQueries.getMail(name);
        
        JSONArray jsonarray = new JSONArray();
        JSONObject jsonObjectArray;
  
        for (int i = 0; i < _playerMail.size(); i++) {
            AuctionMail auctionMail = _playerMail.get(i);
            String itemname = GetItemConfig(auctionMail.getItemStack())[0];
            jsonObjectArray = new JSONObject();
            jsonObjectArray.put("Nick",auctionMail.getPlayerName());
            jsonObjectArray.put("Item Name",itemname );
            jsonObjectArray.put("Quantity", auctionMail.getItemStack().getAmount());
            jsonarray.add(jsonObjectArray);
        }

        Print(jsonarray.toJSONString(),"application/json");
    }
        
    private void playerauction(String name) {
        _PlayerAuction = plugin.dataQueries.getAuctionsLimitbyPlayer(name,0,2000,plugin.Auction);
        JSONArray jsonarray = new JSONArray();
        JSONObject jsonObjectArray;

        for (int i = 0; i < _PlayerAuction.size(); i++) {
            Auction auction = _PlayerAuction.get(i);
            String itemname = GetItemConfig(auction.getItemStack())[0];
            jsonObjectArray = new JSONObject();
            jsonObjectArray.put("Nick",auction.getPlayerName() );
            jsonObjectArray.put("Item Name", itemname );
            jsonObjectArray.put("Quantity", auction.getItemStack().getAmount());
            jsonarray.add(jsonObjectArray);
        }
        
        Print(jsonarray.toJSONString(),"application/json");
    }
}
