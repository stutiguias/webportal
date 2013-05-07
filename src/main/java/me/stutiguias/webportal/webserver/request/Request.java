/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.request;

import com.sun.net.httpserver.HttpExchange;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.webserver.request.type.AdminRequest;
import me.stutiguias.webportal.webserver.request.type.AdminShopRequest;
import me.stutiguias.webportal.webserver.request.type.AuctionRequest;
import me.stutiguias.webportal.webserver.request.type.BoxRequest;
import me.stutiguias.webportal.webserver.request.type.LoginRequest;
import me.stutiguias.webportal.webserver.request.type.MyAuctionsRequest;
import me.stutiguias.webportal.webserver.request.type.MyItemsRequest;
import me.stutiguias.webportal.webserver.request.type.OperationsRequest;
import me.stutiguias.webportal.webserver.request.type.UserRequest;
import me.stutiguias.webportal.webserver.HttpResponse;

/**
 *
 * @author Daniel
 */
public class Request {
    
    AuctionRequest Auction;
    AdminRequest Admin;
    AdminShopRequest AdminShop;
    BoxRequest Box;
    MyAuctionsRequest MyAuctions;
    MyItemsRequest MyItems;
    OperationsRequest Operations;
    LoginRequest Login;
    UserRequest UserInfo;
    HttpResponse Response;
    
    public Request(WebPortal plugin) {
        Auction = new AuctionRequest(plugin);
        Admin = new AdminRequest(plugin);
        AdminShop = new AdminShopRequest(plugin);
        Box = new BoxRequest(plugin);
        MyAuctions = new MyAuctionsRequest(plugin);
        MyItems = new MyItemsRequest(plugin);
        Operations = new OperationsRequest(plugin);
        Login = new LoginRequest(plugin);
        UserInfo = new UserRequest(plugin);
        Response = new HttpResponse(plugin);
    }
    
    public void SetHttpExchange(HttpExchange t) {
        Auction.setHttpExchange(t);
        Admin.setHttpExchange(t);
        AdminShop.setHttpExchange(t);
        Box.setHttpExchange(t);
        MyAuctions.setHttpExchange(t);
        MyItems.setHttpExchange(t);
        Operations.setHttpExchange(t);
        Login.setHttpExchange(t);
        UserInfo.setHttpExchange(t);
        Response.setHttpExchange(t);
    }
    
    public void GetAuction(String param) {
        Auction.getAuction(param);
    }
    
    public void FillAuction(String ip,String url,String param) {
        Auction.fillAuction(ip, url, param);
    }
    
    public void GetMyItems(String HostAddress) {
        MyItems.getMyItems(HostAddress);
    }
    
    public void GetMyItems(String HostAddress,String url,String param){
        MyItems.getMyItems(HostAddress, url, param);
    }
    
    public void Buy(String HostAddress,String url,String param){
        Operations.Buy(HostAddress,url,param);
    }
    
    public void CreateAuction(String HostAddress,String url,String param){
        Operations.CreateAuction(HostAddress, url, param);
    }
            
    public void Mail(String HostAddress,String url,String param){
        Operations.Mail(HostAddress,url,param);   
    }
    
    public void GetMyAuctions(String HostAddress,String url,String param){
        MyAuctions.getMyAuctions(HostAddress,url,param);
    }
    
    public void Cancel(String HostAddress,String url,String param){
       Operations.Cancel(HostAddress, url, param);
    }
    
    public void Box1(String HostAddress) {
        Box.BOX1(HostAddress);
    }
    
    public void Box2(String HostAddress) {
        Box.BOX2(HostAddress);
    }
      
    public void ADM(String HostAddress,String param){
        Admin.ADM(HostAddress, param);
    }
    
    public void Delete(String HostAddress,String url,String param){
        AdminShop.Delete(HostAddress, url, param);
    }
     
    public void AddShop(String HostAddress,String url,String param){
        AdminShop.AddShop(HostAddress, url, param);
    }
    
    public void List(String HostAddress,String url,String param){
        AdminShop.list(HostAddress, url, param);
    }
    
    public void TryLogin(String HostAddress,String param) {
        Login.TryToLogin(HostAddress, param);
    }
    
    public void GetInfo(String HostAddress) {
        UserInfo.GetInfo(HostAddress);
    }
    
    public HttpResponse Response() {
        return Response;
    }
}
