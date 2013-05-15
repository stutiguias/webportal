package me.stutiguias.webportal.init;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.stutiguias.webportal.commands.WebPortalCommands;
import me.stutiguias.webportal.dao.IDataQueries;
import me.stutiguias.webportal.dao.MySQLDataQueries;
import me.stutiguias.webportal.dao.SqliteDataQueries;
import me.stutiguias.webportal.listeners.WebAuctionBlockListener;
import me.stutiguias.webportal.listeners.WebAuctionPlayerListener;
import me.stutiguias.webportal.metrics.Metrics;
import me.stutiguias.webportal.plugins.Essentials;
import me.stutiguias.webportal.plugins.McMMO;
import me.stutiguias.webportal.webserver.authentication.AuthPlayer;
import me.stutiguias.webportal.signs.Mailbox;
import me.stutiguias.webportal.signs.vBox;
import me.stutiguias.webportal.signs.wSell;
import me.stutiguias.webportal.signs.wShop;
import me.stutiguias.webportal.tasks.SaleAlertTask;
import me.stutiguias.webportal.tasks.WebPortalHttpServer;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class WebPortal extends JavaPlugin {

	public String logPrefix = "[WebPortal] ";
	public static final Logger logger = Logger.getLogger("Minecraft");

	private final WebAuctionPlayerListener playerListener = new WebAuctionPlayerListener(this);
	private final WebAuctionBlockListener blockListener = new WebAuctionBlockListener(this);

	public IDataQueries dataQueries;

	public Map<String, Long> lastSignUse = new HashMap<>();
        
        public static final HashMap<String, AuthPlayer> AuthPlayers = new HashMap<>();
        public static final HashMap<String, Boolean> LockTransact = new HashMap<>();
        
        public WebPortalHttpServer server;

        public ConfigAccessor materials;
        public ConfigAccessor config;
        public ConfigAccessor web;
        
        public HashMap<String,String> Messages;
        
        // Plugins Settings
        public McMMO mcmmo;
        public Essentials essentials;
        
        public Boolean showSalesOnJoin = false;
        public Boolean allowlogifonline = false;
        public Boolean blockcreative = true;
        public Boolean OnJoinCheckPermission = false;
        public int signDelay;
        
        public String authplugin;
        public String algorithm;
        public String Table;
        public String ColumnPassword;
        public String Username;
        
	public Permission permission = null;
	public Economy economy = null;

        public int Mail = 3;
        public int Auction = 2;
        public int Myitems = 1;
        
        public int port;
        
        public wSell wsell;
        public Mailbox mailbox;
        public vBox vbox;
        public wShop wshop;
        
	public long getCurrentMilli() {
		return System.currentTimeMillis();
	}

	@Override
	public void onEnable() {

		logger.log(Level.INFO, "{0} WebAuction is initializing.", logPrefix);
                
                File dir = getDataFolder();
                if (!dir.exists()) {
                  dir.mkdirs();
                }

                dir = new File(getDataFolder() + File.separator + "html");
                if (!dir.exists()) {
                    logger.log(Level.INFO, "{0} Copying default HTML ZIP...", logPrefix);
                    dir = new File(getDataFolder() + File.separator + "html.zip");
                    FileMgmt.copy(getResource("html.zip"), dir);
                    logger.log(Level.INFO, "{0} Done! Unzipping...", logPrefix);
                    FileMgmt.unziptodir(dir, getDataFolder());
                    logger.log(Level.INFO, "{0} Done! Deleting zip.", logPrefix);
                    dir.deleteOnExit();
                }
                
                onLoadConfig();
                
		getCommand("wa").setExecutor(new WebPortalCommands(this));
                
                wsell   = new wSell(this);
                mailbox = new Mailbox(this);
                vbox    = new vBox(this);
                wshop   = new wShop(this);
                
                // Setup Vault
		setupEconomy();
		setupPermissions();
                
                if(this.permission.isEnabled() == true)
                {
                   logger.log(Level.INFO, "{0} Vault perm enable.", logPrefix);    
                }else{
                   logger.log(Level.INFO, "{0} Vault NOT ENABLE.", logPrefix);    
                }
		

                //Metrics 
                try {
                    logger.log(Level.INFO, "{0} Sending Metrics", logPrefix);
                    Metrics metrics = new Metrics(this);
                    metrics.start();
                } catch (IOException e) {
                    logger.log(Level.INFO, "{0} Failed to submit Metrics", logPrefix);
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
            permission = rsp.getProvider();
            return permission != null;
        }

	private Boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}
		return (economy != null);
	}

        public void onLoadConfig() {
            
            	initConfig();
                WebConfig();
                
                materials = new ConfigAccessor(this, "materials.yml");
                try {
                    materials.setupConfig();
                }catch(IOException ex) {
                    logger.warning("unable to setup materials.yml");
                    onDisable();
                }
                
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
                
                FileConfiguration c = config.getConfig();
                Messages = new HashMap<>();
                Messages.put("StackStored", c.getString("Sign.Message.StackStored"));
                Messages.put("HoldHelp", c.getString("Sign.Message.HoldHelp"));
                Messages.put("InventoryFull", c.getString("Sign.Message.InventoryFull"));
                Messages.put("InventoryFullNot", c.getString("Sign.Message.InventoryFullNot"));
                Messages.put("MailRetrieved", c.getString("Sign.Message.MailRetrieved"));
                Messages.put("NoMailRetrieved",c.getString("Sign.Message.NoMailRetrieved"));
                Messages.put("NoPermission",c.getString("Sign.NoPermission"));
                
                blockcreative =         c.getBoolean("Misc.BlockCreative");
		showSalesOnJoin =       c.getBoolean("Misc.ShowSalesOnJoin");
                allowlogifonline =      c.getBoolean("Misc.AllowLogOnlyIfOnline");
		signDelay =             c.getInt("Misc.SignDelay");
                port =                  c.getInt("Misc.WebServicePort");
                OnJoinCheckPermission=  c.getBoolean("Misc.OnJoinCheckPermission");
                
                long saleAlertFrequency = c.getLong("Updates.SaleAlertFrequency");
		boolean getMessages = c.getBoolean("Misc.ReportSales");
                
                if (getMessages) {
                    getServer().getScheduler().runTaskTimerAsynchronously(this, new SaleAlertTask(this), saleAlertFrequency, saleAlertFrequency);
                }

                if(!c.getString("DataBase.Type").equalsIgnoreCase("SQLite"))
                {
                    logger.log(Level.INFO, "{0} Choose MySQL db type.", logPrefix);
                    logger.log(Level.INFO, "{0} MySQL Initializing.", logPrefix);

                    String dbHost = c.getString("MySQL.Host");
                    String dbUser = c.getString("MySQL.Username");
                    String dbPass = c.getString("MySQL.Password");
                    String dbPort = c.getString("MySQL.Port");
                    String dbDatabase = c.getString("MySQL.Database");
                    
                    dataQueries = new MySQLDataQueries(this, dbHost, dbPort, dbUser, dbPass, dbDatabase);
                    dataQueries.initTables();
               }else{ 
                    logger.log(Level.INFO, "{0} Choose SQLite db type.", logPrefix);
                    logger.log(Level.INFO, "{0} SQLite Initializing.", logPrefix);
                    
                    dataQueries = new SqliteDataQueries(this);
                    dataQueries.initTables();
                }
                
                int NUM_CONN_MAX = c.getInt("Misc.MaxSimultaneousConnection");
                logger.log(Level.INFO, "{0} Max Simultaneous Connection set {1}", new Object[]{logPrefix, NUM_CONN_MAX});
                if(getConfig().getBoolean("Misc.UseInsideServer")) {
                    server = new WebPortalHttpServer(this, NUM_CONN_MAX);
                    server.start();
                }
                
	}
                
        public void WebConfig(){
                
                web = new ConfigAccessor(this,"web.yml");
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
                
                Messages.put("Buy",w.getString("Message.Buy"));
                Messages.put("Cancel",w.getString("Message.Cancel"));
                Messages.put("Mailit",w.getString("Message.Mailit"));
                Messages.put("CreateAuction",w.getString("Message.CreateAuction"));

                if(w.getBoolean("Index.McMMO.Active")) {
                    ConfigurationSection s = w.getConfigurationSection("Index.McMMO");
                    mcmmo = new McMMO(this);
                    mcmmo.Config.put("McMMOMYSql",s.getBoolean("McMMOMYSql") );
                    mcmmo.Config.put("McMMOTablePrefix", s.getString("McMMOTablePrefix"));
                }

                if(w.getBoolean("Index.Essentials.Active")) {
                    essentials = new Essentials(this);
                }
        }
        
        public String getSearchType(String itemId) {

            ConfigurationSection[] Configs =  { 
                        materials.getConfig().getConfigurationSection("Block"),
                        materials.getConfig().getConfigurationSection("Materials"),
                        materials.getConfig().getConfigurationSection("Micellaneous"),
                        materials.getConfig().getConfigurationSection("Redstone"),
                        materials.getConfig().getConfigurationSection("Transportation"),
                        materials.getConfig().getConfigurationSection("Decoration"),
                        materials.getConfig().getConfigurationSection("Tools"),
                        materials.getConfig().getConfigurationSection("Combat"),
                        materials.getConfig().getConfigurationSection("Food"),    
                        materials.getConfig().getConfigurationSection("Brewing"),    
            };

            for (ConfigurationSection configurationSection : Configs) {

                Set<String> keys = configurationSection.getKeys(false);

                for (Iterator<String> it = keys.iterator(); it.hasNext();) {
                    String key = it.next();
                    
                    if (key.equals(itemId)) {
                        return configurationSection.getName();
                    }
                }

            }
            
            logger.warning(String.format("[WebPortal] Unable to search Item id %s , please add on materials.yml and post comment about that with this id",itemId));
            return "Others";
            
        }
                
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

}
