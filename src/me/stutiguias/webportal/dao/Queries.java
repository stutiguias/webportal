/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.dao;

import me.stutiguias.webportal.dao.connection.WALConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.plugins.ProfileMcMMO;
import me.stutiguias.webportal.settings.Shop;
import me.stutiguias.webportal.settings.WebSiteMail;
import me.stutiguias.webportal.settings.WebSitePlayer;
import me.stutiguias.webportal.settings.SaleAlert;
import me.stutiguias.webportal.settings.Transact;
import me.stutiguias.webportal.settings.WebItemStack;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

/**
 *
 * @author Daniel
 */
public class Queries implements IDataQueries {
        
    protected WebPortal plugin;
    protected WALConnection connection;
    protected Integer found;
    
    public Queries(WebPortal plugin) {
        this.plugin = plugin;
    }

    @Override
    public void initTables() {
        throw new UnsupportedOperationException("Implement On Children.");
    }

    @Override
    public Integer getFound() {
        return found;
    }
        
    protected WALConnection getConnection() {
        throw new UnsupportedOperationException("Implement On Children.");
    }
    
    public void closeResources(WALConnection conn, Statement st, ResultSet rs) {
        if (null != rs) {
            try {
                rs.close();
            } catch (SQLException e) {
            }
        }
        if (null != st) {
            try {
                st.close();
            } catch (SQLException e) {
            }
        }
        if (null != conn) conn.close();
    }
        
    public int tableVersion() {
        int version = 0;
        WALConnection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
                st = conn.prepareStatement("SELECT dbversion FROM WA_DbVersion");
                rs = st.executeQuery();
                while (rs.next()) {
                        version = rs.getInt("dbversion");
                }
        } catch (SQLException e) {
                WebPortal.logger.log(Level.WARNING, "{0} Unable to check if table version ", plugin.logPrefix);
                WebPortal.logger.warning(e.getMessage());
        } finally {
                closeResources(conn, st, rs);
        }
        return version;
    }
    
    public void executeRawSQL(String sql) {
        WALConnection conn = getConnection();
        Statement st = null;
        ResultSet rs = null;

        try {
            st = conn.createStatement();
            st.executeUpdate(sql);
        } catch (SQLException e) {
            WebPortal.logger.log(Level.WARNING, "{0} Exception executing raw SQL {1}", new Object[]{plugin.logPrefix, sql});
            WebPortal.logger.warning(e.getMessage());
        } finally {
            closeResources(conn, st, rs);
        }
    }
                
    @Override
    public void setAlert(String seller, Integer quantity, Double price, String buyer, String item) {
        WALConnection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
                st = conn.prepareStatement("INSERT INTO WA_SaleAlerts (seller, quantity, price, buyer, item) VALUES (?,?,?,?,?)");
                st.setString(1, seller);
                st.setInt(2, quantity);
                st.setDouble(3, price);
                st.setString(4, buyer);
                st.setString(5, item);
                st.executeUpdate();
        } catch (SQLException e) {
                WebPortal.logger.log(Level.WARNING, "{0} Unable to alert item", plugin.logPrefix);
                WebPortal.logger.warning(e.getMessage());
        } finally {
                closeResources(conn, st, rs);
        }
    }

    @Override
     public List<Shop> getSearchAuctions(int from,int qtd,String searchtype) {
             Shop auction;
             WALConnection conn = getConnection();
             PreparedStatement st = null;
             ResultSet rs = null;
             List<Shop> la = new ArrayList<>();

             try {
                     st = conn.prepareStatement("SELECT name,damage,player,quantity,price,id,created,ench,type,tableid FROM WA_Auctions where ( tableid = ? or tableid = ? ) and searchtype = ? LIMIT ? , ?");
                     st.setInt(1, plugin.Sell);
                     st.setInt(2, plugin.Buy);
                     st.setString(3, searchtype);
                     st.setInt(4, from);
                     st.setInt(5, qtd);
                     rs = st.executeQuery();
                     while (rs.next()) {
                             auction = new Shop();
                             auction.setId(rs.getInt("id"));
                             WebItemStack stack = new WebItemStack(rs.getInt("name"), rs.getInt("quantity"), rs.getShort("damage"));
                             stack = EnchantItem(rs.getString("ench"), stack);
                             auction.setType(rs.getString("type"));
                             auction.setItemStack(stack);
                             auction.setPlayerName(rs.getString("player"));
                             auction.setPrice(rs.getDouble("price"));
                             auction.setCreated(rs.getInt("created"));
                             auction.setTableId(rs.getInt("tableid"));
                             la.add(auction);
                     }
                    st = conn.prepareStatement("SELECT COUNT(*) FROM WA_Auctions where ( tableid = ? or tableid = ? ) and searchtype = ?");
                    st.setInt(1, plugin.Sell);
                    st.setInt(2, plugin.Buy);
                    st.setString(3, searchtype);
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
    public List<SaleAlert> getNewSaleAlertsForSeller(String player) {
        List<SaleAlert> saleAlerts = new ArrayList<>();
        WALConnection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
                st = conn.prepareStatement("SELECT * FROM WA_SaleAlerts WHERE seller = ? AND alerted = ?");
                st.setString(1, player);
                st.setInt(2, 0);
                rs = st.executeQuery();
                while (rs.next()) {
                        SaleAlert saleAlert = new SaleAlert();
                        saleAlert.setId(rs.getInt("id"));
                        saleAlert.setBuyer(rs.getString("buyer"));
                        saleAlert.setItem(rs.getString("item"));
                        saleAlert.setQuantity(rs.getInt("quantity"));
                        saleAlert.setPriceEach(rs.getDouble("price"));
                        saleAlerts.add(saleAlert);
                }
        } catch (SQLException e) {
                WebPortal.logger.log(Level.WARNING, "{0} Unable to get sale alerts for player {1}", new Object[]{plugin.logPrefix, player});
                WebPortal.logger.warning(e.getMessage());
        } finally {
                closeResources(conn, st, rs);
        }
        return saleAlerts;
    }

    @Override
    public void markSaleAlertSeen(int id) {
        WALConnection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
                st = conn.prepareStatement("UPDATE WA_SaleAlerts SET alerted = ? WHERE id = ?");
                st.setInt(1, 1);
                st.setInt(2, id);
                st.executeUpdate();
        } catch (SQLException e) {
                WebPortal.logger.log(Level.WARNING, "{0} Unable to mark sale alert seen {1}", new Object[]{plugin.logPrefix, id});
                WebPortal.logger.warning(e.getMessage());
        } finally {
                closeResources(conn, st, rs);
        }
    }

    @Override
    public Shop getAuction(int id) {
        
        Shop auction = null;
        WALConnection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
                st = conn.prepareStatement("SELECT name,tableid,quantity,damage,player,price,created,ench FROM WA_Auctions WHERE id = ?");
                st.setInt(1, id);
                rs = st.executeQuery();
                while (rs.next()) {
                        auction = new Shop();
                        auction.setId(id);
                        WebItemStack stack = new WebItemStack(rs.getInt("name"), rs.getInt("quantity"), rs.getShort("damage"));
                        stack = EnchantItem(rs.getString("ench"),stack);
                        auction.setItemStack(stack);
                        auction.setPlayerName(rs.getString("player"));
                        auction.setPrice(rs.getDouble("price"));
                        auction.setCreated(rs.getInt("created"));
                        auction.setEnchantments(rs.getString("ench"));
                        auction.setTableId(rs.getInt("tableid"));
                }
        } catch (SQLException e) {
                WebPortal.logger.log(Level.WARNING, "{0} Unable to get auction {1}", new Object[]{plugin.logPrefix, id});
                WebPortal.logger.warning(e.getMessage());
        } finally {
                closeResources(conn, st, rs);
        }
        return auction;
    }

        @Override
        public List<Shop> getAuctions(int from,int qtd) {
		Shop auction;
		WALConnection conn = getConnection();
		PreparedStatement st = null;
		ResultSet rs = null;
                List<Shop> la = new ArrayList<>();
                
		try {
			st = conn.prepareStatement("SELECT name,damage,player,quantity,price,id,created,ench,tableid FROM WA_Auctions where tableid = ? or tableid = ? LIMIT ? , ?");
                        st.setInt(1, plugin.Sell);
                        st.setInt(2, plugin.Buy);
                        st.setInt(3, from);
                        st.setInt(4, qtd);
			rs = st.executeQuery();
			while (rs.next()) {
				auction = new Shop();
				auction.setId(rs.getInt("id"));
                                WebItemStack stack = new WebItemStack(rs.getInt("name"), rs.getInt("quantity"), rs.getShort("damage"));
                                stack = EnchantItem(rs.getString("ench"), stack);
				auction.setItemStack(stack);
				auction.setPlayerName(rs.getString("player"));
				auction.setPrice(rs.getDouble("price"));
				auction.setCreated(rs.getInt("created"));
                                auction.setTableId(rs.getInt("tableid"));
                                la.add(auction);
			}
                        st = conn.prepareStatement("SELECT COUNT(*) FROM WA_Auctions where tableid = ? or tableid = ?");
                        st.setInt(1, plugin.Sell);
                        st.setInt(2, plugin.Buy);
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
    public List<Shop> getAuctionsLimitbyPlayer(String player,int from,int qtd,int table) {
            Shop auction;
            WALConnection conn = getConnection();
            PreparedStatement st = null;
            ResultSet rs = null;
            List<Shop> la = new ArrayList<>();

            try {
                    st = conn.prepareStatement("SELECT name,damage,player,quantity,price,id,created,ench,type,searchtype FROM WA_Auctions where player = ? and tableid = ? LIMIT ? , ?");
                    st.setString(1, player);
                    st.setInt(2, table);
                    st.setInt(3, from);
                    st.setInt(4, qtd);
                    rs = st.executeQuery();
                    while (rs.next()) {
                            auction = new Shop();
                            auction.setId(rs.getInt("id"));
                            WebItemStack stack = new WebItemStack(rs.getInt("name"), rs.getInt("quantity"), rs.getShort("damage"));
                            stack = EnchantItem(rs.getString("ench"), stack);
                            auction.setItemStack(stack);
                            auction.setPlayerName(rs.getString("player"));
                            auction.setPrice(rs.getDouble("price"));
                            auction.setType(rs.getString("searchtype"));
                            auction.setCreated(rs.getInt("created"));
                            la.add(auction);
                    }
                    st = conn.prepareStatement("SELECT count(*) FROM WA_Auctions where player = ? and tableid = ?");
                    st.setString(1, player);
                    st.setInt(2, table);
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
    public void UpdateItemAuctionQuantity(Integer numberleft, Integer id) {
        WALConnection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
                st = conn.prepareStatement("UPDATE WA_Auctions SET quantity = ? WHERE id = ?");
                st.setInt(1, numberleft);
                st.setInt(2, id);
                st.executeUpdate();
        } catch (SQLException e) {
                WebPortal.logger.log(Level.WARNING, "{0} Unable to update Auction: {1} error : {2}", new Object[]{plugin.logPrefix, id, e.getMessage()});
        } finally {
                closeResources(conn, st, rs);
        }
    }

    @Override
    public int DeleteAuction(Integer id) {
        WALConnection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;
        int result = 0;
        
        try {
                st = conn.prepareStatement("DELETE FROM WA_Auctions WHERE id = ?");
                st.setInt(1, id);
                result = st.executeUpdate();
        } catch (SQLException e) {
                WebPortal.logger.log(Level.WARNING, "{0} Unable to delete Auction: {1}", new Object[]{plugin.logPrefix, id});
        } finally {
                closeResources(conn, st, rs);
        }
        DeleteInfo(id);
        
        return result;
    }

    @Override
    public void setPriceAndTable(int id, Double price) {
        WALConnection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
                st = conn.prepareStatement("UPDATE WA_Auctions SET price = ? , tableid = ? WHERE id = ?");
                st.setDouble(1, price);
                st.setInt(2, plugin.Sell);
                st.setInt(3, id);
                st.executeUpdate();
        } catch (SQLException e) {
                WebPortal.logger.log(Level.WARNING, "{0} Unable to update item quantity in DB", plugin.logPrefix);
                WebPortal.logger.warning(e.getMessage());
        } finally {
                closeResources(conn, st, rs);
        }
    }

    @Override
    public void updatePlayerPassword(String player, String newPass) {
        WALConnection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
                st = conn.prepareStatement("UPDATE WA_Players SET pass = ? WHERE name = ?");
                st.setString(1, newPass);
                st.setString(2, player);
                st.executeUpdate();
        } catch (SQLException e) {
                WebPortal.logger.log(Level.WARNING, "{0} Unable to update password for player: {1} error : {2}", new Object[]{plugin.logPrefix, player, e.getMessage()});
        } finally {
                closeResources(conn, st, rs);
        }
    }

    @Override
    public void updatePlayerPermissions(String player, int canBuy, int canSell, int isAdmin) {
        WALConnection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
                st = conn.prepareStatement("UPDATE WA_Players SET canBuy = ?, canSell = ?, isAdmin = ? WHERE name = ?");
                st.setInt(1, canBuy);
                st.setInt(2, canSell);
                st.setInt(3, isAdmin);
                st.setString(4, player);
                st.executeUpdate();
        } catch (SQLException e) {
                WebPortal.logger.log(Level.WARNING, "{0} Unable to update player permissions in DB error : {1}", new Object[]{plugin.logPrefix, e.getMessage()});
        } finally {
                closeResources(conn, st, rs);
        }
    }

    @Override
    public void createPlayer(String player, String pass, int canBuy, int canSell, int isAdmin) {
        WALConnection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
                st = conn.prepareStatement("INSERT INTO WA_Players (name, pass, canBuy, canSell, isAdmin) VALUES (?, ?, ?, ?, ?)");
                st.setString(1, player);
                st.setString(2, pass);
                st.setInt(3, canBuy);
                st.setInt(4, canSell);
                st.setInt(5, isAdmin);
                st.executeUpdate();
        } catch (SQLException e) {
                WebPortal.logger.log(Level.WARNING, "{0} Unable to update player permissions in DB", plugin.logPrefix);
                WebPortal.logger.warning(e.getMessage());
        } finally {
                closeResources(conn, st, rs);
        }
    }

    @Override
    public String getPassword(String player) {
        throw new UnsupportedOperationException("Implement On Children.");
    }

    @Override 
    public List<WebSitePlayer> FindAllPlayersWith(String partialName) {
        List<WebSitePlayer> players = new ArrayList<>();
        WALConnection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
                st = conn.prepareStatement("SELECT id,name,pass,canBuy,canSell,isAdmin,webban FROM WA_Players WHERE name like ?");
                st.setString(1,"%"+partialName+"%");
                rs = st.executeQuery();
                while (rs.next()) {
                        WebSitePlayer player = new WebSitePlayer();
                        player.setId(rs.getInt("id"));
                        player.setName(rs.getString("name"));
                        player.setPass(rs.getString("pass"));
                        player.setCanBuy(rs.getInt("canBuy"));
                        player.setCanSell(rs.getInt("canSell"));
                        player.setIsAdmin(rs.getInt("isAdmin"));
                        player.setWebban(rs.getString("webban"));
                        players.add(player);
                }
        } catch (SQLException e) {
                WebPortal.logger.log(Level.WARNING, "{0} Unable to get player {1}", new Object[]{plugin.logPrefix, partialName});
                WebPortal.logger.warning(e.getMessage());
        } finally {
                closeResources(conn, st, rs);
        }
        return players;
    }
    
    @Override
    public WebSitePlayer getPlayer(String player) {
        WebSitePlayer waPlayer = null;
        WALConnection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
                st = conn.prepareStatement("SELECT id,name,pass,canBuy,canSell,isAdmin,webban FROM WA_Players WHERE name = ?");
                st.setString(1, player);
                rs = st.executeQuery();
                while (rs.next()) {
                        waPlayer = new WebSitePlayer();
                        waPlayer.setId(rs.getInt("id"));
                        waPlayer.setName(rs.getString("name"));
                        waPlayer.setPass(rs.getString("pass"));
                        waPlayer.setCanBuy(rs.getInt("canBuy"));
                        waPlayer.setCanSell(rs.getInt("canSell"));
                        waPlayer.setIsAdmin(rs.getInt("isAdmin"));
                        waPlayer.setWebban(rs.getString("webban"));
                }
        } catch (SQLException e) {
                WebPortal.logger.log(Level.WARNING, "{0} Unable to get player {1}", new Object[]{plugin.logPrefix, player});
                WebPortal.logger.warning(e.getMessage());
        } finally {
                closeResources(conn, st, rs);
        }
        return waPlayer;
    }

    @Override
    public List<Shop> getPlayerItems(String player) {
        List<Shop> auctions = new ArrayList<>();
        WALConnection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
                st = conn.prepareStatement("SELECT id,name,damage,player,quantity,ench FROM WA_Auctions WHERE player = ? and tableid = ?");
                st.setString(1, player);
                st.setInt(2,plugin.Myitems);
                rs = st.executeQuery();
                while (rs.next()) {
                        Shop auction = new Shop();
                        auction.setId(rs.getInt("id"));
                        auction.setName(rs.getInt("name"));
                        auction.setDamage(rs.getInt("damage"));
                        auction.setQuantity(rs.getInt("quantity"));
                        WebItemStack stack = new WebItemStack(rs.getInt("name"), rs.getInt("quantity"), rs.getShort("damage"));
                        stack = EnchantItem(rs.getString("ench"),stack);
                        auction.setItemStack(stack);
                        auction.setPlayerName(rs.getString("player"));
                        auction.setEnchantments(rs.getString("ench"));
                        auctions.add(auction);
                }
        } catch (SQLException e) {
                WebPortal.logger.log(Level.WARNING, "{0} Unable to get mail for player {1}", new Object[]{plugin.logPrefix, player});
                WebPortal.logger.warning(e.getMessage());
        } finally {
                closeResources(conn, st, rs);
        }
        return auctions;
    }

    @Override
    public String getLock(String player) {
        String Lock = null;
        WALConnection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
                st = conn.prepareStatement("SELECT lock FROM WA_Players where name = ?");
                st.setString(1, player);
                rs = st.executeQuery();
                while (rs.next()) {
                        Lock = rs.getString("lock");
                }
        } catch (SQLException e) {
                WebPortal.logger.log(Level.WARNING, "{0} Unable to maket price ", plugin.logPrefix);
                WebPortal.logger.warning(e.getMessage());
        } finally {
                closeResources(conn, st, rs);
        }
        return Lock;
    }

    @Override
    public boolean setLock(String player, String lock) {
        WALConnection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
                st = conn.prepareStatement("UPDATE WA_Players SET WA_Players.lock = ? WHERE name = ?");
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

    @Override
    public boolean hasMail(String player) {
        boolean exists = false;
        WALConnection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
                st = conn.prepareStatement("SELECT id FROM WA_Auctions WHERE player = ? and tableid = ?");
                st.setString(1, player);
                st.setInt(2, plugin.Mail);
                rs = st.executeQuery();
                while (rs.next()) {
                        exists = true;
                }
        } catch (SQLException e) {
                WebPortal.logger.log(Level.WARNING, "{0} Unable to check new mail for: {1}", new Object[]{plugin.logPrefix, player});
        } finally {
                closeResources(conn, st, rs);
        }
        return exists;
    }

    @Override
    public void deleteMail(int id) {
        WALConnection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
                st = conn.prepareStatement("DELETE FROM WA_Auctions WHERE id = ?");
                st.setInt(1, id);
                st.executeUpdate();
        } catch (SQLException e) {
                WebPortal.logger.log(Level.WARNING, "{0} Unable to remove mail {1}", new Object[]{plugin.logPrefix, id});
                WebPortal.logger.warning(e.getMessage());
        } finally {
                closeResources(conn, st, rs);
        }
    }

    @Override
    public List<WebSiteMail> getMail(String player) {
        List<WebSiteMail> auctionMails = new ArrayList<>();
        WALConnection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
                st = conn.prepareStatement("SELECT id,name,quantity,damage,player,ench FROM WA_Auctions WHERE player = ? and tableid = ?");
                st.setString(1, player);
                st.setInt(2, plugin.Mail);
                rs = st.executeQuery();
                while (rs.next()) {
                        WebSiteMail auctionMail = new WebSiteMail();
                        auctionMail.setId(rs.getInt("id"));
                        WebItemStack stack = new WebItemStack(rs.getInt("name"), rs.getInt("quantity"), rs.getShort("damage"));
                        stack = EnchantItem(rs.getString("ench"),stack);
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


    @Override
    public List<WebSiteMail> getMail(String player, int to, int from) {
        List<WebSiteMail> auctionMails = new ArrayList<>();

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
                        WebSiteMail auctionMail = new WebSiteMail();
                        auctionMail.setId(rs.getInt("id"));
                        WebItemStack stack = new WebItemStack(rs.getInt("name"), rs.getInt("quantity"), rs.getShort("damage"));
                        stack = EnchantItem(rs.getString("ench"),stack);
                        auctionMail.setItemStack(stack);
                        auctionMail.setPlayerName(rs.getString("player"));
                        auctionMails.add(auctionMail);
                }
                
                st = conn.prepareStatement("SELECT COUNT(*) FROM WA_Auctions WHERE player = ? and tableid = ? LIMIT ? , ?");
                st.setString(1, player);
                st.setInt(2, plugin.Mail);
                st.setInt(3, to);
                st.setInt(4, from);
                rs = st.executeQuery();
                while (rs.next()) {
                      found = rs.getInt(1);
                }
        } catch (SQLException e) {
                WebPortal.logger.log(Level.WARNING, "{0} Unable to get mail for player {1}", new Object[]{plugin.logPrefix, player});
                WebPortal.logger.warning(e.getMessage());
        } finally {
                closeResources(conn, st, rs);
        }
        return auctionMails;
    }
    
    @Override
    public void LogSellPrice(Integer name, Short damage, Integer time, String buyer, String seller, Integer quantity, Double price, String ench) {
        WALConnection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
                st = conn.prepareStatement("INSERT INTO WA_SellPrice (name, damage, time, buyer, seller, quantity, price, ench) VALUES (?,?,?,?,?,?,?,?)");
                st.setInt(1, name);
                st.setInt(2, damage);
                st.setInt(3, time);
                st.setString(4, buyer);
                st.setString(5, seller);
                st.setInt(6, quantity);
                st.setDouble(7, price);
                st.setString(8, ench);
                st.executeUpdate();
        } catch (SQLException e) {
                WebPortal.logger.log(Level.WARNING, "{0} Unable to update Sell Price", plugin.logPrefix);
                WebPortal.logger.warning(e.getMessage());
        } finally {
                closeResources(conn, st, rs);
        }
    }

    @Override
    public List<Transact> GetTransactOfPlayer(String player) {
        List<Transact> Transacts = new ArrayList<>();
        WALConnection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
                st = conn.prepareStatement("SELECT name,damage,time,quantity,price,seller,buyer,ench FROM WA_SellPrice where seller = ? or buyer = ?");
                st.setString(1, player);
                st.setString(2, player);
                rs = st.executeQuery();
                while (rs.next()) {
                        Transact _Transact = new Transact();
                        _Transact.setBuyer(rs.getString("buyer"));
                        _Transact.setSeller(rs.getString("seller"));
                        WebItemStack stack = new WebItemStack(rs.getInt("name"), rs.getInt("quantity"), rs.getShort("damage"));
                        stack = EnchantItem(rs.getString("ench"),stack);
                        _Transact.setItemStack(stack);
                        _Transact.setPrice(rs.getDouble("price"));
                        _Transact.setQuantity(rs.getInt("quantity"));
                        Transacts.add(_Transact);
                }
        } catch (SQLException e) {
                WebPortal.logger.log(Level.WARNING, "{0} Unable to transact ", plugin.logPrefix);
                WebPortal.logger.warning(e.getMessage());
        } finally {
                closeResources(conn, st, rs);
        }
        return Transacts;
    }

    @Override
    public Shop getItemById(int ID, int tableid) {
        Shop auction = null;
        WALConnection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
                String sql = "SELECT id,name,damage,player,quantity,ench,price FROM WA_Auctions WHERE id = ? AND tableid = ?";
                st = conn.prepareStatement(sql);
                st.setInt(1, ID);
                st.setInt(2, tableid);
                rs = st.executeQuery();
                while (rs.next()) {
                        auction = new Shop();
                        auction.setId(rs.getInt("id"));
                        auction.setName(rs.getInt("name"));
                        auction.setDamage(rs.getInt("damage"));
                        auction.setPlayerName(rs.getString("player"));
                        auction.setPrice(rs.getDouble("price"));
                        WebItemStack stack = new WebItemStack(rs.getInt("name"), rs.getInt("quantity"), rs.getShort("damage"));
                        stack = EnchantItem(rs.getString("ench"),stack);
                        auction.setItemStack(stack);
                        auction.setQuantity(rs.getInt("quantity"));
                        auction.setEnchantments(rs.getString("ench"));
                }
        } catch (SQLException e) {
                WebPortal.logger.log(Level.WARNING, "{0} Unable to get items ", plugin.logPrefix);
                WebPortal.logger.warning(e.getMessage());
        } finally {
                closeResources(conn, st, rs);
        }
        return auction;
    }

    @Override
    public List<Shop> getItem(String player, int itemID, int damage, boolean reverseOrder, int tableid) {
        List<Shop> auctions = new ArrayList<>();
        WALConnection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
                String sql = "SELECT id,name,damage,player,quantity,ench FROM WA_Auctions WHERE player = ? AND name = ? AND damage = ? AND tableid = ?";
                if (reverseOrder) {
                        sql += " ORDER BY id DESC";
                }
                st = conn.prepareStatement(sql);
                st.setString(1, player);
                st.setInt(2, itemID);
                st.setInt(3, damage);
                st.setInt(4, tableid);
                rs = st.executeQuery();
                while (rs.next()) {
                        Shop auction = new Shop();
                        auction.setId(rs.getInt("id"));
                        auction.setName(rs.getInt("name"));
                        auction.setDamage(rs.getInt("damage"));
                        WebItemStack stack = new WebItemStack(rs.getInt("name"), rs.getInt("quantity"), rs.getShort("damage"));
                        stack = EnchantItem(rs.getString("ench"),stack);
                        auction.setItemStack(stack);
                        auction.setPlayerName(rs.getString("player"));
                        auction.setQuantity(rs.getInt("quantity"));
                        auction.setEnchantments(rs.getString("ench"));
                        auctions.add(auction);
                }
        } catch (SQLException e) {
                WebPortal.logger.log(Level.WARNING, "{0} Unable to get items ", plugin.logPrefix);
                WebPortal.logger.warning(e.getMessage());
        } finally {
                closeResources(conn, st, rs);
        }
        return auctions;
    }

    @Override
    public List<Shop> getItemByName(String player, boolean reverseOrder, int tableid) {
        List<Shop> auctions = new ArrayList<>();
        WALConnection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
                String sql = "SELECT id,name,damage,player,quantity,price,itemname,ench FROM WA_Auctions WHERE player = ? AND tableid = ?";
                if (reverseOrder) {
                        sql += " ORDER BY id DESC";
                }
                st = conn.prepareStatement(sql);
                st.setString(1, player);
                st.setInt(3, tableid);
                rs = st.executeQuery();
                while (rs.next()) {
                        Shop auction = new Shop();
                        auction.setId(rs.getInt("id"));
                        auction.setName(rs.getInt("name"));
                        auction.setDamage(rs.getInt("damage"));
                        auction.setPlayerName(rs.getString("player"));
                        auction.setQuantity(rs.getInt("quantity"));
                        WebItemStack stack = new WebItemStack(rs.getInt("name"), rs.getInt("quantity"), rs.getShort("damage"));
                        stack = EnchantItem(rs.getString("ench"),stack);
                        auction.setItemStack(stack);
                        auction.setPrice(rs.getDouble("price"));
                        auction.setEnchantments(rs.getString("ench"));
                        auctions.add(auction);
                }
        } catch (SQLException e) {
                WebPortal.logger.log(Level.WARNING, "{0} Unable to get items ", plugin.logPrefix);
                WebPortal.logger.warning(e.getMessage());
        } finally {
                closeResources(conn, st, rs);
        }
        return auctions;
    }

    @Override
    public void updateItemQuantity(int quantity, int id) {
        WALConnection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
                st = conn.prepareStatement("UPDATE WA_Auctions SET quantity = ? WHERE id = ?");
                st.setInt(1, quantity);
                st.setInt(2, id);
                st.executeUpdate();
        } catch (SQLException e) {
                WebPortal.logger.log(Level.WARNING, "{0} Unable to update item quantity in DB", plugin.logPrefix);
                WebPortal.logger.warning(e.getMessage());
        } finally {
                closeResources(conn, st, rs);
        }
    }

    @Override
    public void updateTable(int id, int tableid) {
        WALConnection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
                st = conn.prepareStatement("UPDATE WA_Auctions SET tableid = ? WHERE id = ?");
                st.setInt(1, tableid);
                st.setInt(2, id);
                st.executeUpdate();
        } catch (SQLException e) {
                WebPortal.logger.log(Level.WARNING, "{0} Unable to update item quantity in DB", plugin.logPrefix);
                WebPortal.logger.warning(e.getMessage());
        } finally {
                closeResources(conn, st, rs);
        }
    }

    @Override
    public int createItem(int itemID, int itemDamage, String player, int quantity, Double price, String ench, int tableId, String type, String searchtype) {
        int id = 0;
        WALConnection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
                st = conn.prepareStatement("INSERT INTO WA_Auctions (name, damage, player, quantity, price, ench, tableid, type, searchtype) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                st.setInt(1, itemID);
                st.setInt(2, itemDamage);
                st.setString(3, player);
                st.setInt(4, quantity);
                st.setDouble(5, price);
                st.setString(6, ench);
                st.setInt(7, tableId);
                st.setString(8, type);
                st.setString(9, searchtype);
                st.executeUpdate();
                rs = st.getGeneratedKeys();
                if (rs.next()){
                    id = rs.getInt(1);
                }
        } catch (SQLException e) {
                WebPortal.logger.log(Level.WARNING, "{0} Unable to create item", plugin.logPrefix);
                WebPortal.logger.warning(e.getMessage());
        } finally {
                closeResources(conn, st, rs);
        }
        return id;
    }

    @Override
    public int GetMarketPriceofItem(int itemID, int itemDamage) {
         int MarketPrice = 0;
         WALConnection conn = getConnection();
         PreparedStatement st = null;
         ResultSet rs = null;

         try {
                 st = conn.prepareStatement("SELECT SUM(price)/COUNT(id) as total FROM WA_SellPrice where name = ? and damage = ?");
                 st.setInt(1, itemID);
                 st.setInt(2, itemDamage);
                 rs = st.executeQuery();
                 while (rs.next()) {
                         MarketPrice = rs.getInt("total");
                 }
         } catch (SQLException e) {
                 WebPortal.logger.log(Level.WARNING, "{0} Unable to maket price ", plugin.logPrefix);
                 WebPortal.logger.warning(e.getMessage());
         } finally {
                 closeResources(conn, st, rs);
         }
         return MarketPrice;
    }

    @Override
    public WebItemStack EnchantItem(String ench, WebItemStack stack) {
        if(ench.equals("")) return stack;
        String[] enchs = ench.split(":");

        for (String enchantString:enchs) {
            if(enchantString.equals("")) continue;
            String[] number_level = enchantString.split(",");
            Enchantment enchant = Enchantment.getById(Integer.parseInt(number_level[0]));
            int level = Integer.parseInt(number_level[1]);

            if(stack.getType() == Material.ENCHANTED_BOOK) {
                EnchantmentStorageMeta bookmeta = (EnchantmentStorageMeta)stack.getItemMeta();
                bookmeta.addStoredEnchant(enchant, level, true);
                stack.setItemMeta(bookmeta);
            }else{
                stack.addEnchantment(enchant,level);
            }
        }
        return stack;
    }

    @Override
    public ProfileMcMMO getMcMMOProfileMySql(String tableprefix, String player) {
        throw new UnsupportedOperationException("Implement On Children.");
    }

    @Override
    public int InsertItemInfo(int auctionId,String type, String value) {
        int id = 0;
        WALConnection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
                st = conn.prepareStatement("INSERT INTO WA_ItemExtraInfo (auctionId,type,value) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                st.setInt(1, auctionId);
                st.setString(2, type);
                st.setString(3, value);
                st.executeUpdate();
                rs = st.getGeneratedKeys();
                if (rs.next()){
                    id = rs.getInt(1);
                }
        } catch (SQLException e) {
                WebPortal.logger.log(Level.WARNING, "{0} Unable to insert item info", plugin.logPrefix);
                WebPortal.logger.warning(e.getMessage());
        } finally {
                closeResources(conn, st, rs);
        }
        return id;
    }

    @Override
    public String GetItemInfo(int auctionId,String type) {
         String info = "";
         WALConnection conn = getConnection();
         PreparedStatement st = null;
         ResultSet rs = null;

         try {
                 st = conn.prepareStatement("SELECT value FROM WA_ItemExtraInfo where auctionId = ? and type = ?");
                 st.setInt(1, auctionId);
                 st.setString(2, type);
                 rs = st.executeQuery();
                 while (rs.next()) {
                         info = rs.getString("value");
                 }
         } catch (SQLException e) {
                 WebPortal.logger.log(Level.WARNING, "{0} Unable to get item info ", plugin.logPrefix);
                 WebPortal.logger.warning(e.getMessage());
         } finally {
                 closeResources(conn, st, rs);
         }
         return info;
    }
    
    @Override
    public void DeleteInfo(int auctionId) {
        WALConnection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
                st = conn.prepareStatement("DELETE FROM WA_ItemExtraInfo WHERE auctionId = ?");
                st.setInt(1, auctionId);
                st.executeUpdate();
        } catch (SQLException e) {
                WebPortal.logger.log(Level.WARNING, "{0} Unable to delete Auction: {1}", new Object[]{plugin.logPrefix, auctionId});
        } finally {
                closeResources(conn, st, rs);
        }
    }

    @Override
    public boolean WebSiteBan(String player,String option) {
        WALConnection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
                st = conn.prepareStatement("UPDATE WA_Players SET WA_Players.webban = ? WHERE id = ?");
                st.setString(1, option);
                st.setString(2, player);
                st.executeUpdate();
        } catch (SQLException e) {
                WebPortal.logger.log(Level.WARNING, "{0} Unable setWebBan", plugin.logPrefix);
                WebPortal.logger.warning(e.getMessage());
        } finally {
                closeResources(conn, st, rs);
        }
        return true;
    }

        
    @Override
    public List<Shop> GetBuyList(String player, int from, int qtd) {
        List<Shop> auctions = new ArrayList<>();

        WALConnection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
                st = conn.prepareStatement("SELECT id,name,quantity,damage,player,ench,price FROM WA_Auctions WHERE player = ? and tableid = ? LIMIT ? , ?");
                st.setString(1, player);
                st.setInt(2, plugin.Buy);
                st.setInt(3, from);
                st.setInt(4, qtd);
                rs = st.executeQuery();
                while (rs.next()) {
                        Shop auction = new Shop();
                        auction.setId(rs.getInt("id"));
                        WebItemStack stack = new WebItemStack(rs.getInt("name"), rs.getInt("quantity"), rs.getShort("damage"));
                        stack = EnchantItem(rs.getString("ench"),stack);
                        auction.setItemStack(stack);
                        auction.setPlayerName(rs.getString("player"));
                        auction.setPrice(rs.getDouble("price"));
                        auctions.add(auction);
                }
                st = conn.prepareStatement("SELECT COUNT(*) FROM WA_Auctions where player = ? and tableid = ?");
                st.setString(1, player);
                st.setInt(2, plugin.Buy);
                rs = st.executeQuery();
                while (rs.next()) {
                      found = rs.getInt(1);
                }
        } catch (SQLException e) {
                WebPortal.logger.log(Level.WARNING, "{0} Unable to get mail for player {1}", new Object[]{plugin.logPrefix, player});
                WebPortal.logger.warning(e.getMessage());
        } finally {
                closeResources(conn, st, rs);
        }
        return auctions;
    }
}
