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
    private final WebPortal plugin;
    String htmlDir = "./plugins/WebPortal/html";
    String url;
    Map params;
    
    // Response type
    Request Response;


    public WebPortalHttpHandler(WebPortal plugin)
    {
        this.plugin = plugin;
        Response = new Request(plugin);
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        try {
            Response.SetHttpExchange(t);
            String request = t.getRequestURI().toString();

            params = (Map)t.getAttribute("parameters");
            url = t.getRequestURI().toString();

            if(request.contains("..") || request.contains("./"))
            {
                Response.Response().ReadFile(htmlDir+"/login.html","text/html");
                return;
            }

            SessionId = (String)params.get("sessionid");
          
            if(!WebPortal.AuthPlayers.containsKey(SessionId)) {
                RequestWithoutLogin();
            }else { 
                RequestWithLogin();
            }
        }catch(IOException ex) {
            Response.Response().Print("Cookie Disable, please enable cookie","text/plain");
        }
    }

    public void RequestWithoutLogin() throws IOException {
        if(url.startsWith("/web/login")){
            if(SessionId.length() == 0) {
               Response.Response().Print("Cookie Disable, please enable cookie","text/plain");
               return;
           }
           Response.TryLogin(SessionId,params);
        }else if(url.startsWith("/get/auction")) {
            Response.GetShopWithoutLogin(params);
        }else if(isAllowed()) {
            Response.Response().ReadFile(htmlDir+url,GetMimeType(url));
        }else if(plugin.EnableExternalSource) {
            Response.Response().ReadFile(htmlDir+"/external.html","text/html");
        }else{
            Response.Response().ReadFile(htmlDir+"/login.html","text/html");
        } 
    }

    public void RequestWithLogin() throws IOException {
           if(isAllowed()) {
                Response.Response().ReadFile(htmlDir+url,GetMimeType(url));
            }else if(url.startsWith("/server/username/info")) {
                Response.GetInfo(SessionId);
            }else if(url.startsWith("/logout")) {
                WebPortal.AuthPlayers.remove(SessionId);
                Response.Response().ReadFile(htmlDir + "/login.html","text/html");
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
            }else if(url.startsWith("/buy")) {
                BuyHandler();
            }else if(url.equalsIgnoreCase("/")) {
                Response.Response().ReadFile(htmlDir+"/index.html","text/html");
            }
    }
    
    public void BuyHandler() {
        if(url.startsWith("/buy/additem")) {
            Response.BuyAddItem(SessionId, params);
        }else if(url.startsWith("/buy/remitem")) { 
            Response.BuyCancelItem(params);
        }else if(url.startsWith("/buy/getitem")) {
            Response.BuyGetItems(SessionId, params);
        }
    }
    
    public void AdmHandler() {
        if(url.startsWith("/adm/search")) {
                Response.AdmGetInfo(SessionId,params);
        }else if(url.startsWith("/adm/deleteshop")){     
                Response.AdmDeleteShop(SessionId, url, params);
        }else if(url.startsWith("/adm/addshop")){ 
                Response.AdmAddShop(SessionId, url, params);
        }else if(url.startsWith("/adm/shoplist")){ 
                Response.AdmListShop(SessionId, url, params);
        }else if(url.startsWith("/adm/webban")) {
                Response.AdmWebBan(SessionId, params);
        }else if(url.startsWith("/adm/webunban")) {
                Response.AdmWebUnBan(SessionId, params);
        }
    }
    
    public void MailHandler() {
        if(url.startsWith("/mail/get")) {
                Response.GetMails(SessionId,params);
        }else if(url.startsWith("/mail/send") && !isLocked()) {
                Response.SendMail(SessionId, url, params);
        }
    }
    
    public void BoxHandler() {
        if(url.startsWith("/box/1")) {
                Response.Box1(SessionId);
        }else if(url.startsWith("/box/2")) {
                Response.Box2(SessionId);
        }
    }
    
    public void MyItemsHandler() {
        if(url.startsWith("/myitems/get")) {
                Response.GetMyItems(SessionId);
        }else if(url.startsWith("/myitems/dataTable")) {
                Response.GetMyItems(SessionId, url, params);
        }else if(url.startsWith("/myitems/postauction") && !isLocked()) {
                Response.CreateSell(SessionId, url, params);
        }else if(url.startsWith("/myitems/lore")) {
                Response.ItemLore(SessionId, params);
        }
    }
    
    public void MyAuctionHandler() {
         if(url.startsWith("/myauctions/cancel")) {
                Response.CancelSell(url, params);
        }else if(url.startsWith("/myauctions/get")) {
                Response.GetSell(SessionId, url, params);
        }
    }
    
    public void AuctionHandler() {
        if(url.startsWith("/auction/get")) {
                Response.RequestShopBy(SessionId,url,params);
        }else if(url.startsWith("/auction/shop")) {
                Response.ShopSellBuy(SessionId,params);
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
        if(plugin.EnableExternalSource) return false;
        
        if(url.contains("./") || url.contains("..")) return false;
        
        return  url.startsWith("/css") || 
                url.startsWith("/styles") ||
                url.contains("/image") ||
                url.contains("/favicon.ico") ||
                url.contains("/Images") ||
                url.startsWith("/img") ||
                url.startsWith("/js") ||
                url.startsWith("/scripts") ||
                url.startsWith("/about") ||
                url.startsWith("/myitems.html") ||
                url.startsWith("/login.html") ||
                url.startsWith("/admin.html") ||
                url.startsWith("/sell.html") ||
                url.startsWith("/index.html") ||
                url.startsWith("/about.html") ||
                url.startsWith("/shop.html") ||
                url.startsWith("/mail.html") ||
                url.startsWith("/buy.html") ||
                url.startsWith("/signs.html");
    }

}
