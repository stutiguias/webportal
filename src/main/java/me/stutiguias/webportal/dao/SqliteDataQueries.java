/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.dao;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.settings.*;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Daniel
 */
public class SqliteDataQueries extends Queries {

    public SqliteDataQueries(WebPortal plugin) {
        super(plugin);
    }

    @Override
    public WALConnection getConnection() {
            try {
                    Driver driver = (Driver) Class.forName("org.sqlite.JDBC").newInstance();
                    WALDriver jDriver = new WALDriver(driver);
                    DriverManager.registerDriver(jDriver);
                    connection = new WALConnection(DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + File.separator + "data.db"));
                    return connection;
            } catch (Exception e) {
                    WebPortal.logger.log(Level.SEVERE, "{0} Exception getting SQLite WALConnection", plugin.logPrefix);
                    WebPortal.logger.warning(e.getMessage());
            }
            return null;
    }
	
    private boolean tableExists(String tableName) {
        boolean exists = false;
        WALConnection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
                st = conn.prepareStatement("SELECT name FROM sqlite_master WHERE type = 'table' and name LIKE ?");
                st.setString(1, tableName);
                rs = st.executeQuery();
                while (rs.next()) {
                        exists = true;
                }
        } catch (SQLException e) {
                WebPortal.logger.log(Level.WARNING, "{0} Unable to check if table exists: {1}", new Object[]{plugin.logPrefix, tableName});
                WebPortal.logger.warning(e.getMessage());
        } finally {
                closeResources(conn, st, rs);
        }
        return exists;
    }

    @Override
    public void initTables() {
                File dbFile = new File(plugin.getDataFolder() + File.separator +  "data.db");
                if(!dbFile.exists()) {
                    try {
                        dbFile.createNewFile();
                    } catch (IOException ex) {
                        WebPortal.logger.log(Level.WARNING,"{0} Can`t create file db", plugin.logPrefix);
                    }
                }
                if (!tableExists("WA_Players")) {
			WebPortal.logger.log(Level.INFO, "{0} Creating table WA_Players", plugin.logPrefix);
			executeRawSQL("CREATE TABLE WA_Players (id INTEGER PRIMARY KEY, name VARCHAR(255), pass VARCHAR(255), money DOUBLE, itemsSold INTEGER, itemsBought INTEGER, earnt DOUBLE, spent DOUBLE, canBuy INTEGER, canSell INTEGER, isAdmin INTEGER);");
		}
		if (!tableExists("WA_StorageCheck")) {
			WebPortal.logger.log(Level.INFO, "{0} Creating table WA_StorageCheck", plugin.logPrefix);
			executeRawSQL("CREATE TABLE WA_StorageCheck (id INTEGER PRIMARY KEY, time INTEGER);");
		}
		if (!tableExists("WA_Auctions")) {
			WebPortal.logger.log(Level.INFO, "{0} Creating table WA_Auctions", plugin.logPrefix);
			executeRawSQL("CREATE TABLE WA_Auctions (id INTEGER PRIMARY KEY, name INTEGER, damage INTEGER, player VARCHAR(255), quantity INTEGER, price DOUBLE, created INTEGER, ench VARCHAR(45), tableid INTEGER(1));");
		}
		if (!tableExists("WA_SellPrice")) {
			WebPortal.logger.log(Level.INFO, "{0} Creating table WA_SellPrice", plugin.logPrefix);
			executeRawSQL("CREATE TABLE WA_SellPrice (id INTEGER PRIMARY KEY, name INTEGER, damage INTEGER, time INTEGER, quantity INTEGER, price DOUBLE, seller VARCHAR(255), buyer VARCHAR(255), ench VARCHAR(45));");
		}
		if (!tableExists("WA_MarketPrices")) {
			WebPortal.logger.log(Level.INFO, "{0} Creating table WA_MarketPrices", plugin.logPrefix);
			executeRawSQL("CREATE TABLE WA_MarketPrices (id INTEGER PRIMARY KEY, name INTEGER, damage INTEGER, time INTEGER, marketprice DOUBLE, ref INTEGER);");
		}
		if (!tableExists("WA_SaleAlerts")) {
			WebPortal.logger.log(Level.INFO, "{0} Creating table WA_SaleAlerts", plugin.logPrefix);
			executeRawSQL("CREATE TABLE WA_SaleAlerts (id INTEGER PRIMARY KEY, seller VARCHAR(255), quantity INTEGER, price DOUBLE, buyer VARCHAR(255), item VARCHAR(255), alerted BOOLEAN Default '0');");
		}
                if (!tableExists("WA_DbVersion")) {
                        WebPortal.logger.log(Level.INFO, "{0} Creating table WA_DbVersion", plugin.logPrefix);
                        executeRawSQL("CREATE TABLE WA_DbVersion (id INTEGER PRIMARY KEY, dbversion INTEGER);");
                        executeRawSQL("INSERT INTO WA_DbVersion (dbversion) VALUES (1)");
                        executeRawSQL("ALTER TABLE WA_Auctions ADD COLUMN type VARCHAR(45) NULL;");
                        executeRawSQL("ALTER TABLE WA_Auctions ADD COLUMN itemname VARCHAR(45) NULL;");
                        executeRawSQL("ALTER TABLE WA_Auctions ADD COLUMN searchtype VARCHAR(45) NULL;");
                }
                if (tableVersion() == 1) {
                        WebPortal.logger.log(Level.INFO, "{0} Update DB version to 2", plugin.logPrefix);
                        executeRawSQL("ALTER TABLE WA_Players ADD COLUMN lock VARCHAR(1) Default 'N';");
                        executeRawSQL("UPDATE WA_DbVersion SET dbversion = 2 where id = 1");
                }
                if (tableVersion() == 2) {
                        WebPortal.logger.log(Level.INFO, "{0} Update DB version to 3", plugin.logPrefix);
                        executeRawSQL("CREATE TABLE WA_ItemExtraInfo (id INTEGER PRIMARY KEY, auctionId INTEGER, type VARCHAR(45), value TEXT );");
                        executeRawSQL("UPDATE WA_DbVersion SET dbversion = 3 where id = 1");
                }
    }

    @Override
    public List<Auction> getAuctions(int to, int from) {
                Auction auction;
		WALConnection conn = getConnection();
		PreparedStatement st = null;
		ResultSet rs = null;
                List<Auction> la = new ArrayList<Auction>();
                
		try {
			st = conn.prepareStatement("SELECT name,damage,player,quantity,price,id,created,ench FROM WA_Auctions where tableid = ? LIMIT ? , ?");
                        st.setInt(1, plugin.Auction);
                        st.setInt(2, to);
                        st.setInt(3, from);
			rs = st.executeQuery();
			while (rs.next()) {
				auction = new Auction();
				auction.setId(rs.getInt("id"));
                                ItemStack stack = new ItemStack(rs.getInt("name"), rs.getInt("quantity"), rs.getShort("damage"));
                                stack = Chant(rs.getString("ench"), stack);
				auction.setItemStack(stack);
				auction.setPlayerName(rs.getString("player"));
				auction.setPrice(rs.getDouble("price"));
				auction.setCreated(rs.getInt("created"));
                                la.add(auction);
			}
                        st = conn.prepareStatement("SELECT COUNT(*) FROM WA_Auctions where tableid = ? LIMIT ? , ?");
                        st.setInt(1, plugin.Auction);
                        st.setInt(2, to);
                        st.setInt(3, from);
			rs = st.executeQuery();
			while (rs.next()) {
		              found = rs.getInt(1);
			}
		} catch (SQLException e) {
			WebPortal.logger.log(Level.WARNING, "{0} Unable to get auction ", plugin.logPrefix);
			WebPortal.logger.warning(e.getMessage());
		} finally {
			closeResources(conn, st, rs);
		}
		return la;
    }

    @Override
    public List<Auction> getSearchAuctions(int to, int from, String searchtype) {
                Auction auction;
		WALConnection conn = getConnection();
		PreparedStatement st = null;
		ResultSet rs = null;
                List<Auction> la = new ArrayList<>();
                
		try {
			st = conn.prepareStatement("SELECT name,damage,player,quantity,price,id,created,ench,type FROM WA_Auctions where tableid = ? and searchtype = ? LIMIT ? , ?");
                        st.setInt(1, plugin.Auction);
                        st.setString(2, searchtype);
                        st.setInt(3, to);
                        st.setInt(4, from);
			rs = st.executeQuery();
			while (rs.next()) {
				auction = new Auction();
				auction.setId(rs.getInt("id"));
                                ItemStack stack = new ItemStack(rs.getInt("name"), rs.getInt("quantity"), rs.getShort("damage"));
                                stack = Chant(rs.getString("ench"), stack);
                                auction.setType(rs.getString("type"));
				auction.setItemStack(stack);
				auction.setPlayerName(rs.getString("player"));
				auction.setPrice(rs.getDouble("price"));
				auction.setCreated(rs.getInt("created"));
                                la.add(auction);
			}
  			st = conn.prepareStatement("SELECT COUNT(*) FROM WA_Auctions where tableid = ? and searchtype = ? LIMIT ? , ?");
                        st.setInt(1, plugin.Auction);
                        st.setString(2, searchtype);
                        st.setInt(3, to);
                        st.setInt(4, from);
			rs = st.executeQuery();
			while (rs.next()) {
		              found = rs.getInt(1);
			}
		} catch (SQLException e) {
			WebPortal.logger.log(Level.WARNING, "{0}Unable to get auction ", plugin.logPrefix);
			WebPortal.logger.warning(e.getMessage());
		} finally {
			closeResources(conn, st, rs);
		}
		return la;
    }

    @Override
    public List<Auction> getAuctionsLimitbyPlayer(String player,int to,int from,int table) {
            Auction auction;
            WALConnection conn = getConnection();
            PreparedStatement st = null;
            ResultSet rs = null;
            List<Auction> la = new ArrayList<Auction>();

            try {
                    st = conn.prepareStatement("SELECT name,damage,player,quantity,price,id,created,ench,type,searchtype FROM WA_Auctions where player = ? and tableid = ? LIMIT ? , ?");
                    st.setString(1, player);
                    st.setInt(2, table);
                    st.setInt(3, to);
                    st.setInt(4, from);
                    rs = st.executeQuery();
                    while (rs.next()) {
                            auction = new Auction();
                            auction.setId(rs.getInt("id"));
                            ItemStack stack = new ItemStack(rs.getInt("name"), rs.getInt("quantity"), rs.getShort("damage"));
                            stack = Chant(rs.getString("ench"), stack);
                            auction.setItemStack(stack);
                            auction.setPlayerName(rs.getString("player"));
                            auction.setType(rs.getString("searchtype"));
                            auction.setPrice(rs.getDouble("price"));
                            auction.setCreated(rs.getInt("created"));
                            la.add(auction);
                    }
                    st = conn.prepareStatement("SELECT count(*) FROM WA_Auctions where player = ? and tableid = ? LIMIT ? , ?");
                    st.setString(1, player);
                    st.setInt(2, table);
                    st.setInt(3, to);
                    st.setInt(4, from);
                    rs = st.executeQuery();
                    while (rs.next()) {
                            found = rs.getInt(1);
                    }
            } catch (SQLException e) {
                    WebPortal.logger.log(Level.WARNING, "{0}Unable to get auction ", plugin.logPrefix);
                    WebPortal.logger.warning(e.getMessage());
            } finally {
                    closeResources(conn, st, rs);
            }
            return la;
    }

    @Override
    public String getPassword(String player) {
            WALConnection conn = getConnection();
            PreparedStatement st = null;
            ResultSet rs = null;
            String pass = null;

            try {
                    st = conn.prepareStatement("SELECT pass FROM WA_Players WHERE name = ?");
                    st.setString(1, player);
                    rs = st.executeQuery();
                    while (rs.next()) {
                        pass = rs.getString("pass");
                    }
            } catch (SQLException e) {
                    WebPortal.logger.log(Level.WARNING, "{0} Unable to update player permissions in DB", plugin.logPrefix);
                    WebPortal.logger.warning(e.getMessage());
            } finally {
                    closeResources(conn, st, rs);
            }
            return pass;
    }

    @Override
    public List<AuctionMail> getMail(String player, int to, int from) {
        List<AuctionMail> auctionMails = new ArrayList<AuctionMail>();

        WALConnection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
                st = conn.prepareStatement("SELECT id,name,quantity,damage,player,ench FROM WA_Auctions WHERE player = ? and tableid = ? LIMIT ? , ?");
                st.setString(1, player);
                st.setInt(2, plugin.Mail);
                st.setInt(3, to);
                st.setInt(4, from);
                rs = st.executeQuery();
                while (rs.next()) {
                        AuctionMail auctionMail = new AuctionMail();
                        auctionMail.setId(rs.getInt("id"));
                        ItemStack stack = new ItemStack(rs.getInt("name"), rs.getInt("quantity"), rs.getShort("damage"));
                        stack = Chant(rs.getString("ench"),stack);
                        auctionMail.setItemStack(stack);
                        auctionMail.setPlayerName(rs.getString("player"));
                        auctionMails.add(auctionMail);
                }
        } catch (SQLException e) {
                WebPortal.logger.log(Level.WARNING, "{0} Unable to get mail for player {1}", new Object[]{plugin.logPrefix, player});
                WebPortal.logger.warning(e.getMessage());
        } finally {
                closeResources(conn, st, rs);
        }
        return auctionMails;
    }
    
}
