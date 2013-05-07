/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.request.type;

import java.util.List;
import java.util.Map;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.settings.Auction;
import me.stutiguias.webportal.webserver.Html;
import me.stutiguias.webportal.webserver.HttpResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Daniel
 */
public class MyAuctionsRequest extends HttpResponse {
    
    private WebPortal plugin;
    private Html html;
    
    public MyAuctionsRequest(WebPortal plugin) {
        super(plugin);
        this.plugin = plugin;
        html = new Html(plugin);
    }
    
    public void GetMyAuctions(String ip,String url,Map param) {
        
        int iDisplayStart = Integer.parseInt((String)param.get("iDisplayStart"));
        int iDisplayLength = Integer.parseInt((String)param.get("iDisplayLength"));
        String search = (String)param.get("sSearch");
        int sEcho =  Integer.parseInt((String)param.get("sEcho"));
        
        List<Auction> la = plugin.dataQueries.getAuctionsLimitbyPlayer(WebPortal.AuthPlayers.get(ip).AuctionPlayer.getName(),iDisplayStart,iDisplayLength,plugin.Auction);

        int iTotalRecords = plugin.dataQueries.getFound();
        int iTotalDisplayRecords = iTotalRecords;
        JSONObject json = new JSONObject();
        JSONArray jsonData = new JSONArray();
        JSONObject jsonTwo;
        
        json.put("sEcho", sEcho);
        json.put("iTotalRecords", iTotalRecords);
        json.put("iTotalDisplayRecords", iTotalDisplayRecords);
        
        if(iTotalRecords > 0) {
            for(Auction item:la){
                jsonTwo = new JSONObject();
                jsonTwo.put("DT_RowId","row_" + item.getId() );
                jsonTwo.put("DT_RowClass", "gradeA");
                jsonTwo.put("0", ConvertItemToResult(item,item.getType()));
                jsonTwo.put("1", item.getId());
                jsonTwo.put("2", item.getItemStack().getAmount());
                jsonTwo.put("3", "$ " + item.getPrice());
                jsonTwo.put("4", "$ " + item.getPrice() * item.getItemStack().getAmount());
                jsonTwo.put("5", Format(MarketPrice(item, item.getPrice())) + "%" );
                jsonTwo.put("6", GetEnchant(item));
                jsonTwo.put("7", GetDurability(item));
                jsonTwo.put("8", html.HTMLCancel(ip,item.getId()));

                jsonData.add(jsonTwo);
            }
        }else{
                jsonTwo = new JSONObject();
                jsonTwo.put("DT_RowId","row_0" );
                jsonTwo.put("DT_RowClass", "gradeU");
                jsonTwo.put("0", "");
                jsonTwo.put("1", "");
                jsonTwo.put("2", "");
                jsonTwo.put("3", "No Auction");
                jsonTwo.put("4", "");
                jsonTwo.put("5", "");
                jsonTwo.put("6", "");
                jsonTwo.put("7", "");
                jsonTwo.put("8", "");
                jsonData.add(jsonTwo);
        }
        json.put("aaData",jsonData);
        
        Print(json.toJSONString(),"text/plain");
    }
}
