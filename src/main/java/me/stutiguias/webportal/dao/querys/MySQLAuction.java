/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.dao.querys;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import me.stutiguias.webportal.dao.MySQLDataQueries;
import me.stutiguias.webportal.dao.WALConnection;
import me.stutiguias.webportal.init.WebAuction;
import me.stutiguias.webportal.settings.Auction;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Daniel
 */
public class MySQLAuction extends MySQLDataQueries {
    
    public WebAuction plugin;
            
    public MySQLAuction(WebAuction plugin, String dbHost, String dbPort, String dbUser, String dbPass, String dbName) {
        super(plugin,dbHost,dbPort,dbUser,dbPass,dbName);
    }
    
    @Override
    public Auction getAuction(int id) {
            Auction auction = null;

            WALConnection conn = getConnection();
            PreparedStatement st = null;
            ResultSet rs = null;

            try {
                    st = conn.prepareStatement("SELECT name,quantity,damage,player,price,created,allowBids,currentBid,currentWinner,ench FROM WA_Auctions WHERE id = ?");
                    st.setInt(1, id);
                    rs = st.executeQuery();
                    while (rs.next()) {
                            auction = new Auction();
                            auction.setId(id);
                            ItemStack stack = new ItemStack(rs.getInt("name"), rs.getInt("quantity"), rs.getShort("damage"));
                            stack = Chant(rs.getString("ench"),stack);
                            auction.setItemStack(stack);
                            auction.setPlayerName(rs.getString("player"));
                            auction.setPrice(rs.getDouble("price"));
                            auction.setCreated(rs.getInt("created"));
                            auction.setAllowBids(rs.getBoolean("allowBids"));
                            auction.setCurrentBid(rs.getDouble("currentBid"));
                            auction.setCurrentWinner(rs.getString("currentWinner"));
                            auction.setEnch(rs.getString("ench"));
                    }
            } catch (SQLException e) {
                    WebAuction.log.warning(plugin.logPrefix + "Unable to get auction " + id);
                    WebAuction.log.warning(e.getMessage());
            } finally {
                    closeResources(conn, st, rs);
            }
            return auction;
    }
    
    @Override
    public int getTotalAuctionCount() {
        int totalAuctionCount = 0;

        WALConnection conn = getConnection();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
                st = conn.prepareStatement("SELECT COUNT(*) FROM WA_Auctions");
                rs = st.executeQuery();
                while (rs.next()) {
                        totalAuctionCount = rs.getInt(1);
                }
        } catch (SQLException e) {
                WebAuction.log.warning(plugin.logPrefix + "Unable to get total auction count error : " + e.getMessage());
        } finally {
                closeResources(conn, st, rs);
        }
        return totalAuctionCount;
    }
}
