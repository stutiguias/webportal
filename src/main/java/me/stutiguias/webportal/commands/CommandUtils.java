package me.stutiguias.webportal.commands;

import me.stutiguias.webportal.init.WebPortal;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CommandUtils {
    /**
     * Executa um comando no servidor e retorna a saída como uma lista de strings.
     *
     * @param sender O CommandSender que está executando o comando (pode ser um jogador ou o console).
     * @param command A linha de comando a ser executada.
     * @return Uma lista contendo as linhas da saída do comando.
     */
    public static CompletableFuture<List<String>> executeCommandAndGetResult(WebPortal instance,CommandSender sender, String command) {
        CompletableFuture<List<String>> future = new CompletableFuture<>();

        // Agendar execução do comando no thread principal
        Bukkit.getScheduler().runTask(instance, () -> {
            CapturingCommandSender capturingSender = new CapturingCommandSender(sender);
            Bukkit.dispatchCommand(capturingSender, command);
            future.complete(capturingSender.getMessages());
        });

        return future;
    }


}
