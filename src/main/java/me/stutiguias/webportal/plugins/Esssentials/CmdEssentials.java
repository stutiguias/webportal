package me.stutiguias.webportal.plugins.Esssentials;

import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.init.json.JSONArray;
import me.stutiguias.webportal.init.json.JSONObject;
import me.stutiguias.webportal.webserver.HttpResponse;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class CmdEssentials extends HttpResponse {

    public CmdEssentials(WebPortal plugin) {
        super(plugin);
    }

    public void Whois(String sender,Map param) {
        try {
            String nickname = (String) param.get("nickname");

            plugin.executeCommandAndGetResultAsync(Bukkit.getConsoleSender(),"whois " + nickname).thenAccept(messages -> {
                SendResult(messages, sender, "Command : Whois ", nickname);
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
                SendResult(messages, webSitePlayerName, "Command : Mail send ", nickname);
            });
        } catch (Exception e) {
            Print(e.getMessage(), "text/plain");
            e.printStackTrace();
        }
    }

    private void SendResult(List<String> messages, String webSitePlayerName, String x, String nickname) {
        JSONObject json = new JSONObject();
        String cleanedResult = messages.stream()
                .map(this::removeColorCodes)
                .collect(Collectors.joining("\n"));
        json.put("result", cleanedResult);

        JSONArray jsonarray = new JSONArray();
        jsonarray.add(json);

        plugin.getLogger().info("Sender by : " + webSitePlayerName);
        plugin.getLogger().info(x + nickname);
        plugin.getLogger().info("Result : " + cleanedResult);

        sendJsonResponse(jsonarray.toJSONString());
    }

    public String removeColorCodes(String textWithCodes) {
        return textWithCodes.replaceAll("§[0-9a-fk-or]", "");
    }
}
