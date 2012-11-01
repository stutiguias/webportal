package me.stutiguias.webportal.init;

import me.stutiguias.webportal.commands.WebAuctionCommands;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.stutiguias.webportal.dao.IDataQueries;
import me.stutiguias.webportal.dao.MySQLDataQueries;
import me.stutiguias.webportal.dao.SqliteDataQueries;
import me.stutiguias.webportal.listeners.WebAuctionBlockListener;
import me.stutiguias.webportal.listeners.WebAuctionPlayerListener;
import me.stutiguias.webportal.metrics.Metrics;
import me.stutiguias.webportal.plugins.Essentials;
import me.stutiguias.webportal.plugins.McMMO;
import me.stutiguias.webportal.settings.AuthPlayer;
import me.stutiguias.webportal.tasks.SaleAlertTask;
import me.stutiguias.webportal.tasks.WebAuctionServerListenTask;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class WebAuction extends JavaPlugin {

	public String logPrefix = ChatColor.GOLD + "[WebPortal] " + ChatColor.WHITE;
        public String PluginDir = "plugins" + File.separator + "WebPortal";
	public static final Logger log = Logger.getLogger("Minecraft");

	private final WebAuctionPlayerListener playerListener = new WebAuctionPlayerListener(this);
	private final WebAuctionBlockListener blockListener = new WebAuctionBlockListener(this);

	public IDataQueries dataQueries;

	public Map<String, Long> lastSignUse = new HashMap<String, Long>();
        public static final HashMap<String, AuthPlayer> AuthPlayer = new HashMap<String, AuthPlayer>();
        public static final HashMap<String, Boolean> LockTransact = new HashMap<String, Boolean>();
        public WebAuctionServerListenTask server;
	public int signDelay = 0;
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
        
	public long getCurrentMilli() {
		return System.currentTimeMillis();
	}

	@Override
        @SuppressWarnings("LoggerStringConcat")
	public void onEnable() {

		log.log(Level.INFO,logPrefix + "WebAuction is initializing.");
                
                File dir = new File(this.PluginDir);
                if (!dir.exists()) dir.mkdirs();

                dir = new File(this.PluginDir + File.separator + "html");
                if (!dir.exists()) {
                    log.log(Level.INFO,logPrefix + "Copying default HTML ZIP...");
                    dir = new File(this.PluginDir + File.separator + "webportal.zip");
                    FileMgmt.copy(getResource("webportal.zip"), dir);
                    log.log(Level.INFO,logPrefix + "Done! Unzipping...");
                    FileMgmt.unziptodir(dir, new File(this.PluginDir));
                    log.log(Level.INFO,logPrefix + "Done! Deleting zip.");
                    dir.deleteOnExit();
                }
                
                onLoadConfig();
                
		getCommand("wa").setExecutor(new WebAuctionCommands(this));

		setupEconomy();
		setupPermissions();
                
                if(this.permission.isEnabled() == true)
                {
                   log.log(Level.INFO,logPrefix + "Vault perm enable.");    
                }else{
                   log.log(Level.INFO,logPrefix + "Vault NOT ENABLE.");    
                }
		

                //Metrics 
                try {
                    log.info(logPrefix + "Sending Metrics for help the dev... http://metrics.griefcraft.com :-)");
                    Metrics metrics = new Metrics(this);
                    metrics.start();
                } catch (IOException e) {
                    log.info(logPrefix + "Failed to submit the stats :-(");
                }
	}

        public void onReload() {
            try {
                server.server.close();
            }catch(Exception ex) {
                WebAuction.log.warning(logPrefix + " Error try stop server bind");
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
		log.log(Level.INFO, logPrefix + " Disabled. Bye :D");
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
                if(UseEssentialsBox)
                   essentials = new Essentials(this);
                
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
                
		long saleAlertFrequency = getConfig().getLong("Updates.SaleAlertFrequency");
		boolean getMessages = getConfig().getBoolean("Misc.ReportSales");
		showSalesOnJoin = getConfig().getBoolean("Misc.ShowSalesOnJoin");
                allowlogifonline = getConfig().getBoolean("Misc.AllowLogOnlyIfOnline");
		signDelay = getConfig().getInt("Misc.SignDelay");
                
                port = getConfig().getInt("Misc.WebServicePort");
                int NUM_CONN_MAX = getConfig().getInt("Misc.MaxSimultaneousConnection");
                log.log(Level.INFO, logPrefix + " Number max Simultaneous Connection is " + NUM_CONN_MAX);
                connections = 0;
                
                if(getConfig().getBoolean("Misc.UseInsideServer")) {
                    server = new WebAuctionServerListenTask(this,NUM_CONN_MAX);
                    server.start();
                }
                // so load the materials.yml
                materials = new ConfigAccessor(this, "materials.yml");
                try {
                    materials.setupConfig();
                    materials.getConfig();
                }catch(IOException ex) {
                    log.warning("unable to setup materials.yml");
                }
                
                
                String dbtype = getConfig().getString("DataBase.Type");
                // Set up IDataQueries
                if(!dbPass.equals("password123") && !dbtype.equalsIgnoreCase("SQLite") )
                {
                    log.log(Level.INFO, logPrefix + "Choose MySQL db type.");
                    log.log(Level.INFO, logPrefix + "MySQL Initializing.");

                    dataQueries = new MySQLDataQueries(this, dbHost, dbPort, dbUser, dbPass, dbDatabase);
                    dataQueries.initTables();
               }else{ 
                    log.log(Level.INFO, logPrefix + "Choose SQLite db type.");
                    log.log(Level.INFO, logPrefix + "SQLite Initializing.");
                    
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
                if((Boolean)mcmmoconfig.get("UseMcMMO")) mcmmo = new McMMO(this);
            }catch(NullPointerException ex){
                log.info(logPrefix + " McmmoBox Disable");
            }
            
        }
        
        public String getSearchType(String Itemname) {
            // it a block 
            try {
                for (Iterator<String> it = materials.getConfig().getConfigurationSection("Block").getKeys(false).iterator(); it.hasNext();) {
                    String key = it.next();
                    if(key.equalsIgnoreCase(Itemname)) return "Block";
                }
            }catch(NullPointerException ex){
                log.warning("Unable to search by item type Block");
                ex.getMessage();
            }
            
            try {
                for (Iterator<String> it = materials.getConfig().getConfigurationSection("Materials").getKeys(false).iterator(); it.hasNext();) {
                    String key = it.next();
                    if(key.equalsIgnoreCase(Itemname)) return "Materials";
                }
            }catch(NullPointerException ex){
                log.warning("Unable to search by item type Materials");
                ex.getMessage();
            }
            
            try {
                for (Iterator<String> it = materials.getConfig().getConfigurationSection("Micellaneous").getKeys(false).iterator(); it.hasNext();) {
                    String key = it.next();
                    if(key.equalsIgnoreCase(Itemname)) return "Micellaneous";
                }
            }catch(NullPointerException ex){
                log.warning("Unable to search by item type Micellaneous");
                ex.getMessage();
            }
                        
            try {
                for (Iterator<String> it = materials.getConfig().getConfigurationSection("Redstone").getKeys(false).iterator(); it.hasNext();) {
                    String key = it.next();
                    if(key.equalsIgnoreCase(Itemname)) return "Redstone";
                }
            }catch(NullPointerException ex){
                log.warning("Unable to search by item type Redstone");
                ex.getMessage();
            }
                                    
            try {
                for (Iterator<String> it = materials.getConfig().getConfigurationSection("Transportation").getKeys(false).iterator(); it.hasNext();) {
                    String key = it.next();
                    if(key.equalsIgnoreCase(Itemname)) return "Transportation";
                }
            }catch(NullPointerException ex){
                log.warning("Unable to search by item type Transportation");
                ex.getMessage();
            }
            
            try {
                for (Iterator<String> it = materials.getConfig().getConfigurationSection("Decoration").getKeys(false).iterator(); it.hasNext();) {
                    String key = it.next();
                    if(key.equalsIgnoreCase(Itemname)) return "Decoration";
                }
            }catch(NullPointerException ex){
                log.warning("Unable to search by item type Decoration");
                ex.getMessage();
            }
            
            try {
                for (Iterator<String> it = materials.getConfig().getConfigurationSection("Tools").getKeys(false).iterator(); it.hasNext();) {
                    String key = it.next();
                    if(key.equalsIgnoreCase(Itemname)) return "Tools";
                }
            }catch(NullPointerException ex){
                log.warning("Unable to search by item type Tools");
                ex.getMessage();
            }
                        
            try {
                for (Iterator<String> it = materials.getConfig().getConfigurationSection("Combat").getKeys(false).iterator(); it.hasNext();) {
                    String key = it.next();
                    if(key.equalsIgnoreCase(Itemname)) return "Combat";
                }
            }catch(NullPointerException ex){
                log.warning("Unable to search by item type Combat");
                ex.getMessage();
            }
            
            try {
                for (Iterator<String> it = materials.getConfig().getConfigurationSection("Food").getKeys(false).iterator(); it.hasNext();) {
                    String key = it.next();
                    if(key.equalsIgnoreCase(Itemname)) return "Food";
                }
            }catch(NullPointerException ex){
                log.warning("Unable to search by item type Food");
                ex.getMessage();
            }
            
            
            return "nothing";
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
