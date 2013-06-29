/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.webserver.request.Request;

/**
 *
 * @author Daniel
 */
public class WebPortalHttpHandler implements HttpHandler {
  
    Socket WebServerSocket;
    String Lang;
    int Port;
    String SessionId;
    
    String htmlDir = "./plugins/WebPortal/html";
    String url;
    Map params;
    
    // Response type
    Request Fill;


    public WebPortalHttpHandler(WebPortal plugin)
    {
        Fill = new Request(plugin);
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        Fill.SetHttpExchange(t);
        String request = t.getRequestURI().toString();
        
        params = (Map)t.getAttribute("parameters");
        url = t.getRequestURI().toString();

        if(request.contains("..") || request.contains("./"))
        {
            Fill.Response().ReadFile(htmlDir+"/login.html","text/html");
            return;
        }

        SessionId = (String)params.get("sessionid");
 
        if(!WebPortal.AuthPlayers.containsKey(SessionId))
        {
            RequestWithoutLogin();
        }else {
            RequestWithLogin();
        }
    }

    public void RequestWithoutLogin() throws IOException {
        if(url.startsWith("/web/login")){
            Fill.TryLogin(SessionId,params);
        }else if(url.startsWith("/get/auction")) {
            Fill.GetAuction(params);
        }else if(isAllowed()) {
            Fill.Response().ReadFile(htmlDir+url,GetMimeType(url));
        }else{
            Fill.Response().ReadFile(htmlDir+"/login.html","text/html");
        } 
    }

    public void RequestWithLogin() throws IOException {
           if(isAllowed()) {
                Fill.Response().ReadFile(htmlDir+url,GetMimeType(url));
            }else if(url.startsWith("/server/username/info")) {
                Fill.GetInfo(SessionId);
            }else if(url.startsWith("/logout")) {
                WebPortal.AuthPlayers.remove(SessionId);
                Fill.Response().ReadFile(htmlDir + "/login.html","text/html");
            }else if(url.startsWith("/myitems")) {
                MyItemsHandler();
            }else if(url.startsWith("/mail")) {
                MailHandler();
            }else if(url.startsWith("/myauctions")) {
                MyAuctionHandler();
            }else if(url.startsWith("/box")) {
                BoxHandler();
            }else if(url.startsWith("/adm")) {
                AdmHandler();
            }else if(url.startsWith("/auction")) {
                AuctionHandler();
            }else if(url.startsWith("/withlist")) {
                WithListHandler();
            }else if(url.equalsIgnoreCase("/")) {
                Fill.Response().ReadFile(htmlDir+"/index.html","text/html");
            }
    }
    
    public void WithListHandler() {
        if(url.startsWith("/withlist/additem")) {
            Fill.WithListAddItem(SessionId, params);
        }else if(url.startsWith("/withlist/getitem")) {
            Fill.WithListGetItems(SessionId, params);
        }
    }
    
    public void AdmHandler() {
        if(url.startsWith("/adm/search")) {
                Fill.AdmGetInfo(SessionId,params);
        }else if(url.startsWith("/adm/deleteshop")){     
                Fill.AdmDeleteShop(SessionId, url, params);
        }else if(url.startsWith("/adm/addshop")){ 
                Fill.AdmAddShop(SessionId, url, params);
        }else if(url.startsWith("/adm/shoplist")){ 
                Fill.AdmListShop(SessionId, url, params);
        }else if(url.startsWith("/adm/getinfo")) {
                Fill.AdmGetServerInfo(SessionId);
        }else if(url.startsWith("/adm/viewplugins")) {
                Fill.AdmViewPlugins(SessionId);
        }else if(url.startsWith("/adm/sendmsg")) {
                Fill.AdmMsg(SessionId, params);
        }else if(url.startsWith("/adm/sendcmd")) {
                Fill.AdmCmdSend(SessionId, params);
        }else if(url.startsWith("/adm/shutdown")) {
                Fill.AdmShutDown(SessionId);
        }else if(url.startsWith("/adm/reload")) {
                Fill.AdmRestart(SessionId);
        }else if(url.startsWith("/adm/seeconsole")) {
                Fill.AdmSeeConsole(SessionId);
        }else if(url.startsWith("/adm/playerlist")) {
                Fill.AdmPlayerList(SessionId);
        }else if(url.startsWith("/adm/banlist")) {
                Fill.AdmBanList(SessionId);
        }else if(url.startsWith("/adm/ban")) {
                Fill.AdmBan(SessionId, params);
        }else if(url.startsWith("/adm/webban")) {
                Fill.AdmWebBan(SessionId, params);
        }else if(url.startsWith("/adm/webunban")) {
                Fill.AdmWebUnBan(SessionId, params);
        }else if(url.startsWith("/adm/unban")) {
                Fill.AdmUnBan(SessionId, params);
        }else if(url.startsWith("/adm/kick")) {
                Fill.AdmKickPlayer(SessionId, params);
        }
    }
    
    public void MailHandler() {
        if(url.startsWith("/mail/get")) {
                Fill.GetMails(SessionId,params);
        }else if(url.startsWith("/mail/send") && !isLocked()) {
                Fill.SendMail(SessionId, url, params);
        }
    }
    
    public void BoxHandler() {
        if(url.startsWith("/box/1")) {
                Fill.Box1(SessionId);
        }else if(url.startsWith("/box/2")) {
                Fill.Box2(SessionId);
        }
    }
    
    public void MyItemsHandler() {
        if(url.startsWith("/myitems/get")) {
                Fill.GetMyItems(SessionId);
        }else if(url.startsWith("/myitems/dataTable")) {
                Fill.GetMyItems(SessionId, url, params);
        }else if(url.startsWith("/myitems/postauction") && !isLocked()) {
                Fill.CreateAuction(SessionId, url, params);
        }else if(url.startsWith("/myitems/lore")) {
                Fill.ItemLore(SessionId, params);
        }
    }
    
    public void MyAuctionHandler() {
         if(url.startsWith("/myauctions/cancel")) {
                Fill.Cancel(url, params);
        }else if(url.startsWith("/myauctions/get")) {
                Fill.GetMyAuctions(SessionId, url, params);
        }
    }
    
    public void AuctionHandler() {
        if(url.startsWith("/auction/get")) {
                Fill.RequestAuctionBy(SessionId,url,params);
        }else if(url.startsWith("/auction/buy")) {
                Fill.Buy(SessionId,params);
        }else if(url.startsWith("/auction/sell")) {
                Fill.AuctionSell(SessionId, params);
        }
    }
    
    public Boolean isLocked() {
        if(WebPortal.LockTransact.get(WebPortal.AuthPlayers.get(SessionId).AuctionPlayer.getName()) != null) {
            return WebPortal.LockTransact.get(WebPortal.AuthPlayers.get(SessionId).AuctionPlayer.getName());
        }else{
            return false;
        }
    }
    
    public String GetMimeType(String url) {
        if(url.contains(".js"))
            return "text/javascript";
        if(url.contains(".png"))
            return "image/jpg";
        if(url.contains(".css"))
            return "text/css";
        if(url.contains(".html"))
            return "text/html";
        return "text/plain";
    }
    
    public Boolean isAllowed() {
        if(url.contains("./") || url.contains("..")) return false;
        
        if(url.startsWith("/css") || 
           url.startsWith("/styles") || 
           url.contains("/image") || 
           url.contains("/Images") || 
           url.startsWith("/img") ||
           url.startsWith("/js") || 
           url.startsWith("/scripts") ||
           url.startsWith("/about") ||
           url.startsWith("/myitems.html") ||
           url.startsWith("/login.html") || 
           url.startsWith("/admin.html") || 
           url.startsWith("/myauctions.html") || 
           url.startsWith("/index.html") || 
           url.startsWith("/about.html") || 
           url.startsWith("/auction.html") ||
           url.startsWith("/mail.html") ||
           url.startsWith("/withlist.html") ||
           url.startsWith("/signs.html")
                )
            return true;
        
        return false;
    }

}
