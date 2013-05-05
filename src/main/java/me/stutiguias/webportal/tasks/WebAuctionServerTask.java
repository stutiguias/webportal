/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.tasks;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.request.*;
import me.stutiguias.webportal.webserver.Response;

/**
 *
 * @author Stutiguias
 */
public class WebAuctionServerTask extends Thread {
    
    private WebPortal plugin;
    Socket WebServerSocket;
    String Lang;
    int Port;

    // will be delete soon
    Response Response;
    
    // Response type
    FillAuction _FillAuction;
    FillMyItems _FillMyItems;
    FillMyAuctions _FillMyAuctions;
    FillBox _FillBox;
    FillAdmin _FillAdminBox;
    FillAdminShop _FillAdminShop;
    FillOperations _FillOperations;
    Login _Login;
    Userinfo _UserInfo;

    
    String levelname;
    String PluginDir;

    String ExternalUrl;

    public WebAuctionServerTask(WebPortal plugin, Socket s)
    {
        PluginDir = "plugins/WebPortal/";
        this.plugin = plugin;
        Response = new Response(plugin,s);
        _FillAuction = new FillAuction(plugin, s);
        _FillMyItems = new FillMyItems(plugin, s);
        _FillMyAuctions = new FillMyAuctions(plugin, s);
        _FillAdminBox = new FillAdmin(plugin, s);
        _FillAdminShop = new FillAdminShop(plugin, s);
        _FillBox = new FillBox(plugin, s);
        _FillOperations = new FillOperations(plugin, s);
        _Login = new Login(plugin, s);
        _UserInfo = new Userinfo(plugin, s);
        WebServerSocket = s;
    }

    @Override
    public void run()
    {
        try
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(WebServerSocket.getInputStream()));
            try
            {
                String l, url = "", param = "", htmlDir = "./plugins/WebPortal/html";
                boolean flag = true;
                while((l = in.readLine()) != null && flag)
                {
                    if(l.startsWith("GET"))
                    {
                        flag = false;
                        String g = l.split(" ")[1];
                        Pattern regex = Pattern.compile("([^\\?]*)([^#]*)");
                        Matcher result = regex.matcher(g);
                        if(result.find())
                        {
                            url = result.group(1);
                            param = result.group(2);
                        }
                        if(l.contains("..") || l.contains("./"))
                        {
                            Response.readFileAsBinary(htmlDir+"/login.html","text/html");
                        }

                        String HostAddress = WebServerSocket.getInetAddress().getHostAddress();

                        if(url.startsWith("/web/login"))
                        {
                          _Login.TryToLogin(param);
                        }else if(!WebPortal.AuthPlayers.containsKey(HostAddress))
                        {
                            if(url.startsWith("/css") || url.startsWith("/styles"))
                            {
                                Response.readFileAsBinary(htmlDir+url,"text/css");
                            }else if(url.startsWith("/image")) {
                                Response.readFileAsBinary(htmlDir+url,"image/jpg");
                            }else if(url.startsWith("/js") || url.startsWith("/scripts")) {
                                Response.readFileAsBinary(htmlDir+url,"application/javascript");
                            }else {
                                Response.readFileAsBinary(htmlDir+"/login.html","text/html");
                            }
                        }else if(WebPortal.AuthPlayers.containsKey(HostAddress))
                        {
                            if(url.startsWith("/css") || url.startsWith("/styles"))
                            {
                                if(url.contains("image"))
                                {
                                    Response.readFileAsBinary(htmlDir+url,"image/png");
                                }else{
                                    Response.readFileAsBinary(htmlDir+url,"text/css");
                                }
                            }else if(url.startsWith("/image")) {
                                Response.readFileAsBinary(htmlDir+url,"image/png");
                            }else if(url.startsWith("/js") || url.startsWith("/scripts")) {
                                Response.readFileAsBinary(htmlDir+url,"application/javascript");
                            }else if(url.startsWith("/server/username/info"))
                            {
                                _UserInfo.GetInfo();
                            }else if(url.startsWith("/logout")) {
                                WebPortal.AuthPlayers.remove(HostAddress);
                                Response.readFileAsBinary(htmlDir + "/login.html","text/html");
                            }else if(url.startsWith("/fill/auction")) {
                                _FillAuction.fillAuction(HostAddress,url,param);
                            }else if(url.startsWith("/buy/item")) {
                                _FillOperations.Buy(HostAddress,url,param);
                            }else if(url.startsWith("/fill/myitens")) {
                                _FillMyItems.getMyItems(HostAddress, url, param);
                            }else if(url.startsWith("/web/postauction") && !getLockState(HostAddress)) {
                                _FillOperations.CreateAuction(HostAddress, url, param);
                            }else if(url.startsWith("/web/mail") && !getLockState(HostAddress)) {
                                _FillOperations.Mail(HostAddress, url, param);
                            }else if(url.startsWith("/fill/myauctions")) {
                                _FillMyAuctions.getMyAuctions(HostAddress, url, param);
                            }else if(url.startsWith("/cancel/auction")) {
                                _FillOperations.Cancel(HostAddress, url, param);
                            }else if(url.startsWith("/box/1")) {
                                _FillBox.BOX1(HostAddress);
                            }else if(url.startsWith("/box/2")) {
                                _FillBox.BOX2(HostAddress);
                            }else if(url.startsWith("/admsearch")) {
                                _FillAdminBox.ADM(HostAddress,param);
                            }else if(url.startsWith("/web/delete")){     
                                _FillAdminShop.Delete(HostAddress, url, param);
                            }else if(url.startsWith("/web/shop")){ 
                                _FillAdminShop.AddShop(HostAddress, url, param);
                            }else if(url.startsWith("/web/adminshoplist")){ 
                                _FillAdminShop.list(HostAddress, url, param);
                            }else if(url.equalsIgnoreCase("/")) {
                                Response.readFileAsBinary(htmlDir + "/login.html","text/html");
                            }else{
                                Response.readFileAsBinary(htmlDir + url,"text/html");
                            }
                        }else{
                                Response.readFileAsBinary(htmlDir+"/login.html","text/html");
                        }
                    }
                }
            }catch(IOException e) {
                WebPortal.logger.log(Level.WARNING, "{0} ERROR in IO ", plugin.logPrefix);
                e.printStackTrace();
            }
            catch(Exception e)
            {
                WebPortal.logger.log(Level.WARNING, "{0} ERROR in ServerParser ", plugin.logPrefix);
                e.printStackTrace();
            }
        }
        catch(IOException e)
        {
                WebPortal.logger.log(Level.WARNING, "{0} ERROR in IO ", plugin.logPrefix);
                e.printStackTrace();
        }finally {
            plugin.connections--;
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
