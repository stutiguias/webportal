/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.tasks;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import me.stutiguias.webportal.init.WebPortal;

/**
 *
 * @author Daniel
 */
public class WebPortalHttpServer extends Thread {

    private WebPortal plugin;
    int Port;
    public int NUM_CONN_MAX;
    public HttpServer server;
    public static final int threadCount = 10;
    
    public WebPortalHttpServer(WebPortal plugin,int NUM_CONN_MAX)
    {
        this.NUM_CONN_MAX = NUM_CONN_MAX;
        Port = plugin.port;
        this.plugin = plugin;
    }
    
    @Override
    public void run()
    {
        try {
            WebPortal.logger.log(Level.INFO,"{0} Start HTTP Server",plugin.logPrefix);
            
            server = HttpServer.create(new InetSocketAddress(Port),NUM_CONN_MAX);
            server.createContext("/", new WebPortalHttpHandler(plugin));
            server.start();
            
            WebPortal.logger.log(Level.INFO,"{0} Server start on port {1} ",new Object[]{ plugin.logPrefix , Port });
        }catch(Exception ex) {
            WebPortal.logger.info((new StringBuilder()).append("ERROR : ").append(ex.getMessage()).toString());
        }
    }
    
}