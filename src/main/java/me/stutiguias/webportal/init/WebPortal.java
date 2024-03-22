package me.stutiguias.webportal.init;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.stutiguias.webportal.commands.WebPortalCommands;
import me.stutiguias.webportal.dao.IDataQueries;
import me.stutiguias.webportal.dao.MySQLDataQueries;
import me.stutiguias.webportal.dao.SqliteDataQueries;
import me.stutiguias.webportal.listeners.WebAuctionBlockListener;
import me.stutiguias.webportal.listeners.WebAuctionPlayerListener;
import me.stutiguias.webportal.model.Shop;
import me.stutiguias.webportal.model.WebItemStack;
import me.stutiguias.webportal.plugins.Esssentials.Essentials;
import me.stutiguias.webportal.plugins.LoginSecurity.LoginSecurity;
import me.stutiguias.webportal.plugins.McMMO.McMMO;
import me.stutiguias.webportal.webserver.authentication.LoggedPlayer;
import me.stutiguias.webportal.signs.Mailbox;
import me.stutiguias.webportal.signs.vBox;
import me.stutiguias.webportal.signs.wSell;
import me.stutiguias.webportal.tasks.SaleAlertTask;
import me.stutiguias.webportal.tasks.WebPortalHttpServer;
import me.stutiguias.webportal.trade.Transaction;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class WebPortal extends JavaPlugin {

    public String logPrefix = "[WebPortal] ";
    public static final Logger logger = Logger.getLogger("Minecraft");

    private final WebAuctionPlayerListener playerListener = new WebAuctionPlayerListener(this);
    private final WebAuctionBlockListener blockListener = new WebAuctionBlockListener(this);

    public IDataQueries db;

    public Map<String, Long> lastUse = new HashMap<>();

    public static final HashMap<String, LoggedPlayer> AuthPlayers = new HashMap<>();
    public static final HashMap<String, Boolean> LockTransact = new HashMap<>();

    public WebPortalHttpServer server;

    public static ConfigAccessor materials;
    public static ConfigAccessor config;
    public static ConfigAccessor web;

    public static Messages Messages;

    // Plugins Settings
    public McMMO mcmmo;
    public Essentials essentials;
    public LoginSecurity loginSecutiry;

    public Boolean showSalesOnJoin = false;
    public Boolean allowlogifonline = false;
    public Boolean blockcreative = true;
    public Boolean OnJoinCheckPermission = false;
    public static Boolean AllowMetaItem = false;
    public Integer SessionTime;
    public String Avatarurl;
    public int signDelay;
    public Boolean UpdaterNotify;
    public int mailboxDelay;

    public String allowexternal;
    public Boolean EnableExternalSource;
    public String authplugin;
    public String algorithm;
    public String Table;
    public String ColumnPassword;
    public String Username;
    public String Moneyformat;

    public Permission permission = null;
    public Economy economy = null;

    public int Buy = 4;
    public int Sell = 2;
    public int Mail = 3;
    public int Myitems = 1;

    public int port;

    public wSell wsell;
    public Mailbox mailbox;
    public vBox vbox;

    public Transaction transaction;   

    public static boolean update = false;
    public static String name = "";
    public static String type = "";
    public static String version = "";
    public static String link = "";

    public long getCurrentMilli() {
            return System.currentTimeMillis();
    }

    @Override
    public void onEnable() {

        logger.log(Level.INFO, "{0} WebPortal is initializing.", logPrefix);

        File dir = getDataFolder();
        if (!dir.exists()) {
          dir.mkdirs();
        }

        try {
            new SaveResource(this).extract();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        onLoadConfig();

        getCommand("wa").setExecutor(new WebPortalCommands(this));

        wsell   = new wSell(this);
        mailbox = new Mailbox(this);
        vbox    = new vBox(this);

        transaction = new Transaction(this);

        // Setup Vault
        boolean bEconomy = setupEconomy();
        boolean bPermissions = setupPermissions();

        if(this.permission.isEnabled() && bEconomy && bPermissions)
        {
           logger.log(Level.INFO, "{0} Vault perm enable.", logPrefix);
        }else{
           logger.log(Level.INFO, "{0} Vault NOT ENABLE.", logPrefix);
        }

        try{
            int pluginId = 21331;
            Metrics metrics = new Metrics(this, pluginId);
            logger.log(Level.INFO,"{0} Metrics Enable !", logPrefix);
        }catch (Exception ex){
            logger.log(Level.INFO, "{0} Metrics NOT ENABLE!", logPrefix);
        }

    }

    public void onReload() {
        try {
            server.server.stop(0);
        }catch(Exception ex) {
            WebPortal.logger.log(Level.WARNING, "{0} Error try stop server bind", logPrefix);
        }
        server.interrupt();

        config.reloadConfig();
        materials.reloadConfig();
        web.reloadConfig();

        getServer().getPluginManager().disablePlugin(this);
        getServer().getPluginManager().enablePlugin(this);
    }

    @Override
    public void onDisable() {
            getServer().getScheduler().cancelTasks(this);
            try {
                server.server.stop(0);
            }catch(Exception ex) {
                WebPortal.logger.log(Level.WARNING, "{0} Error try stop server bind", logPrefix);
            }
            server.interrupt();

            logger.log(Level.INFO, "{0} Disabled. Bye :D", logPrefix);
    }



    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        permission = rsp != null ? rsp.getProvider() : null;
        return permission != null;
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        economy = economyProvider != null ? economyProvider.getProvider() : null;
        return economy != null;
    }

    public void onLoadConfig() {

            initConfig();
            WebConfig();
            PluginManager pm = getServer().getPluginManager();
            pm.registerEvents(playerListener, this);
            pm.registerEvents(blockListener, this);
    }

    private void initConfig() {

            config = new ConfigAccessor(this,"config.yml");

            try {
                config.setupConfig();
            }catch(IOException ex) {
                logger.warning("unable to setup config.yml");
                onDisable();
            }

            FileConfiguration c = updateConfigFileVersionIfNeedIt();

            blockcreative =         c.getBoolean("Misc.BlockCreative");
            showSalesOnJoin =       c.getBoolean("Misc.ShowSalesOnJoin");
            allowlogifonline =      c.getBoolean("Misc.AllowLogOnlyIfOnline");
            signDelay =             c.getInt("Misc.SignDelay");
            mailboxDelay =          c.getInt("Misc.MailboxDelay");
            port =                  c.getInt("Misc.WebServicePort");
            OnJoinCheckPermission=  c.getBoolean("Misc.OnJoinCheckPermission");
            AllowMetaItem=          c.getBoolean("Misc.AllowMetaItem");
            allowexternal=          c.getString("Misc.Allow");
            EnableExternalSource=   c.getBoolean("Misc.EnableExternalSource");
            UpdaterNotify=          c.getBoolean("Misc.UpdaterNotify");
            try {
                Messages = new Messages(this,c.getString("Misc.Language"));
            }catch(IOException ex){
                logger.warning("error parse language file yml");
                onDisable();
            }

            SetupSaleAlert(c);
            SetupDatabase(c);
            SetupWebServer(c);
    }

    private void SetupWebServer(FileConfiguration c) {
        int NUM_CONN_MAX = c.getInt("Misc.MaxSimultaneousConnection");
        logger.log(Level.INFO, "{0} Max Simultaneous Connection set {1}", new Object[]{logPrefix, NUM_CONN_MAX});

        server = new WebPortalHttpServer(this, NUM_CONN_MAX);
        server.start();
    }

    private void SetupDatabase(FileConfiguration c) {
        if(!Objects.requireNonNull(c.getString("DataBase.Type")).equalsIgnoreCase("SQLite"))
        {
            logger.log(Level.INFO, "{0} Choose MySQL db type.", logPrefix);
            logger.log(Level.INFO, "{0} MySQL Initializing.", logPrefix);

            String dbHost = c.getString("MySQL.Host");
            String dbUser = c.getString("MySQL.Username");
            String dbPass = c.getString("MySQL.Password");
            String dbPort = c.getString("MySQL.Port");
            String dbDatabase = c.getString("MySQL.Database");

            db = new MySQLDataQueries(this, dbHost, dbPort, dbUser, dbPass, dbDatabase);
            db.initTables();
       }else{
            logger.log(Level.INFO, "{0} Choose SQLite db type.", logPrefix);
            logger.log(Level.INFO, "{0} SQLite Initializing.", logPrefix);

            db = new SqliteDataQueries(this);
            db.initTables();
        }
    }

    private void SetupSaleAlert(FileConfiguration c) {
        long saleAlertFrequency = c.getLong("Updates.SaleAlertFrequency");
        boolean getMessages = c.getBoolean("Misc.ReportSales");

        if (getMessages)
            getServer().getScheduler().runTaskTimerAsynchronously(this, new SaleAlertTask(this), saleAlertFrequency, saleAlertFrequency);
    }

    private FileConfiguration updateConfigFileVersionIfNeedIt() {
        FileConfiguration c = config.getConfig();
        if(!c.isSet("configversion") || c.getInt("configversion") != 3){
            try {
                boolean makeOld = config.MakeOld();
                if(makeOld) config.setupConfig();
                c = config.getConfig();
            }catch(IOException ex) {
                logger.warning("unable to setup config.yml");
                onDisable();
            }
        }
        return c;
    }

    public void WebConfig(){

            web = new ConfigAccessor(this, "web.yml");
            try {
                web.setupConfig();
            }catch(IOException ex) {
                logger.warning("unable to setup web.yml");
                onDisable();
            }

            FileConfiguration w = web.getConfig();

            authplugin =     w.getString("AuthSystem.System");
            algorithm =      w.getString("AuthSystem.Algorithm");
            Table =          w.getString("AuthSystem.TableName");
            ColumnPassword = w.getString("AuthSystem.ColumnPassword");
            Username =       w.getString("AuthSystem.ColumnUsername");
            SessionTime =    w.getInt("Setting.SessionTime");
            Avatarurl=       w.getString("Setting.Avatarurl");
            Moneyformat=     w.getString("Setting.Moneyformat");

            if(w.getBoolean("Index.McMMO.Active")) {
                ConfigurationSection s = w.getConfigurationSection("Index.McMMO");
                mcmmo = new McMMO(this);
                mcmmo.Config.put("McMMOMYSql",s.getBoolean("McMMOMYSql") );
                mcmmo.Config.put("McMMOTablePrefix", s.getString("McMMOTablePrefix"));
            }

            if(w.getBoolean("Index.Essentials.Active")) {
                essentials = new Essentials(this);
            }

            if(authplugin.equalsIgnoreCase("LoginSecurity")) {
                loginSecutiry = new LoginSecurity(this);
            }
    }

// TODO Remover
//    public static String GetSearchType(String itemId) {
//
//        logger.warning(String.format("[WebPortal] Item id %s Not FOUND \n[WebPortal] Add it on materials.yml and post comment on dev site",itemId));
//        return "Others";
//
//    }

    public String parseColor(String message) {
        try {
            for (ChatColor color : ChatColor.values()) {
                message = message.replaceAll(String.format("&%c", color.getChar()), color.toString());
            }
            return message;
        }catch(Exception e) {
            return message;
        }
    }

    public boolean hasPermission(Player player, String Permission) {
        return permission.has(player, Permission.toLowerCase());
    }

    public void Store(ItemStack item, Player player) {
        transaction.Store( item , player);
    }

    public void Store(WebItemStack stack,Player player){
        transaction.Store(stack, player);
    }

    public String Buy(String BuyPlayerName,Shop itemSold,int qtd){
        return transaction.Buy(BuyPlayerName, itemSold, qtd);
    }
    
    public String Sell(String sellerPlayerName,Shop itemBuy,int qtd) {
        return transaction.Sell(sellerPlayerName, itemBuy, qtd);
    }
}
