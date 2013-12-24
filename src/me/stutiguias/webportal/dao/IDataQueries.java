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
        Shop getAuction(int id); // Get One Auction
        List<Shop> getAuctions(int from,int qtd); // 
        List<Shop> getSearchAuctions(int from,int qtd,String type);
        List<Shop> getAuctionsLimitbyPlayer(String player,int from,int qtd,int table);
        void UpdateItemAuctionQuantity(Integer numberleft, Integer id);
        int DeleteAuction(Integer id);
        void DeleteInfo(int auctionId);
        void setPriceAndTable(int id,Double price);
        
        //Player
        List<WebSitePlayer> FindAllPlayersWith(String partialName);
        boolean WebSiteBan(String player,String option);
	void updatePlayerPassword(String player, String newPass); 
        void updatePlayerPermissions(String player, int canBuy, int canSell, int isAdmin);
        void createPlayer(String player, String pass, int canBuy, int canSell, int isAdmin);
        String getPassword(String player);
	WebSitePlayer getPlayer(String player);	
        List<Shop> getPlayerItems(String player);
        String getLock(String player);
        boolean setLock(String player,String lock);
        
        // Player Mail
        boolean hasMail(String player);
        void deleteMail(int id);
        List<WebSiteMail> getMail(String player);
        List<WebSiteMail> getMail(String player,int to,int from);
        
	// Admin
        void LogSellPrice(Integer name,Short damage,Integer time,String buyer,String seller,Integer quantity,Double price,String ench);
        List<Transact> GetTransactOfPlayer(String player);

        //Items
        Shop getItemById(int ID,int tableid);
        List<Shop> getItem(String player, int itemID, int damage, boolean reverseOrder, int tableid);
        List<Shop> getItemByName(String player, boolean reverseOrder, int tableid);
        void updateItemQuantity(int quantity, int id);
        void updateTable(int id,int tableid);
	//void CreateAuction(int quantity, int id);
	int createItem(int itemID, int itemDamage, String player, int quantity,Double price,String ench,int tableId,String type,String searchtype);
        int GetMarketPriceofItem(int itemID, int itemDamage);
        int InsertItemInfo(int auctionId,String type,String value);
        String GetItemInfo(int auctionId,String type);
        ItemStack EnchantItem(String ench,ItemStack stack);
        
        //WithList
        List<Shop> GetBuyList(String player,int from,int qtd);
        
        //Plugins
        ProfileMcMMO getMcMMOProfileMySql(String tableprefix,String player);
}
