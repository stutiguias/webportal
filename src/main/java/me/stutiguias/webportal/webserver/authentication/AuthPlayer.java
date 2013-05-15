/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.authentication;

import java.util.Date;
import me.stutiguias.webportal.settings.AuctionPlayer;

/**
 *
 * @author Daniel
 */
public class AuthPlayer {
    
    public  AuctionPlayer AuctionPlayer;
    private  Date date;
    
    public AuthPlayer() {
        AuctionPlayer = new AuctionPlayer();
    }
    
    public boolean Login(String name){
        if(true) {
           AuctionPlayer.setName(name);
        }
        return true;
    }
    
    public String GetLogin() {
        return AuctionPlayer.getName();
    }
    
    public boolean isLogin(String name){
        return AuctionPlayer.isLogin();
    }
    
    public boolean CheckLogin(String ip) {
        if(AuctionPlayer.getIp().equals(ip)){
            return true;
        }else{
            return false;
        }
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
