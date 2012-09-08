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
public interface DataQueries {

        void initTables();
        Integer getFound();
        List<SaleAlert> getNewSaleAlertsForSeller(String player);
        void markSaleAlertSeen(int id);
        Auction getAuction(int id);
        int getTotalAuctionCount();
        Auction getAuctionForOffset(int offset);
        List<Auction> getAuctions(int togetAuctions,int from);
        List<Auction> getSearchAuctions(int to,int from,String search);
	void updatePlayerPassword(String player, String newPass); 
	void UpdateItemAuctionQuantity(Integer numberleft, Integer id);
        void DeleteAuction(Integer id);
	boolean hasMail(String player);
	AuctionPlayer getPlayer(String player);	
        List<Auction> getAuctionsLimitbyPlayer(String player,int to,int from,int table);
	void updatePlayerPermissions(String player, int canBuy, int canSell, int isAdmin);
	void createPlayer(String player, String pass, double money, int canBuy, int canSell, int isAdmin);
        void LogSellPrice(Integer name,Short damage,Integer time,String buyer,String seller,Integer quantity,Double price,String ench);
        String getPassword(String player);
	void updatePlayerMoney(String player, double money);
        AuctionItem getItemsById(int ID,int tableid);
	List<AuctionItem> getItems(String player, int itemID, int damage, boolean reverseOrder, int tableid);
	void CreateAuction(int quantity, int id);
        void updateforCreateAuction(int id,Double price);
	void updateItemQuantity(int quantity, int id);
        void updateTable(int id,int tableid);
	void createItem(int itemID, int itemDamage, String player, int quantity,Double price,String ench,int on,String type,String Itemname);
	void setAlert(String seller,Integer quantity,Double price,String buyer,String item);
        List<AuctionItem> getPlayerItems(String player);
	List<AuctionMail> getMail(String player);
        ItemStack Chant(String ench,ItemStack stack);
	void deleteMail(int id);
        ProfileMcMMO getMcMMOProfileMySql(String tableprefix,String player);
}
