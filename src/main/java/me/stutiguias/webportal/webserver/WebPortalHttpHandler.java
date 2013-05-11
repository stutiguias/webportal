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

        String HostAddress = t.getLocalAddress().getHostName();

        if(!WebPortal.AuthPlayers.containsKey(HostAddress))
        {
            RequestWithoutLogin(HostAddress);
        }else {
            RequestWithLogin(HostAddress);
        }
    }

    public void RequestWithoutLogin(String HostAddress) throws IOException {
        if(url.startsWith("/web/login")){
            Fill.TryLogin(HostAddress,params);
        }else if(url.startsWith("/get/auction")) {
            Fill.GetAuction(params);
        }else if(isAllowed(url)) {
            Fill.Response().ReadFile(htmlDir+url,GetMimeType(url));
        }else{
            Fill.Response().ReadFile(htmlDir+"/login.html","text/html");
        } 
    }

    public void RequestWithLogin(String HostAddress) throws IOException {
           if(isAllowed(url)) {
                Fill.Response().ReadFile(htmlDir+url,GetMimeType(url));
            }else if(url.startsWith("/server/username/info")) {
                Fill.GetInfo(HostAddress);
            }else if(url.startsWith("/logout")) {
                WebPortal.AuthPlayers.remove(HostAddress);
                Fill.Response().ReadFile(htmlDir + "/login.html","text/html");
            }else if(url.startsWith("/myitems")) {
                MyItemsHandler(HostAddress);
            }else if(url.startsWith("/mail")) {
                MailHandler(HostAddress);
            }else if(url.startsWith("/myauctions")) {
                MyAuctionHandler(HostAddress);
            }else if(url.startsWith("/box")) {
                BoxHandler(HostAddress);
            }else if(url.startsWith("/auction")) {
                AuctionHandler(HostAddress);
            }else if(url.startsWith("/admsearch")) {
                Fill.ADM(HostAddress,params);
            }else if(url.startsWith("/web/delete")){     
                Fill.Delete(HostAddress, url, params);
            }else if(url.startsWith("/web/shop")){ 
                Fill.AddShop(HostAddress, url, params);
            }else if(url.startsWith("/web/adminshoplist")){ 
                Fill.List(HostAddress, url, params);
            }else if(url.equalsIgnoreCase("/")) {
                Fill.Response().ReadFile("Please Log","text/html");
            }
    }
    
    public void MailHandler(String HostAddress) {
        if(url.startsWith("/mail/get")) {
                Fill.GetMails(HostAddress,params);
        }else if(url.startsWith("/mail/send") && !isLocked(HostAddress)) {
                Fill.SendMail(HostAddress, url, params);
        }
    }
    
    public void BoxHandler(String HostAddress) {
        if(url.startsWith("/box/1")) {
                Fill.Box1(HostAddress);
        }else if(url.startsWith("/box/2")) {
                Fill.Box2(HostAddress);
        }
    }
    
    public void MyItemsHandler(String HostAddress) {
        if(url.startsWith("/myitems/get")) {
                Fill.GetMyItems(HostAddress);
        }else if(url.startsWith("/myitems/dataTable")) {
                Fill.GetMyItems(HostAddress, url, params);
        }else if(url.startsWith("/myitems/postauction") && !isLocked(HostAddress)) {
                Fill.CreateAuction(HostAddress, url, params);
        }
    }
    
    public void MyAuctionHandler(String HostAddress) {
         if(url.startsWith("/myauctions/cancel")) {
                Fill.Cancel(HostAddress, url, params);
        }else if(url.startsWith("/myauctions/get")) {
                Fill.GetMyAuctions(HostAddress, url, params);
        }
    }
    
    public void AuctionHandler(String HostAddress) {
        if(url.startsWith("/auction/get")) {
                Fill.RequestAuctionBy(HostAddress,url,params);
        }else if(url.startsWith("/auction/buy")) {
                Fill.Buy(HostAddress,url,params);
        }
    }
    
    public Boolean isLocked(String HostAddress) {
        if(WebPortal.LockTransact.get(WebPortal.AuthPlayers.get(HostAddress).AuctionPlayer.getName()) != null) {
            return WebPortal.LockTransact.get(WebPortal.AuthPlayers.get(HostAddress).AuctionPlayer.getName());
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
    
    public Boolean isAllowed(String url) {
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
           url.startsWith("/sign.html")
                )
            return true;
        
        return false;
    }

}
