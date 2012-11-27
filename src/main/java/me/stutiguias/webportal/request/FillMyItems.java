/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.request;

import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.settings.Auction;
import me.stutiguias.webportal.webserver.Html;
import me.stutiguias.webportal.webserver.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Daniel
 */
public class FillMyItems extends Response {
    
    private WebPortal plugin;
    private Html html;
    
    public FillMyItems(WebPortal plugin,Socket s) {
        super(plugin, s);
        this.plugin = plugin;
        html = new Html(plugin);
    }
    
    public void getMyItems(String ip,String url,String param) {

        int iDisplayStart = Integer.parseInt(getParam("iDisplayStart", param));
        int iDisplayLength = Integer.parseInt(getParam("iDisplayLength", param));
        List<Auction> auctions = plugin.dataQueries.getAuctionsLimitbyPlayer(WebPortal.AuthPlayers.get(ip).AuctionPlayer.getName(),iDisplayStart,iDisplayLength,plugin.Myitems);
        if(WebPortal.AuthPlayers.get(ip).AuctionPlayer.getName() == null) {
            WebPortal.log.log(Level.WARNING,"Cant determine player name");
            return;
        }
        if(auctions == null) {
            WebPortal.log.log(Level.WARNING,"Cant get auctions");
            return;
        }
        int sEcho = Integer.parseInt(getParam("sEcho", param));
        int iTotalRecords = plugin.dataQueries.getFound();
        int iTotalDisplayRecords = iTotalRecords;
        JSONObject json = new JSONObject();
        JSONArray jsonData = new JSONArray();
        JSONObject jsonTwo;
        
        json.put("sEcho", sEcho);
        json.put("iTotalRecords", iTotalRecords);
        json.put("iTotalDisplayRecords", iTotalDisplayRecords);
        
        if(iTotalRecords > 0) {
            for(Auction item:auctions){
                jsonTwo = new JSONObject();
                jsonTwo.put("DT_RowId","row_" + item.getId() );
                jsonTwo.put("DT_RowClass", "gradeA");
                jsonTwo.put("0", ConvertItemToResult(item,item.getType()));
                jsonTwo.put("1", item.getItemStack().getAmount());
                jsonTwo.put("2", "$ " + item.getPrice());
                jsonTwo.put("3", "$ " + item.getPrice() * item.getItemStack().getAmount());
                jsonTwo.put("4", html.HTMLAuctionCreate(ip,item.getId()));
                jsonTwo.put("5", html.HTMLAuctionMail(ip,item.getId()));

                jsonData.add(jsonTwo);
            }
        }else{
                jsonTwo = new JSONObject();
                jsonTwo.put("DT_RowId","row_0" );
                jsonTwo.put("DT_RowClass", "gradeU");
                jsonTwo.put("0", "");
                jsonTwo.put("1", "");
                jsonTwo.put("2", "");
                jsonTwo.put("3", "No Items");
                jsonTwo.put("4", "");
                jsonTwo.put("5", "");
                jsonData.add(jsonTwo);
        }
        json.put("aaData",jsonData);
        
        print(json.toJSONString(),"text/plain");
    }
}
