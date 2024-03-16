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
import me.stutiguias.webportal.webserver.request.AdminRequest;
import me.stutiguias.webportal.webserver.request.AdminShopRequest;
import me.stutiguias.webportal.webserver.request.BoxRequest;
import me.stutiguias.webportal.webserver.request.BuyRequest;
import me.stutiguias.webportal.webserver.request.LoginRequest;
import me.stutiguias.webportal.webserver.request.MailRequest;
import me.stutiguias.webportal.webserver.request.MyItemsRequest;
import me.stutiguias.webportal.webserver.request.SellRequest;
import me.stutiguias.webportal.webserver.request.ShopRequest;
import me.stutiguias.webportal.webserver.request.UserRequest;

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
    
    ShopRequest Shop;
    AdminRequest Admin;
    AdminShopRequest AdminShop;
    BoxRequest Box;
    SellRequest Sell;
    MyItemsRequest MyItems;
    LoginRequest Login;
    UserRequest UserInfo;
    HttpResponse Response;
    MailRequest Mail;
    BuyRequest Buy;


    public WebPortalHttpHandler(WebPortal plugin)
    {
        this.plugin = plugin;
        Shop = new ShopRequest(plugin);
        Admin = new AdminRequest(plugin);
        AdminShop = new AdminShopRequest(plugin);
        Box = new BoxRequest(plugin);
        Sell = new SellRequest(plugin);
        MyItems = new MyItemsRequest(plugin);
        Login = new LoginRequest(plugin);
        UserInfo = new UserRequest(plugin);
        Response = new HttpResponse(plugin);
        Mail = new MailRequest(plugin);
        Buy = new BuyRequest(plugin);
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        try {
            SetHttpExchange(t);
            String request = t.getRequestURI().toString();

            params = (Map)t.getAttribute("parameters");
            url = t.getRequestURI().toString();

            if(request.contains("..") || request.contains("./"))
            {
                Response().ReadFile(htmlDir+"/login.html","text/html");
                return;
            }

            SessionId = (String)params.get("sessionid");
          
            if(!WebPortal.AuthPlayers.containsKey(SessionId)) {
                RequestWithoutLogin();
            }else { 
                RequestWithLogin();
            }
        }catch(IOException ex) {
            Response().Print("Cookie Disable, please enable cookie","text/plain");
        }
    }
    
    public void SetHttpExchange(HttpExchange t) {
        Shop.setHttpExchange(t);
        Admin.setHttpExchange(t);
        AdminShop.setHttpExchange(t);
        Box.setHttpExchange(t);
        Sell.setHttpExchange(t);
        MyItems.setHttpExchange(t);
        Login.setHttpExchange(t);
        UserInfo.setHttpExchange(t);
        Response.setHttpExchange(t);
        Mail.setHttpExchange(t);
        Buy.setHttpExchange(t);
    }
    
    public void RequestWithoutLogin() throws IOException {
        if(url.startsWith("/web/login")){
            if(SessionId.length() == 0) {
               Response().Print("Cookie Disable, please enable cookie","text/plain");
               return;
           }
           Login.TryToLogin(SessionId,params);
        }else if(url.startsWith("/get/auction")) {
            Shop.GetShopWithoutLogin(params);
        }else if(isAllowed()) {
            Response().ReadFile(htmlDir+url,GetMimeType(url));
        }else if(plugin.EnableExternalSource) {
            Response().ReadFile(htmlDir+"/external.html","text/html");
        }else{
            Response().ReadFile(htmlDir+"/login.html","text/html");
        } 
    }

    public void RequestWithLogin() throws IOException {
           if(isAllowed()) {
                Response().ReadFile(htmlDir+url,GetMimeType(url));
            }else if(url.startsWith("/server/username/info")) {
                UserInfo.GetInfo(SessionId);
            }else if(url.startsWith("/logout")) {
                WebPortal.AuthPlayers.remove(SessionId);
                Response().ReadFile(htmlDir + "/login.html","text/html");
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
                Response().ReadFile(htmlDir+"/index.html","text/html");
            }
    }
    
    public void BuyHandler() {
        if(url.startsWith("/buy/additem")) {
            Buy.AddItem(SessionId, params);
        }else if(url.startsWith("/buy/remitem")) { 
            Buy.Cancel(params,SessionId);
        }else if(url.startsWith("/buy/getitem")) {
            Buy.GetItems(SessionId, params);
        }
    }
    
    public void AdmHandler() {
        if(url.startsWith("/adm/search")) {
                Admin.AdmGetInfo(SessionId,params);
        }else if(url.startsWith("/adm/deleteshop")){     
                AdminShop.Delete(SessionId, url, params);
        }else if(url.startsWith("/adm/addshop")){ 
                AdminShop.AddShop(SessionId, url, params);
        }else if(url.startsWith("/adm/shoplist")){ 
                AdminShop.List(SessionId, url, params);
        }else if(url.startsWith("/adm/webban")) {
                Admin.WebBan(SessionId, params);
        }else if(url.startsWith("/adm/webunban")) {
                Admin.WebUnBan(SessionId, params);
        }
    }
    
    public void MailHandler() {
        if(url.startsWith("/mail/get")) {
                Mail.GetMails(SessionId,params);
        }else if(url.startsWith("/mail/send") && !isLocked()) {
                Mail.SendMail(SessionId, url, params);
        }
    }
    
    public void BoxHandler() {
        if(url.startsWith("/box/1")) {
                Box.BoxMcMMO(SessionId);
        }else if(url.startsWith("/box/2")) {
                Box.BOX2(SessionId);
        }
    }
    
    public void MyItemsHandler() {
        if(url.startsWith("/myitems/get")) {
                MyItems.GetMyItemsForSelectBox(SessionId);
        }else if(url.startsWith("/myitems/dataTable")) {
                MyItems.GetMyItems(SessionId, url, params);
        }else if(url.startsWith("/myitems/postauction") && !isLocked()) {
                MyItems.CreateSell(SessionId, url, params);
        }else if(url.startsWith("/myitems/lore")) {
                UserInfo.ItemLore(SessionId, params);
        }
    }
    
    public void MyAuctionHandler() {
         if(url.startsWith("/myauctions/cancel")) {
                Sell.Cancel(url, params,SessionId);
        }else if(url.startsWith("/myauctions/get")) {
                Sell.GetSell(SessionId, url, params);
        }
    }
    
    public void AuctionHandler() {
        if(url.startsWith("/auction/get")) {
                Shop.RequestShopBy(SessionId,url,params);
        }else if(url.startsWith("/auction/shop")) {
                Shop.BuySellShop(SessionId,params);
        }
    }
    
    public Boolean isLocked() {
        if(WebPortal.LockTransact.get(WebPortal.AuthPlayers.get(SessionId).WebSitePlayer.getName()) != null) {
            return WebPortal.LockTransact.get(WebPortal.AuthPlayers.get(SessionId).WebSitePlayer.getName());
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
    
    public HttpResponse Response() {
        return Response;
    }
}
