package me.stutiguias.webportal.plugins.LoginSecurity;

import com.lenis0012.bukkit.loginsecurity.LoginSecurity;
import com.lenis0012.bukkit.loginsecurity.session.PlayerSession;
import com.lenis0012.bukkit.loginsecurity.storage.PlayerProfile;
import com.lenis0012.bukkit.loginsecurity.hashing.Algorithm;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class ProfileLoginSecurity {

    private final PlayerSession session;
    public ProfileLoginSecurity(OfflinePlayer player) {

        session = LoginSecurity.getSessionManager().getOfflineSession(player.getUniqueId());
    }

    public boolean Validate(String password) {
        final PlayerProfile profile = session.getProfile();
        final Algorithm algorithm = Algorithm.getById(profile.getHashingAlgorithm());
        return algorithm.check(password, profile.getPassword());
    }
}
