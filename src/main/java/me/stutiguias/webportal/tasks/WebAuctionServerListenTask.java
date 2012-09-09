/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.tasks;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import me.stutiguias.webportal.init.WebAuction;

/**
 *
 * @author Daniel
 */
public class WebAuctionServerListenTask extends Thread {
   
    private WebAuction plugin;
    int Port;
    WebAuctionServerTask WebAuctionServerTask;
    public int NUM_CONN_MAX;
    public ServerSocket server;
    
    public WebAuctionServerListenTask(WebAuction plugin,int NUM_CONN_MAX)
    {
        this.NUM_CONN_MAX = NUM_CONN_MAX;
        Port = plugin.port;
        this.plugin = plugin;
    }
    
    @Override
    public void run()
    {
        try {
            server = new ServerSocket(Port);
            Socket client;
            try
            {
                WebAuction.log.info(plugin.logPrefix + "WebServer listening on port "+Port);
                while(!server.isClosed()){
                        if(plugin.connections < NUM_CONN_MAX) {
                            client = server.accept();
                            WebAuctionServerTask = new WebAuctionServerTask(plugin, client);
                            WebAuctionServerTask.start();
                            plugin.connections++;
                        }else{
                            WebAuction.log.log(Level.WARNING, plugin.logPrefix + " The max number of Simultaneous as Reach");
                        }
                }
            }catch(Exception e){ 
                WebAuction.log.info((new StringBuilder()).append("ERROR : ").append(e.getMessage()).toString());
            }
        }catch(Exception ex) {
            WebAuction.log.info((new StringBuilder()).append("ERROR : ").append(ex.getMessage()).toString());
        }
    }
    
}
