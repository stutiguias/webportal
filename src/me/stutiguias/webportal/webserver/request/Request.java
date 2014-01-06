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
import me.stutiguias.webportal.webserver.request.type.ShopRequest;
import me.stutiguias.webportal.webserver.request.type.BoxRequest;
import me.stutiguias.webportal.webserver.request.type.LoginRequest;
import me.stutiguias.webportal.webserver.request.type.SellRequest;
import me.stutiguias.webportal.webserver.request.type.MyItemsRequest;
import me.stutiguias.webportal.webserver.request.type.UserRequest;
import me.stutiguias.webportal.webserver.HttpResponse;
import me.stutiguias.webportal.webserver.request.type.MailRequest;
import me.stutiguias.webportal.webserver.request.type.BuyRequest;

/**
 *
 * @author Daniel
 */
public class Request {
    
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
    
    public Request(WebPortal plugin) {
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
    
    public void GetShopWithoutLogin(Map param) {
        Shop.GetShopWithoutLogin(param);
    }
    
    public void RequestShopBy(String ip,String url,Map param) {
        Shop.RequestShopBy(ip, url, param);
    }
    
    public void GetMyItems(String HostAddress) {
        MyItems.GetMyItems(HostAddress);
    }
    
    public void GetMyItems(String HostAddress,String url,Map param){
        MyItems.GetMyItems(HostAddress, url, param);
    }
    
    public void CreateSell(String HostAddress,String url,Map param){
        MyItems.CreateSell(HostAddress, url, param);
    }
            
    public void SendMail(String HostAddress,String url,Map param){
        Mail.SendMail(HostAddress,url,param);   
    }
    
    public void GetSell(String HostAddress,String url,Map param){
        Sell.GetSell(HostAddress,url,param);
    }
    
    public void ItemLore(String SessionId,Map param){
        UserInfo.ItemLore(SessionId, param);
    }
    
    public void CancelSell(String url,Map param,String sessionId){
       Sell.Cancel(url, param, sessionId);
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
    
    public void BuyAddItem(String SessionId,Map param) {
        Buy.AddItem(SessionId,param);
    }
    
    public void BuyCancelItem(Map param,String sessionId) {
        Buy.Cancel(param,sessionId);
    }
    
    public void BuyGetItems(String SessionId,Map param) {
        Buy.GetItems(SessionId, param);
    }
    
    public void ShopSellBuy(String SessionId,Map param) {
        Shop.BuySellShop(SessionId, param);
    }
    
    public HttpResponse Response() {
        return Response;
    }
}
