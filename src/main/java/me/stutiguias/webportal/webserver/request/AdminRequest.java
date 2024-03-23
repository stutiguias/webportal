/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.request;

import com.sun.net.httpserver.HttpExchange;
import me.stutiguias.webportal.commands.CapturingCommandSender;
import me.stutiguias.webportal.commands.CommandUtils;
import me.stutiguias.webportal.model.WebSitePlayer;
import me.stutiguias.webportal.model.Transact;
import me.stutiguias.webportal.model.Shop;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.init.json.JSONArray;
import me.stutiguias.webportal.init.json.JSONObject;
import me.stutiguias.webportal.model.WebSiteMail;
import me.stutiguias.webportal.plugins.Esssentials.CmdEssentials;
import me.stutiguias.webportal.webserver.Html;
import me.stutiguias.webportal.webserver.HttpResponse;
import org.bukkit.Bukkit;

/**
 *
 * @author Daniel
 */
@SuppressWarnings("unchecked")
public class AdminRequest extends HttpResponse {

    WebSitePlayer _authPlayer;
    List<Shop> _playerItems;
    List<WebSiteMail> _playerMail;
    List<Shop> _PlayerAuction;
    
    public AdminRequest(WebPortal plugin, HttpExchange exchange) {
        super(plugin);
        setHttpExchange(exchange);
    }

    public void getMonitor(String SessionId) {
        if(!isAdmin(SessionId)) {
            Print(message.WebNotAdmin,"text/html");
            return;
        }
        Runtime runtime = Runtime.getRuntime();

        long memoryMax = runtime.maxMemory() / 1024 / 1024;
        long memoryTotal = runtime.totalMemory() / 1024 / 1024;
        long memoryFree = runtime.freeMemory() / 1024 / 1024;
        long memoryUsed = memoryTotal - memoryFree;
        double cpuLoad = getProcessCpuLoad();

        JSONArray jsonarray = new JSONArray();
        JSONObject json;
        json = new JSONObject();
        json.put("mem","Memory (MB): " + memoryUsed + " / " + memoryTotal + " (Max: " + memoryMax + ")");
        json.put("cpu","CPU Load (%): " + String.format("%.2f", cpuLoad * 100));
        jsonarray.add(json);
        Print(jsonarray.toJSONString(),"application/json");
    }

    public void CmdEssentials(String cmd,String sessionid,Map param) {
        if(!isAdmin(sessionid)) {
            Print(message.WebNotAdmin,"text/html");
            return;
        }
        CmdEssentials cmdEssentials = new CmdEssentials(plugin);
        cmdEssentials.setHttpExchange(getHttpExchange());
        String webSitePlayerName = WebPortal.AuthPlayers.get(sessionid).WebSitePlayer.getName();

        if(cmd.equals("whois")) cmdEssentials.Whois(webSitePlayerName,param);
        if(cmd.equals("mail")) cmdEssentials.Mail(webSitePlayerName,param);
    }

    public static double getProcessCpuLoad() {
        com.sun.management.OperatingSystemMXBean osBean =
                java.lang.management.ManagementFactory.getPlatformMXBean(
                        com.sun.management.OperatingSystemMXBean.class);
        return osBean.getProcessCpuLoad();
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
            Print(message.WebNotAdmin,"text/html");
        }
    }

    public void WebBan(String SessionId,Map param) {
        if(!isAdmin(SessionId)) {
            Print(message.WebNotAdmin,"text/html");
            return;
        }
        String setplayer = (String)param.get("ID");
        boolean success = plugin.db.WebSiteBan(setplayer,"Y");
        if(success) {
            Print(message.WebPlayerBanned,"text/plain");
        }else{
            Print(message.WebPlayerNotBanned,"text/plain");
        }
    }
        
    public void WebUnBan(String SessionId,Map param) {
        if(!isAdmin(SessionId)) {
            Print(message.WebNotAdmin,"text/html");
            return;
        }
        String setplayer = (String)param.get("ID");
        boolean success = plugin.db.WebSiteBan(setplayer,"N");
        if(success) {
            Print(message.WebPlayerDesBanned,"text/plain");
        }else{
            Print(message.WebPlayerNotDesBanned,"text/plain");
        }
    }
    
    private void playerinfo(String SessionId,String partialName) {
        if(!isAdmin(SessionId)) {
            Print(message.WebNotAdmin,"text/html");
            return;
        }
        List<WebSitePlayer> players = plugin.db.FindAllPlayersWith(partialName);
        if(players == null) {
            Print(message.WebPlayerNotFound,"text/html");
        }else{
            JSONArray jsonarray = new JSONArray();
            JSONObject json;
            for (int i = 0; i < players.size(); i++) {
                json = new JSONObject();
                json.put("Ip",players.get(i).getIp());
                json.put(message.WebName,players.get(i).getName());
                json.put(message.WebCanBuy,players.get(i).getCanBuy());
                json.put(message.WebCanSell,players.get(i).getCanSell());
                json.put(message.WebisAdmin,players.get(i).getIsAdmin());
                json.put(message.WebBanned,players.get(i).getWebban());
                json.put(message.WebWebSiteBan, new Html(plugin).HTMLBan(SessionId,players.get(i).getId()) );
                jsonarray.add(json);
            }
            Print(jsonarray.toJSONString(),"application/json");
        }
    }
    
    private void playertransaction(String name) {
        List<Transact> Transacts = plugin.db.GetTransactOfPlayer(name);
        JSONArray jsonarray = new JSONArray();
        JSONObject jsonObjectArray;
        for (int i = 0; i < Transacts.size(); i++) {

            Transact transact = Transacts.get(i);
   
            jsonObjectArray = new JSONObject();
            jsonObjectArray.put(message.WebBuyer,transact.getBuyer());
            jsonObjectArray.put(message.WebItemName,transact.getItemStack().getName());
            jsonObjectArray.put(message.WebPrice,transact.getPrice());
            jsonObjectArray.put(message.WebQuantity,transact.getQuantity());
            jsonObjectArray.put(message.WebSeller, transact.getSeller());
            jsonarray.add(jsonObjectArray);
            
        }
        Print(jsonarray.toJSONString(),"application/json");
    }
    
    private void playeritems(String name) {
        _playerItems = plugin.db.getAuctionsLimitbyPlayer(name,0,2000,plugin.Myitems);

        JSONArray jsonarray = new JSONArray();
        JSONObject jsonObjectArray;
        for (int i = 0; i < _playerItems.size(); i++) {
            Shop shop = _playerItems.get(i);

            jsonObjectArray = new JSONObject();
            jsonObjectArray.put(message.WebName,shop.getPlayerName());
            jsonObjectArray.put(message.WebItemName,shop.getItemStack().getName());
            jsonObjectArray.put(message.WebQuantity,shop.getItemStack().getAmount());
            jsonarray.add(jsonObjectArray);
        }
        Print(jsonarray.toJSONString(),"application/json");
    }
    
    private void playermails(String name) {
        _playerMail = plugin.db.getMail(name);
        
        JSONArray jsonarray = new JSONArray();
        JSONObject jsonObjectArray;
  
        for (int i = 0; i < _playerMail.size(); i++) {
            WebSiteMail mail = _playerMail.get(i);

            jsonObjectArray = new JSONObject();
            jsonObjectArray.put(message.WebName,mail.getPlayerName());
            jsonObjectArray.put(message.WebItemName,mail.getItemStack().getName() );
            jsonObjectArray.put(message.WebQuantity, mail.getItemStack().getAmount());
            jsonarray.add(jsonObjectArray);
        }

        Print(jsonarray.toJSONString(),"application/json");
    }
        
    private void playerauction(String name) {
        _PlayerAuction = plugin.db.getAuctionsLimitbyPlayer(name,0,2000,plugin.Sell);
        JSONArray jsonarray = new JSONArray();
        JSONObject jsonObjectArray;

        for (int i = 0; i < _PlayerAuction.size(); i++) {
            Shop shop = _PlayerAuction.get(i);

            jsonObjectArray = new JSONObject();
            jsonObjectArray.put(message.WebName,shop.getPlayerName() );
            jsonObjectArray.put(message.WebItemName, shop.getItemStack().getName() );
            jsonObjectArray.put(message.WebQuantity, shop.getItemStack().getAmount());
            jsonarray.add(jsonObjectArray);
        }
        
        Print(jsonarray.toJSONString(),"application/json");
    }
}
