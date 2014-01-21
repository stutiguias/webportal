/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.authentication;

import java.util.Date;
import me.stutiguias.webportal.model.WebSitePlayer;

/**
 *
 * @author Daniel
 */
public class LoggedPlayer {
    
    public  WebSitePlayer WebSitePlayer;
    private  Date date;
    
    public LoggedPlayer() {
        WebSitePlayer = new WebSitePlayer();
    }
    
    public boolean Login(String name){
        if(true) {
           WebSitePlayer.setName(name);
        }
        return true;
    }
    
    public String GetLogin() {
        return WebSitePlayer.getName();
    }
    
    public boolean isLogin(String name){
        return WebSitePlayer.isLogin();
    }
    
    public boolean CheckLogin(String ip) {
        return WebSitePlayer.getIp().equals(ip);
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

}
