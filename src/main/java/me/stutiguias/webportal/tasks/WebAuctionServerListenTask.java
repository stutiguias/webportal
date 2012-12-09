/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.tasks;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import me.stutiguias.webportal.init.WebPortal;

/**
 *
 * @author Daniel
 */
public class WebAuctionServerListenTask extends Thread {
   
    private WebPortal plugin;
    int Port;
    WebAuctionServerTask WebAuctionServerTask;
    public int NUM_CONN_MAX;
    public ServerSocket server;
    
    public WebAuctionServerListenTask(WebPortal plugin,int NUM_CONN_MAX)
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
                WebPortal.logger.info(plugin.logPrefix + "WebServer listening on port "+Port);
                while(!server.isClosed()){
                        if(plugin.connections < NUM_CONN_MAX) {
                            client = server.accept();
                            WebAuctionServerTask = new WebAuctionServerTask(plugin, client);
                            WebAuctionServerTask.start();
                            plugin.connections++;
                        }else{
                            WebPortal.logger.log(Level.WARNING, plugin.logPrefix + " The max number of Simultaneous as Reach");
                        }
                }
            }catch(Exception e){ 
                WebPortal.logger.info((new StringBuilder()).append("ERROR : ").append(e.getMessage()).toString());
            }
        }catch(Exception ex) {
            WebPortal.logger.info((new StringBuilder()).append("ERROR : ").append(ex.getMessage()).toString());
        }
    }
    
}
