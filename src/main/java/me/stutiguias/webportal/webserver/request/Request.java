/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.request;

import com.sun.net.httpserver.HttpExchange;
import java.util.Map;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.webserver.request.type.AdminRequest;
import me.stutiguias.webportal.webserver.request.type.AdminShopRequest;
import me.stutiguias.webportal.webserver.request.type.AuctionRequest;
import me.stutiguias.webportal.webserver.request.type.BoxRequest;
import me.stutiguias.webportal.webserver.request.type.LoginRequest;
import me.stutiguias.webportal.webserver.request.type.MyAuctionsRequest;
import me.stutiguias.webportal.webserver.request.type.MyItemsRequest;
import me.stutiguias.webportal.webserver.request.type.UserRequest;
import me.stutiguias.webportal.webserver.HttpResponse;
import me.stutiguias.webportal.webserver.request.type.MailRequest;
import me.stutiguias.webportal.webserver.request.type.WithListRequest;

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
    LoginRequest Login;
    UserRequest UserInfo;
    HttpResponse Response;
    MailRequest Mail;
    WithListRequest Withlist;
    
    public Request(WebPortal plugin) {
        Auction = new AuctionRequest(plugin);
        Admin = new AdminRequest(plugin);
        AdminShop = new AdminShopRequest(plugin);
        Box = new BoxRequest(plugin);
        MyAuctions = new MyAuctionsRequest(plugin);
        MyItems = new MyItemsRequest(plugin);
        Login = new LoginRequest(plugin);
        UserInfo = new UserRequest(plugin);
        Response = new HttpResponse(plugin);
        Mail = new MailRequest(plugin);
        Withlist = new WithListRequest(plugin);
    }
    
    public void SetHttpExchange(HttpExchange t) {
        Auction.setHttpExchange(t);
        Admin.setHttpExchange(t);
        AdminShop.setHttpExchange(t);
        Box.setHttpExchange(t);
        MyAuctions.setHttpExchange(t);
        MyItems.setHttpExchange(t);
        Login.setHttpExchange(t);
        UserInfo.setHttpExchange(t);
        Response.setHttpExchange(t);
        Mail.setHttpExchange(t);
        Withlist.setHttpExchange(t);
    }
    
    public void GetAuction(Map param) {
        Auction.GetAuction(param);
    }
    
    public void RequestAuctionBy(String ip,String url,Map param) {
        Auction.RequestAuctionBy(ip, url, param);
    }
    
    public void GetMyItems(String HostAddress) {
        MyItems.GetMyItems(HostAddress);
    }
    
    public void GetMyItems(String HostAddress,String url,Map param){
        MyItems.GetMyItems(HostAddress, url, param);
    }
    
    public void Buy(String HostAddress,Map param){
        Auction.Buy(HostAddress,param);
    }
    
    public void CreateAuction(String HostAddress,String url,Map param){
        MyItems.CreateAuction(HostAddress, url, param);
    }
            
    public void SendMail(String HostAddress,String url,Map param){
        Mail.SendMail(HostAddress,url,param);   
    }
    
    public void GetMyAuctions(String HostAddress,String url,Map param){
        MyAuctions.GetMyAuctions(HostAddress,url,param);
    }
    
    public void ItemLore(String SessionId,Map param){
        UserInfo.ItemLore(SessionId, param);
    }
    
    public void Cancel(String url,Map param){
       MyAuctions.Cancel(url, param);
    }
    
    public void Box1(String HostAddress) {
        Box.BOX1(HostAddress);
    }
    
    public void Box2(String HostAddress) {
        Box.BOX2(HostAddress);
    }
      
    public void AdmGetInfo(String HostAddress,Map param){
        Admin.AdmGetInfo(HostAddress, param);
    }
    
    public void AdmDeleteShop(String HostAddress,String url,Map param){
        AdminShop.Delete(HostAddress, url, param);
    }
     
    public void AdmAddShop(String HostAddress,String url,Map param){
        AdminShop.AddShop(HostAddress, url, param);
    }
    
    public void AdmListShop(String HostAddress,String url,Map param){
        AdminShop.List(HostAddress, url, param);
    }
    
    public void AdmWebBan(String SessionId,Map param) {
        Admin.WebBan(SessionId, param);
    }
    
    public void AdmWebUnBan(String SessionId,Map param) {
        Admin.WebUnBan(SessionId, param);
    }

    public void TryLogin(String HostAddress,Map param) {
        Login.TryToLogin(HostAddress, param);
    }
    
    public void GetInfo(String HostAddress) {
        UserInfo.GetInfo(HostAddress);
    }
    
    public void GetMails(String HostAddress,Map param) {
        Mail.GetMails(HostAddress,param);
    }
    
    public void WithListAddItem(String SessionId,Map param) {
        Withlist.AddItem(SessionId,param);
    }
    
    public void WithListGetItems(String SessionId,Map param) {
        Withlist.GetItems(SessionId, param);
    }
    
    public void AuctionSell(String SessionId,Map param) {
        Auction.Sell(SessionId, param);
    }
    
    public HttpResponse Response() {
        return Response;
    }
}
