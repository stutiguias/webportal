/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.request;

import java.net.Socket;
import me.stutiguias.webportal.init.WebAuction;
import me.stutiguias.webportal.webserver.Response;

/**
 *
 * @author Daniel
 */
public class FillAdminBox extends Response {
    
    WebAuction plugin;
    
    public FillAdminBox(WebAuction plugin,Socket s) {
        super(plugin,s);
    }
    
    public void ADMBOX1(String Hostadress) {
        
        print("","text/plain");
    }
}
