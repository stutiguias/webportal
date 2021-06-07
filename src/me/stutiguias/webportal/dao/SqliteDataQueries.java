/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.dao;

import me.stutiguias.webportal.dao.connection.WALConnection;
import me.stutiguias.webportal.dao.connection.WALDriver;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;
import me.stutiguias.webportal.init.WebPortal;

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
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException e) {
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
			executeRawSQL("CREATE TABLE WA_Players "
                                + "(id INTEGER PRIMARY KEY, "
                                + "name VARCHAR(255), "
                                + "pass VARCHAR(255), "
                                + "money DOUBLE, "
                                + "itemsSold INTEGER, "
                                + "itemsBought INTEGER, "
                                + "earnt DOUBLE, "
                                + "spent DOUBLE,"
                                + " canBuy INTEGER, "
                                + "canSell INTEGER, "
                                + "isAdmin INTEGER,"
                                + "lock VARCHAR(1) Default 'N',"
                                + "webban VARCHAR(1) Default 'N');");
		}
		if (!tableExists("WA_StorageCheck")) {
			WebPortal.logger.log(Level.INFO, "{0} Creating table WA_StorageCheck", plugin.logPrefix);
			executeRawSQL("CREATE TABLE WA_StorageCheck (id INTEGER PRIMARY KEY, time INTEGER);");
		}
		if (!tableExists("WA_Auctions")) {
			WebPortal.logger.log(Level.INFO, "{0} Creating table WA_Auctions", plugin.logPrefix);
			executeRawSQL("CREATE TABLE WA_Auctions "
                                + "(id INTEGER PRIMARY KEY, "
                                + "name VARCHAR(255), "
                                + "damage INTEGER, "
                                + "player VARCHAR(255), "
                                + "quantity INTEGER, "
                                + "price DOUBLE, "
                                + "created INTEGER, "
                                + "ench VARCHAR(45),"
                                + " tableid INTEGER(1), "
                                + "type VARCHAR(45), "
                                + "itemname VARCHAR(45), "
                                + "searchtype VARCHAR(45));");
		}
		if (!tableExists("WA_SellPrice")) {
			WebPortal.logger.log(Level.INFO, "{0} Creating table WA_SellPrice", plugin.logPrefix);
			executeRawSQL("CREATE TABLE WA_SellPrice "
                                + "(id INTEGER PRIMARY KEY, "
                                + "name VARCHAR(255), "
                                + "damage INTEGER, "
                                + "time INTEGER, "
                                + "quantity INTEGER, "
                                + "price DOUBLE, "
                                + "seller VARCHAR(255), "
                                + "buyer VARCHAR(255), "
                                + "ench VARCHAR(45));");
		}
		if (!tableExists("WA_SaleAlerts")) {
			WebPortal.logger.log(Level.INFO, "{0} Creating table WA_SaleAlerts", plugin.logPrefix);
			executeRawSQL("CREATE TABLE WA_SaleAlerts (id INTEGER PRIMARY KEY, seller VARCHAR(255), quantity INTEGER, price DOUBLE, buyer VARCHAR(255), item VARCHAR(255), alerted BOOLEAN Default '0');");
		}
                if (!tableExists("WA_ItemExtraInfo")) {
			WebPortal.logger.log(Level.INFO, "{0} Creating table WA_ItemExtraInfo", plugin.logPrefix);
			executeRawSQL("CREATE TABLE WA_ItemExtraInfo (id INTEGER PRIMARY KEY, auctionId INTEGER, type VARCHAR(45), value TEXT );");
		}
                if (!tableExists("WA_DbVersion")) {
                        WebPortal.logger.log(Level.INFO, "{0} Creating table WA_DbVersion", plugin.logPrefix);
                        executeRawSQL("CREATE TABLE WA_DbVersion (id INTEGER PRIMARY KEY, dbversion INTEGER);");
                        executeRawSQL("INSERT INTO WA_DbVersion (dbversion) VALUES (1)");
                }
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
    public boolean setLock(String player, String lock) {
        WALConnection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
                st = conn.prepareStatement("UPDATE WA_Players SET lock = ? WHERE name = ?");
                st.setString(1, lock);
                st.setString(2, player);
                st.executeUpdate();
        } catch (SQLException e) {
                WebPortal.logger.log(Level.WARNING, "{0} Unable setLock", plugin.logPrefix);
                WebPortal.logger.warning(e.getMessage());
        } finally {
                closeResources(conn, st, rs);
        }
        return true;
    }
    
}
