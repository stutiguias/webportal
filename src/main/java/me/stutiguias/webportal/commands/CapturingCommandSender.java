package me.stutiguias.webportal.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CapturingCommandSender implements CommandSender {
    private final List<String> messages = new ArrayList<>();
    private final CommandSender originalSender;

    public CapturingCommandSender(CommandSender originalSender) {
        this.originalSender = originalSender;
    }

    public List<String> getMessages() {
        return messages;
    }

    @Override
    public void sendMessage(String message) {
        messages.add(message);
    }

    @Override
    public void sendMessage(String[] messages) {
        for (String message : messages) {
            sendMessage(message);
        }
    }

    // Implementação do novo método necessário
    @Override
    public void sendMessage(UUID sender, String message) {
        // Aqui você decide como deseja tratar a identificação do remetente (UUID).
        // Por simplicidade, vamos apenas adicionar a mensagem à lista de mensagens capturadas.
        messages.add(message);
    }

    @Override
    public void sendMessage(@Nullable UUID uuid, @NotNull String... strings) {
        for (String message : strings) {
            sendMessage(uuid, message);
        }
    }

    // Implemente todos os outros métodos abstratos do CommandSender aqui
    // Exemplo:
    @Override
    public boolean isOp() {
        return originalSender.isOp();
    }

    @Override
    public void setOp(boolean b) {

    }

    @Override
    public Server getServer() {
        return originalSender.getServer();
    }

    @NotNull
    @Override
    public String getName() {
        return null;
    }

    @NotNull
    @Override
    public Spigot spigot() {
        return null;
    }

    @Override
    public boolean isPermissionSet(@NotNull String s) {
        return false;
    }

    @Override
    public boolean isPermissionSet(@NotNull Permission permission) {
        return false;
    }

    @Override
    public boolean hasPermission(@NotNull String s) {
        return false;
    }

    @Override
    public boolean hasPermission(@NotNull Permission permission) {
        return false;
    }

    @NotNull
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String s, boolean b) {
        return null;
    }

    @NotNull
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin) {
        return null;
    }

    @Nullable
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String s, boolean b, int i) {
        return null;
    }

    @Nullable
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, int i) {
        return null;
    }

    @Override
    public void removeAttachment(@NotNull PermissionAttachment permissionAttachment) {

    }

    @Override
    public void recalculatePermissions() {

    }

    @NotNull
    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return null;
    }

    // Adicione os métodos faltantes conforme necessário...
}
