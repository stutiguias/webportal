/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.dao;

import java.util.List;
import me.stutiguias.webportal.plugins.ProfileMcMMO;
import me.stutiguias.webportal.settings.*;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Daniel
 */
public interface IDataQueries {

        void initTables(); // Init Tables
        Integer getFound(); // Found On Last Search
        
        // Alert
        void setAlert(String seller,Integer quantity,Double price,String buyer,String item);
        List<SaleAlert> getNewSaleAlertsForSeller(String player);
        void markSaleAlertSeen(int id);
        
        // Auction
        Auction getAuction(int id); // Get One Auction
        List<Auction> getAuctions(int to,int from); // 
        List<Auction> getSearchAuctions(int to,int from,String type);
        List<Auction> getAuctionsLimitbyPlayer(String player,int to,int from,int table);
        void UpdateItemAuctionQuantity(Integer numberleft, Integer id);
        void DeleteAuction(Integer id);
        void DeleteInfo(int auctionId);
        void setPriceAndTable(int id,Double price);
        
        //Player
        List<AuctionPlayer> FindAllPlayersWith(String partialName);
        boolean WebSiteBan(String player,String option);
	void updatePlayerPassword(String player, String newPass); 
        void updatePlayerPermissions(String player, int canBuy, int canSell, int isAdmin);
        void createPlayer(String player, String pass, int canBuy, int canSell, int isAdmin);
        String getPassword(String player);
	AuctionPlayer getPlayer(String player);	
        List<Auction> getPlayerItems(String player);
        String getLock(String player);
        boolean setLock(String player,String lock);
        
        // Player Mail
        boolean hasMail(String player);
        void deleteMail(int id);
        List<AuctionMail> getMail(String player);
        List<AuctionMail> getMail(String player,int to,int from);
        
	// Admin
        void LogSellPrice(Integer name,Short damage,Integer time,String buyer,String seller,Integer quantity,Double price,String ench);
        List<Transact> GetTransactOfPlayer(String player);

        //Items
        Auction getItemById(int ID,int tableid);
        List<Auction> getItem(String player, int itemID, int damage, boolean reverseOrder, int tableid);
        List<Auction> getItemByName(String player, boolean reverseOrder, int tableid);
        void updateItemQuantity(int quantity, int id);
        void updateTable(int id,int tableid);
	//void CreateAuction(int quantity, int id);
	int createItem(int itemID, int itemDamage, String player, int quantity,Double price,String ench,int tableId,String type,String searchtype);
        int GetMarketPriceofItem(int itemID, int itemDamage);
        int InsertItemInfo(int auctionId,String type,String value);
        String GetItemInfo(int auctionId,String type);
        ItemStack Chant(String ench,ItemStack stack);
        
        //Plugins
        ProfileMcMMO getMcMMOProfileMySql(String tableprefix,String player);
}
