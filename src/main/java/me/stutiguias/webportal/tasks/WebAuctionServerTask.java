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
import me.stutiguias.webportal.init.WebAuction;
import me.stutiguias.webportal.request.*;
import me.stutiguias.webportal.webserver.Response;

/**
 *
 * @author Stutiguias
 */
public class WebAuctionServerTask extends Thread {
    
    private WebAuction plugin;
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
    FillAdminBox _FillAdminBox;
    FillOperations _FillOperations;
    Login _Login;
    Userinfo _UserInfo;

    
    String levelname;
    String PluginDir;

    String ExternalUrl;

    public WebAuctionServerTask(WebAuction plugin, Socket s)
    {
        PluginDir = "plugins/WebPortal/";
        this.plugin = plugin;
        Response = new Response(plugin,s);
        _FillAuction = new FillAuction(plugin, s);
        _FillMyItems = new FillMyItems(plugin, s);
        _FillMyAuctions = new FillMyAuctions(plugin, s);
        _FillAdminBox = new FillAdminBox(plugin, s);
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
                        }else if(!WebAuction.AuthPlayer.containsKey(HostAddress))
                        {
                            if(url.startsWith("/css"))
                            {
                                Response.readFileAsBinary(htmlDir+url,"text/css");
                            }else if(url.startsWith("/image")) {
                                Response.readFileAsBinary(htmlDir+url,"image/jpg");
                            }else if(url.startsWith("/js")) {
                                Response.readFileAsBinary(htmlDir+url,"text/javascript");
                            }else {
                                Response.readFileAsBinary(htmlDir+"/login.html","text/html");
                            }
                        }else if(WebAuction.AuthPlayer.containsKey(HostAddress))
                        {
                            if(url.startsWith("/css"))
                            {
                                if(url.contains("image"))
                                {
                                    Response.readFileAsBinary(htmlDir+url,"image/png");
                                }else{
                                    Response.readFileAsBinary(htmlDir+url,"text/css");
                                }
                            }else if(url.startsWith("/image")) {
                                Response.readFileAsBinary(htmlDir+url,"image/png");
                            }else if(url.startsWith("/js")) {
                                Response.readFileAsBinary(htmlDir+url,"text/javascript");
                            }else if(url.startsWith("/server/username/info"))
                            {
                                _UserInfo.GetInfo();
                            }else if(url.startsWith("/logout")) {
                                WebAuction.AuthPlayer.remove(HostAddress);
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
                            }else if(url.startsWith("/admbox/1")) {
                                _FillAdminBox.ADMBOX1(HostAddress);
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
                WebAuction.log.log(Level.WARNING, plugin.logPrefix + "ERROR in IO ");
                e.printStackTrace();
            }
            catch(Exception e)
            {
                WebAuction.log.log(Level.WARNING, plugin.logPrefix + "ERROR in ServerParser ");
                e.printStackTrace();
            }
        }
        catch(IOException e)
        {
                WebAuction.log.log(Level.WARNING, plugin.logPrefix + "ERROR in IO ");
                e.printStackTrace();
        }finally {
            plugin.connections--;
        }
    }

    public Boolean getLockState(String HostAddress) {
        if(WebAuction.LockTransact.get(WebAuction.AuthPlayer.get(HostAddress).AuctionPlayer.getName()) != null) {
            return WebAuction.LockTransact.get(WebAuction.AuthPlayer.get(HostAddress).AuctionPlayer.getName());
        }else{
            return false;
        }
    }

}
