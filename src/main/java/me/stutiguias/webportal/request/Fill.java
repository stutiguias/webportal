/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.request;

import com.sun.net.httpserver.HttpExchange;
import me.stutiguias.webportal.init.WebPortal;

/**
 *
 * @author Daniel
 */
public class Fill {
    
    FillAuction FillAuction;
    FillAdmin FillAdmin;
    FillAdminShop FillAdminShop;
    FillBox FillBox;
    FillMyAuctions FillMyAuctions;
    FillMyItems FillMyItems;
    FillOperations FillOperations;
    Login Login;
    Userinfo UserInfo;
    
    public Fill(WebPortal plugin) {
        FillAuction = new FillAuction(plugin);
        FillAdmin = new FillAdmin(plugin);
        FillAdminShop = new FillAdminShop(plugin);
        FillBox = new FillBox(plugin);
        FillMyAuctions = new FillMyAuctions(plugin);
        FillMyItems = new FillMyItems(plugin);
        FillOperations = new FillOperations(plugin);
        Login = new Login(plugin);
        UserInfo = new Userinfo(plugin);
    }
    
    public void SetHttpExchange(HttpExchange t) {
        FillAuction.httpExchange = t;
        FillAdmin.httpExchange = t;
        FillAdminShop.httpExchange = t;
        FillBox.httpExchange = t;
        FillMyAuctions.httpExchange = t;
        FillMyItems.httpExchange = t;
        FillOperations.httpExchange = t;
        Login.httpExchange = t;
        UserInfo.httpExchange = t;
    }
    
    public void GetAuction(String param) {
        FillAuction.getAuction(param);
    }
    
    public void FillAuction(String ip,String url,String param) {
        FillAuction.fillAuction(ip, url, param);
    }
    
    public void GetMyItems(String HostAddress) {
        FillMyItems.getMyItems(HostAddress);
    }
    
    public void GetMyItems(String HostAddress,String url,String param){
        FillMyItems.getMyItems(HostAddress, url, param);
    }
    
    public void Buy(String HostAddress,String url,String param){
        FillOperations.Buy(HostAddress,url,param);
    }
    
    public void CreateAuction(String HostAddress,String url,String param){
        FillOperations.CreateAuction(HostAddress, url, param);
    }
            
    public void Mail(String HostAddress,String url,String param){
        FillOperations.Mail(HostAddress,url,param);   
    }
    
    public void GetMyAuctions(String HostAddress,String url,String param){
        FillMyAuctions.getMyAuctions(HostAddress,url,param);
    }
    
    public void Cancel(String HostAddress,String url,String param){
       FillOperations.Cancel(HostAddress, url, param);
    }
    
    public void Box1(String HostAddress) {
        FillBox.BOX1(HostAddress);
    }
    
    public void Box2(String HostAddress) {
        FillBox.BOX2(HostAddress);
    }
      
    public void ADM(String HostAddress,String param){
        FillAdmin.ADM(HostAddress, param);
    }
    
    public void Delete(String HostAddress,String url,String param){
        FillAdminShop.Delete(HostAddress, url, param);
    }
     
    public void AddShop(String HostAddress,String url,String param){
        FillAdminShop.AddShop(HostAddress, url, param);
    }
    
    public void List(String HostAddress,String url,String param){
        FillAdminShop.list(HostAddress, url, param);
    }
    
    public void TryLogin(String HostAddress,String param) {
        Login.TryToLogin(HostAddress, param);
    }
    
    public void GetInfo(String HostAddress) {
        UserInfo.GetInfo(HostAddress);
    }
}
