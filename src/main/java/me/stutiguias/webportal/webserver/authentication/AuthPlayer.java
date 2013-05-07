/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.webserver.authentication;

import me.stutiguias.webportal.settings.AuctionPlayer;

/**
 *
 * @author Daniel
 */
public class AuthPlayer {
    
    public  AuctionPlayer AuctionPlayer;
    
    public AuthPlayer() {
        AuctionPlayer = new AuctionPlayer();
    }
    
    public boolean Login(String name){
        if(true) {
           AuctionPlayer.setName(name);
        }
        return true;
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
    
}
