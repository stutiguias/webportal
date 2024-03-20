/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.request;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import com.sun.net.httpserver.HttpExchange;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.init.json.JSONArray;
import me.stutiguias.webportal.init.json.JSONObject;
import me.stutiguias.webportal.model.WebSitePlayer;
import me.stutiguias.webportal.webserver.authentication.LoggedPlayer;
import me.stutiguias.webportal.webserver.HttpResponse;

/**
 *
 * @author Daniel
 */
@SuppressWarnings("unchecked")
public class UserRequest extends HttpResponse {

    public UserRequest(WebPortal plugin, HttpExchange exchange) {
        super(plugin);
        setHttpExchange(exchange);
    }

    public void GetInfo(String sessionId)  {

        try{
            LoggedPlayer authPlayer = WebPortal.AuthPlayers.get(sessionId);

            WebSitePlayer webSitePlayer = plugin.db.getPlayer(authPlayer.WebSitePlayer.getName());
            UUID uuid = UUID.fromString(webSitePlayer.getUUID());

            JSONObject json = new JSONObject();
            json.put("Name", authPlayer.WebSitePlayer.getName() );
            json.put("Admin", authPlayer.WebSitePlayer.getIsAdmin() );
            json.put("Money", plugin.economy.getBalance( plugin.getServer().getOfflinePlayer(uuid) ) );
            json.put("Mail", plugin.db.getMail(authPlayer.WebSitePlayer.getName() ).size() );
            json.put("Avatarurl", plugin.Avatarurl + authPlayer.WebSitePlayer.getName() );
            Print(json.toJSONString(),"application/json");
        }catch (Exception ex){
            plugin.getLogger().log(Level.INFO, ex.getMessage());
        }

    }

    public void ItemLore(String SessionId,Map param) {
        int id = Integer.parseInt((String)param.get("id"));
        String metaCSV = plugin.db.GetItemInfo(id,"meta");
        String[] metas = metaCSV.split(",");
        JSONObject json = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (String meta : metas) {
            if (meta.startsWith("N[#$]")) {
                String metad = meta.replace("N[#$]", "").replaceAll("§\\w", "");
                json.put("Display Name",metad);
            } else {
                String metal = meta.replace("L[#$]", "").replaceAll("§\\w", "");
                jsonArray.add(metal);
            }
        }
        json.put("Lore",jsonArray.toJSONString());
        Print(json.toJSONString(),"application/json");
    }

}
