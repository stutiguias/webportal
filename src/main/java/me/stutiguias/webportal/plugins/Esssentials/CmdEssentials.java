package me.stutiguias.webportal.plugins.Esssentials;

import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.webserver.HttpResponse;
import org.bukkit.Bukkit;

import java.util.Map;

@SuppressWarnings("unchecked")
public class CmdEssentials extends HttpResponse {

    public CmdEssentials(WebPortal plugin) {
        super(plugin);
    }

    public void Whois(String sender,Map param) {
        try {
            String nickname = (String) param.get("nickname");

            plugin.executeCommandAndGetResultAsync(Bukkit.getConsoleSender(),"whois " + nickname).thenAccept(messages -> {
                SendResultCMD(messages, sender, "Command : Whois ", nickname);
            });
        } catch (Exception e) {
            Print(e.getMessage(), "text/plain");
            e.printStackTrace();
        }
    }

    public void Mail(String webSitePlayerName, Map param) {
        try {
            String nickname = (String) param.get("nickname");
            String msg = (String) param.get("msg");

            plugin.executeCommandAndGetResultAsync(Bukkit.getConsoleSender(),"mail send " + nickname + " " + msg).thenAccept(messages -> {
                SendResultCMD(messages, webSitePlayerName, "Command : Mail send ", nickname);
            });
        } catch (Exception e) {
            Print(e.getMessage(), "text/plain");
            e.printStackTrace();
        }
    }
}
