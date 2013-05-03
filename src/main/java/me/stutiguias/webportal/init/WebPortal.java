package me.stutiguias.webportal.init;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
import me.stutiguias.webportal.settings.AuthPlayer;
import me.stutiguias.webportal.signs.Mailbox;
import me.stutiguias.webportal.signs.vBox;
import me.stutiguias.webportal.signs.wSell;
import me.stutiguias.webportal.signs.wShop;
import me.stutiguias.webportal.tasks.SaleAlertTask;
import me.stutiguias.webportal.tasks.WebAuctionServerListenTask;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class WebPortal extends JavaPlugin {

	public String logPrefix = "[WebPortal] ";
	public static final Logger logger = Logger.getLogger("Minecraft");

	private final WebAuctionPlayerListener playerListener = new WebAuctionPlayerListener(this);
	private final WebAuctionBlockListener blockListener = new WebAuctionBlockListener(this);

	public IDataQueries dataQueries;

	public Map<String, Long> lastSignUse = new HashMap<String, Long>();
        public static final HashMap<String, AuthPlayer> AuthPlayers = new HashMap<String, AuthPlayer>();
        public static final HashMap<String, Boolean> LockTransact = new HashMap<String, Boolean>();
        public WebAuctionServerListenTask server;
	public int signDelay;
        
        public ConfigAccessor materials;
        
        public HashMap<String,String> Messages;
        
        // Mcmmo Settings
        public HashMap<String,Object> mcmmoconfig;
        public McMMO mcmmo;
        
        //Essentials settings
        public Essentials essentials;
        public Boolean UseEssentialsBox;
        
        public Boolean showSalesOnJoin = false;
        public Boolean allowlogifonline = false;
        public Boolean blockcreative = true;
        
        public String authplugin;
        public String algorithm;
        public String Table;
        public String ColumnPassword;
        public String Username;
        
	public Permission permission = null;
	public Economy economy = null;
        
        public int connections;
        
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
                
                wsell = new wSell(this);
                mailbox = new Mailbox(this);
                vbox = new vBox(this);
                wshop = new wShop(this);
                
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
                server.server.close();
            }catch(Exception ex) {
                WebPortal.logger.log(Level.WARNING, "{0} Error try stop server bind", logPrefix);
            }
            server.interrupt();
            this.reloadConfig();
            saveConfig();
            getServer().getPluginManager().disablePlugin(this);
            getServer().getPluginManager().enablePlugin(this);
        }
        
	@Override
	public void onDisable() {
		getServer().getScheduler().cancelTasks(this);
		logger.log(Level.INFO, "{0} Disabled. Bye :D", logPrefix);
	}

	private void initConfig() {
                getConfig().addDefault("DataBase.Type", "SQLite");
		getConfig().addDefault("MySQL.Host", "localhost");
		getConfig().addDefault("MySQL.Username", "root");
		getConfig().addDefault("MySQL.Password", "password123");
		getConfig().addDefault("MySQL.Port", "3306");
		getConfig().addDefault("MySQL.Database", "minecraft");
		getConfig().addDefault("Misc.ReportSales", false);
		getConfig().addDefault("Misc.ShowSalesOnJoin", false);
                getConfig().addDefault("Misc.AllowLogOnlyIfOnline", false);
                getConfig().addDefault("Misc.UseInsideServer",true);
                getConfig().addDefault("Misc.WebServicePort",25900);
		getConfig().addDefault("Misc.SignDelay", 1000);
                getConfig().addDefault("Misc.MaxSimultaneousConnection", 200);
                getConfig().addDefault("Misc.BlockCreative",true);
                
                getConfig().addDefault("SignMessage.StackStored", "Item stack stored.");
                getConfig().addDefault("SignMessage.HoldHelp","Please hold a stack of item in your hand and right click to deposit them.");
                getConfig().addDefault("SignMessage.InventoryFull", "Inventory full, Store again not fit itens");
                getConfig().addDefault("SignMessage.InventoryFullNot", "Inventory full, cannot get mail");
                getConfig().addDefault("SignMessage.MailRetrieved","Mail retrieved");
                getConfig().addDefault("SignMessage.NoMailRetrieved", "No mail to retrieve");
                getConfig().addDefault("SignMessage.NoPermission","You do not have permission to use the mailbox");
                getConfig().addDefault("WebMessage.Buy","Buy");
                getConfig().addDefault("WebMessage.Cancel","Cancel");
                getConfig().addDefault("WebMessage.Mailit","Mail it");
                getConfig().addDefault("WebMessage.CreateAuction","Create Auction");
                
                mcmmoconfig = new HashMap<String, Object>();
                mcmmoconfig.put("UseMcMMO", false);
                mcmmoconfig.put("McMMOMYSql", false );
                mcmmoconfig.put("McMMOTablePrefix", "mcmmo_"); 
                getConfig().addDefault("PortalBox.McMMO", mcmmoconfig);
                getConfig().addDefault("PortalBox.Essentials", false);
                
                getConfig().addDefault("AuthSystem.System", "WebPortal");
                getConfig().addDefault("AuthSystem.Algorithm", "MD5");
                getConfig().addDefault("AuthSystem.TableName", "minecraft");
                getConfig().addDefault("AuthSystem.ColumnPassword", "password");
                getConfig().addDefault("AuthSystem.ColumnUsername", "username");
                
		getConfig().addDefault("Updates.SaleAlertFrequency", 30L);
		getConfig().options().copyDefaults(true);
		saveConfig();
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
                
                Messages = new HashMap<String, String>();
                Messages.put("StackStored", getConfig().getString("SignMessage.StackStored"));
                Messages.put("HoldHelp", getConfig().getString("SignMessage.HoldHelp"));
                Messages.put("InventoryFull", getConfig().getString("SignMessage.InventoryFull"));
                Messages.put("InventoryFullNot", getConfig().getString("SignMessage.InventoryFullNot"));
                Messages.put("MailRetrieved", getConfig().getString("SignMessage.MailRetrieved"));
                Messages.put("NoMailRetrieved",getConfig().getString("SignMessage.NoMailRetrieved"));
                Messages.put("NoPermission",getConfig().getString("Sign.NoPermission"));
                Messages.put("Buy",getConfig().getString("WebMessage.Buy"));
                Messages.put("Cancel",getConfig().getString("WebMessage.Cancel"));
                Messages.put("Mailit",getConfig().getString("WebMessage.Mailit"));
                Messages.put("CreateAuction",getConfig().getString("WebMessage.CreateAuction"));
                getMcMMOConfig();

                UseEssentialsBox = getConfig().getBoolean("PortalBox.Essentials");
                if(UseEssentialsBox) {
                    essentials = new Essentials(this);
                }
                
		String dbHost = getConfig().getString("MySQL.Host");
		String dbUser = getConfig().getString("MySQL.Username");
		String dbPass = getConfig().getString("MySQL.Password");
		String dbPort = getConfig().getString("MySQL.Port");
		String dbDatabase = getConfig().getString("MySQL.Database");
                
                authplugin = getConfig().getString("AuthSystem.System");
                algorithm = getConfig().getString("AuthSystem.Algorithm");
                Table = getConfig().getString("AuthSystem.TableName");
                ColumnPassword = getConfig().getString("AuthSystem.ColumnPassword");
                Username = getConfig().getString("AuthSystem.ColumnUsername");
                blockcreative = getConfig().getBoolean("Misc.BlockCreative");
                
		long saleAlertFrequency = getConfig().getLong("Updates.SaleAlertFrequency");
		boolean getMessages = getConfig().getBoolean("Misc.ReportSales");
		showSalesOnJoin = getConfig().getBoolean("Misc.ShowSalesOnJoin");
                allowlogifonline = getConfig().getBoolean("Misc.AllowLogOnlyIfOnline");
		signDelay = getConfig().getInt("Misc.SignDelay");
                
                port = getConfig().getInt("Misc.WebServicePort");
                int NUM_CONN_MAX = getConfig().getInt("Misc.MaxSimultaneousConnection");
                logger.log(Level.INFO, "{0} Max Simultaneous Connection set {1}", new Object[]{logPrefix, NUM_CONN_MAX});
                connections = 0;
                
                if(getConfig().getBoolean("Misc.UseInsideServer")) {
                    server = new WebAuctionServerListenTask(this,NUM_CONN_MAX);
                    server.start();
                }

                materials = new ConfigAccessor(this, "materials.yml");
                
                try {
                    materials.setupConfig();
                    materials.getConfig();
                }catch(IOException ex) {
                    logger.warning("unable to setup materials.yml");
                }
                
                
                String dbtype = getConfig().getString("DataBase.Type");

                if(!dbPass.equals("password123") && !dbtype.equalsIgnoreCase("SQLite") )
                {
                    logger.log(Level.INFO, "{0} Choose MySQL db type.", logPrefix);
                    logger.log(Level.INFO, "{0} MySQL Initializing.", logPrefix);

                    dataQueries = new MySQLDataQueries(this, dbHost, dbPort, dbUser, dbPass, dbDatabase);
                    dataQueries.initTables();
               }else{ 
                    logger.log(Level.INFO, "{0} Choose SQLite db type.", logPrefix);
                    logger.log(Level.INFO, "{0} SQLite Initializing.", logPrefix);
                    
                    dataQueries = new SqliteDataQueries(this);
                    dataQueries.initTables();
                    

                }
   
                if (getMessages) {
                    getServer().getScheduler().scheduleAsyncRepeatingTask(this, new SaleAlertTask(this), saleAlertFrequency, saleAlertFrequency);
                }
                          
                PluginManager pm = getServer().getPluginManager();
                pm.registerEvents(playerListener, this);
                pm.registerEvents(blockListener, this);
        }
        
        public void getMcMMOConfig(){
            try {
                mcmmoconfig = new HashMap<String, Object>();
                for (String key : getConfig().getConfigurationSection("PortalBox.McMMO").getKeys(false)){
                mcmmoconfig.put(key, getConfig().get("PortalBox.McMMO." + key));
                }
                if((Boolean)mcmmoconfig.get("UseMcMMO")) {
                    mcmmo = new McMMO(this);
                }
            }catch(NullPointerException ex){
                logger.log(Level.INFO, "{0} McmmoBox Disable", logPrefix);
            }
            
        }
        
        public String getSearchType(String itemName) {
            try {
                
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
                            materials.getConfig().getConfigurationSection("Potions"),    
                };
                
                String Type = null;
                
                for (ConfigurationSection configurationSection : Configs) {
                    Type = GetType(itemName, configurationSection);
                    if(Type != null) return Type;
                }
                
                if(Type == null) return "nothing";
                
            }catch(NullPointerException ex){
                logger.warning(String.format("Unable to search Item %s",itemName));
                ex.printStackTrace();
            }
            return "nothing";
            
        }
        
        public String GetType(String itemName,ConfigurationSection Section) {
            for (Iterator<String> it = Section.getKeys(false).iterator(); it.hasNext();) {
                String key = it.next();
                if(key.equalsIgnoreCase(itemName)) {
                    return Section.getName();
                }
            }
            return null;
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
