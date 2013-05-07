/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.net.Socket;
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
    String param;
    
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
        
        Pattern regex = Pattern.compile("([^\\?]*)([^#]*)");
        Matcher result = regex.matcher(request);

        if(result.find())
        {
            url = result.group(1);
            param = result.group(2);
        }

        if(request.contains("..") || request.contains("./"))
        {
            Fill.Response().ReadFile(htmlDir+"/login.html","text/html");
            return;
        }

        String HostAddress = t.getLocalAddress().getHostName();

        if(!WebPortal.AuthPlayers.containsKey(HostAddress))
        {
            RequestWithoutLogin(HostAddress);
        }else if(WebPortal.AuthPlayers.containsKey(HostAddress)) {
            RequestWithLogin(HostAddress);
        }else{
            Fill.Response().ReadFile(htmlDir+"/login.html","text/html");
        }
        
    }

    public void RequestWithoutLogin(String HostAddress) throws IOException {
        if(url.startsWith("/web/login"))
        {
            Fill.TryLogin(HostAddress,param);
        }else if(url.startsWith("/css") || url.startsWith("/styles")) {
            Fill.Response().ReadFile(htmlDir+url,"text/css");
        }else if(url.startsWith("/image") || url.startsWith("/img")) {
            Fill.Response().ReadFile(htmlDir+url,"image/jpg");
        }else if(url.startsWith("/js") || url.startsWith("/scripts")) {
            Fill.Response().ReadFile(htmlDir+url,"application/javascript");
        }else if(url.startsWith("/get/auction")) {
            Fill.GetAuction(param);
        }else {
            Fill.Response().ReadFile(htmlDir+"/login.html","text/html");
        } 
    }

    public void RequestWithLogin(String HostAddress) throws IOException {
           if(url.startsWith("/css") || url.startsWith("/styles"))
            {
                if(url.contains("image"))
                {
                    Fill.Response().ReadFile(htmlDir+url,"image/png");
                }else{
                    Fill.Response().ReadFile(htmlDir+url,"text/css");
                }
            }else if(url.startsWith("/image") || url.startsWith("/img")) {
                Fill.Response().ReadFile(htmlDir+url,"image/png");
            }else if(url.startsWith("/js") || url.startsWith("/scripts")) {
                Fill.Response().ReadFile(htmlDir+url,"application/javascript");
            }else if(url.startsWith("/server/username/info"))
            {
                Fill.GetInfo(HostAddress);
            }else if(url.startsWith("/logout")) {
                WebPortal.AuthPlayers.remove(HostAddress);
                Fill.Response().ReadFile(htmlDir + "/login.html","text/html");
            }else if(url.startsWith("/fill/auction")) {
                Fill.FillAuction(HostAddress,url,param);
            }else if(url.startsWith("/get/myitems")) {
                Fill.GetMyItems(HostAddress);
            }else if(url.startsWith("/buy/item")) {
                Fill.Buy(HostAddress,url,param);
            }else if(url.startsWith("/fill/myitens")) {
                Fill.GetMyItems(HostAddress, url, param);
            }else if(url.startsWith("/web/postauction") && !getLockState(HostAddress)) {
                Fill.CreateAuction(HostAddress, url, param);
            }else if(url.startsWith("/web/mail") && !getLockState(HostAddress)) {
                Fill.Mail(HostAddress, url, param);
            }else if(url.startsWith("/fill/myauctions")) {
                Fill.GetMyAuctions(HostAddress, url, param);
            }else if(url.startsWith("/cancel/auction")) {
                Fill.Cancel(HostAddress, url, param);
            }else if(url.startsWith("/box/1")) {
                Fill.Box1(HostAddress);
            }else if(url.startsWith("/box/2")) {
                Fill.Box2(HostAddress);
            }else if(url.startsWith("/admsearch")) {
                Fill.ADM(HostAddress,param);
            }else if(url.startsWith("/web/delete")){     
                Fill.Delete(HostAddress, url, param);
            }else if(url.startsWith("/web/shop")){ 
                Fill.AddShop(HostAddress, url, param);
            }else if(url.startsWith("/web/adminshoplist")){ 
                Fill.List(HostAddress, url, param);
            }else if(url.equalsIgnoreCase("/")) {
                Fill.Response().ReadFile(htmlDir + "/login.html","text/html");
            }else{
                Fill.Response().ReadFile(htmlDir + url,"text/html");
            }
    }
    
    public Boolean getLockState(String HostAddress) {
        if(WebPortal.LockTransact.get(WebPortal.AuthPlayers.get(HostAddress).AuctionPlayer.getName()) != null) {
            return WebPortal.LockTransact.get(WebPortal.AuthPlayers.get(HostAddress).AuctionPlayer.getName());
        }else{
            return false;
        }
    }

}
