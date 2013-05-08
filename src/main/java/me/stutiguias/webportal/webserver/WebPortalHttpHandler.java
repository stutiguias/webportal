/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
            }else if(url.startsWith("/fill/auction")) {
                Fill.FillAuction(HostAddress,url,params);
            }else if(url.startsWith("/get/myitems")) {
                Fill.GetMyItems(HostAddress);
            }else if(url.startsWith("/buy/item")) {
                Fill.Buy(HostAddress,url,params);
            }else if(url.startsWith("/fill/myitens")) {
                Fill.GetMyItems(HostAddress, url, params);
            }else if(url.startsWith("/web/postauction") && !isLocked(HostAddress)) {
                Fill.CreateAuction(HostAddress, url, params);
            }else if(url.startsWith("/web/mail") && !isLocked(HostAddress)) {
                Fill.Mail(HostAddress, url, params);
            }else if(url.startsWith("/fill/myauctions")) {
                Fill.GetMyAuctions(HostAddress, url, params);
            }else if(url.startsWith("/cancel/auction")) {
                Fill.Cancel(HostAddress, url, params);
            }else if(url.startsWith("/box/1")) {
                Fill.Box1(HostAddress);
            }else if(url.startsWith("/box/2")) {
                Fill.Box2(HostAddress);
            }else if(url.startsWith("/admsearch")) {
                Fill.ADM(HostAddress,params);
            }else if(url.startsWith("/web/delete")){     
                Fill.Delete(HostAddress, url, params);
            }else if(url.startsWith("/web/shop")){ 
                Fill.AddShop(HostAddress, url, params);
            }else if(url.startsWith("/web/adminshoplist")){ 
                Fill.List(HostAddress, url, params);
            }else if(url.equalsIgnoreCase("/")) {
                Fill.Response().ReadFile(htmlDir + "/index.html","text/html");
            }else{
                Fill.Response().ReadFile(htmlDir + url,"text/html");
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
           url.startsWith("/image") || 
           url.startsWith("/img") ||
           url.startsWith("/js") || 
           url.startsWith("/scripts") ||
           url.startsWith("/about"))
            return true;
        
        return false;
    }

}
