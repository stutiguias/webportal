package me.stutiguias.webportal.plugins.LoginSecurity;

import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.model.WebSitePlayer;
import me.stutiguias.webportal.plugins.Esssentials.ProfileEssentials;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.UUID;
import java.util.logging.Level;

public class LoginSecurity {
    public WebPortal plugin;
    public ProfileLoginSecurity profile;

    public LoginSecurity(WebPortal instance) {
        plugin = instance;

        Plugin pl = plugin.getServer().getPluginManager().getPlugin("LoginSecurity");
        if(pl != null) {
            WebPortal.logger.log(Level.INFO, plugin.logPrefix + " LoginSecurity set to true and LoginSecurity found !!!");
        }
    }

    public boolean Auth(String name, String pass) {
        WebSitePlayer  webSitePlayer = plugin.db.getPlayer(name);
        if(webSitePlayer == null) return false;
        UUID uuid = UUID.fromString(webSitePlayer.getUUID());
        OfflinePlayer player = plugin.getServer().getOfflinePlayer(uuid);
        profile = new ProfileLoginSecurity(player);
        return profile.Validate(pass);
    }

}
