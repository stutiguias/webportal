/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.init;

import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;

/**
 *
 * @author Daniel
 */
public class Messages {
    
    private final ConfigAccessor message;
    
    public Messages(WebPortal instance,String language) throws IOException {
        
        message = new ConfigAccessor(instance,language + ".yml");
        message.setupConfig();
        FileConfiguration c = message.getConfig();
 
        SignStackStored         =  c.getString("Sign.StackStored");
        SignHoldHelp            =  c.getString("Sign.HoldHelp");
        SignInventoryFull       =  c.getString("Sign.InventoryFull");
        SignInventoryFullNot    =  c.getString("Sign.InventoryFullNot");
        SignMailRetrieved       =  c.getString("Sign.MailRetrieved");
        SignNoMailRetrieved     =  c.getString("Sign.NoMailRetrieved");
        SignNoPermission        =  c.getString("Sign.NoPermission");
                
        WebBuy                  =  c.getString("Web.Buy");
        WebSell                 =  c.getString("Web.Sell");
        WebCancel               =  c.getString("Web.Cancel");
        WebMailit               =  c.getString("Web.Mailit");
        WebCreateSell           =  c.getString("Web.CreateSell");
        WebFailQtdGreaterThen   =  c.getString("Web.Fail.QtdGreaterThen");
        WebFailPurchaseMoreThen =  c.getString("Web.Fail.PurchaseMoreThen");
        WebFailSaleMoreThen     =  c.getString("Web.Fail.SaleMoreThen");
        WebFailBuyMoney         =  c.getString("Web.Fail.BuyMoney");
        WebFailSaleMoney        =  c.getString("Web.Fail.SaleMoney");
        WebFailSellMore         =  c.getString("Web.Fail.SellMore");
        WebFailBuyYours         =  c.getString("Web.Fail.BuyYours");
        WebFailSellYours        =  c.getString("Web.Fail.SellYours");
        WebFailDontHave         =  c.getString("Web.Fail.DontHave");
        WebNoShop               =  c.getString("Web.NoShop");
        WebNever                =  c.getString("Web.Never");
        WebCancelDone           =  c.getString("Web.CancelDone");
        WebInvalidNumber        =  c.getString("Web.InvalidNumber");
        WebSucessCreateSale     =  c.getString("Web.SucessCreateSale");
        WebNoItem               =  c.getString("Web.NoItem");
        WebItemName             =  c.getString("Web.ItemName");
        WebQuantity             =  c.getString("Web.Quantity");
        WebImage                =  c.getString("Web.Image");
        WebItemCategory         =  c.getString("Web.ItemCategory");
        WebNotEnought           =  c.getString("Web.NotEnought");
        WebMailSend             =  c.getString("Web.MailSend");
        WebIdNotFound           =  c.getString("Web.IdNotFound");
        WebPrice                =  c.getString("Web.Price");
        WebNotAdmin             =  c.getString("Web.NotAdmin");
        WebDelete               =  c.getString("Web.Delete");
        WebDeleted              =  c.getString("Web.Deleted");
        WebPlayerBanned         =  c.getString("Web.PlayerBanned");
        WebPlayerNotBanned      =  c.getString("Web.PlayerNotBanned");
        WebPlayerDesBanned      =  c.getString("Web.PlayerDesBanned");
        WebPlayerNotDesBanned   =  c.getString("Web.PlayerNotDesBanned");
        WebPlayerNotFound       =  c.getString("Web.PlayerNotFound");
        WebName                 =  c.getString("Web.Name");
        WebCanBuy               =  c.getString("Web.CanBuy");
        WebCanSell              =  c.getString("Web.CanSell");
        WebisAdmin              =  c.getString("Web.isAdmin");
        WebBanned               =  c.getString("Web.Banned");
        WebWebSiteBan           =  c.getString("Web.WebSiteBan");
        WebBuyer                =  c.getString("Web.Buyer");
        WebSeller               =  c.getString("Web.Seller");
        WebYouPurchase          =  c.getString("Web.YouPurchase");
        WebYouSell              =  c.getString("Web.YouSell");
        WebSucessCreateBuy      =  c.getString("Web.SucessCreateBuy");
        WebCantSell             =  c.getString("Web.CantSell");
        WebCantBuy              =  c.getString("Web.CantBuy");
        WebType                 =  c.getString("Web.Type");
        WebOwner                =  c.getString("Web.Owner");
        WebExpire               =  c.getString("Web.Expire");
        WebPriceEach            =  c.getString("Web.PriceEach");
        WebEnchant              =  c.getString("Web.Enchant");
        WebDurability           =  c.getString("Web.Durability");
        WebMarketPrice          =  c.getString("Web.MarketPrice");
        WebInfinit              =  c.getString("Web.Infinit");
        WebMarketPriceE         =  c.getString("Web.MarketPriceE");
        WebMarketPriceT         =  c.getString("Web.MarketPriceT");
    }
    
    public String SignStackStored;
    public String SignHoldHelp;
    public String SignInventoryFull;
    public String SignInventoryFullNot;
    public String SignMailRetrieved;
    public String SignNoMailRetrieved;
    public String SignNoPermission;
    public String WebBuy;
    public String WebSell;
    public String WebCancel;
    public String WebMailit;
    public String WebCreateSell;
    public String WebFailQtdGreaterThen;
    public String WebFailPurchaseMoreThen;
    public String WebFailSaleMoreThen;
    public String WebFailBuyMoney;
    public String WebFailSaleMoney;
    public String WebFailBuyYours;
    public String WebFailSellYours;
    public String WebFailSellMore;
    public String WebNoShop;
    public String WebNever;
    public String WebCancelDone;
    public String WebInvalidNumber;
    public String WebSucessCreateSale;
    public String WebNoItem;
    public String WebItemName;
    public String WebQuantity;
    public String WebImage;
    public String WebItemCategory;
    public String WebNotEnought;
    public String WebMailSend;
    public String WebIdNotFound;
    public String WebPrice;
    public String WebNotAdmin;
    public String WebDelete;
    public String WebDeleted;
    public String WebPlayerBanned;
    public String WebPlayerNotBanned;
    public String WebPlayerDesBanned;
    public String WebPlayerNotDesBanned;
    public String WebPlayerNotFound;
    public String WebName;
    public String WebCanBuy;
    public String WebCanSell;
    public String WebisAdmin;
    public String WebBanned;
    public String WebWebSiteBan;
    public String WebBuyer;
    public String WebSeller;
    public String WebYouPurchase;
    public String WebFailDontHave;
    public String WebYouSell;
    public String WebSucessCreateBuy;
    public String WebCantSell;
    public String WebCantBuy;
    public String WebType;
    public String WebOwner;
    public String WebExpire;
    public String WebPriceEach;
    public String WebEnchant;
    public String WebDurability;
    public String WebMarketPrice;
    public String WebInfinit;
    public String WebMarketPriceE;
    public String WebMarketPriceT;
}
