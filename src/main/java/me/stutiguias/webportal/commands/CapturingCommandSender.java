package me.stutiguias.webportal.commands;

import org.bukkit.Bukkit;
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

    @Override
    public void sendMessage(UUID sender, String message) {
        messages.add(message);
    }

    @Override
    public void sendMessage(@Nullable UUID uuid, @NotNull String... strings) {
        for (String message : strings) {
            sendMessage(uuid, message);
        }
    }

    @Override
    public boolean isOp() {
        return originalSender.isOp();
    }

    @Override
    public void setOp(boolean b) {

    }

    @NotNull
    @Override
    public Server getServer() {
        return originalSender.getServer();
    }

    @NotNull
    @Override
    public String getName() {
        return originalSender.getName();
    }

    @NotNull
    @Override
    public Spigot spigot() {
        return originalSender.spigot();
    }

    @Override
    public boolean isPermissionSet(@NotNull String s) {
        return originalSender.isPermissionSet(s);
    }

    @Override
    public boolean isPermissionSet(@NotNull Permission permission) {
        return originalSender.isPermissionSet(permission);
    }

    @Override
    public boolean hasPermission(@NotNull String s) {
        return originalSender.hasPermission(s);
    }

    @Override
    public boolean hasPermission(@NotNull Permission permission) {
        return originalSender.hasPermission(permission);
    }

    @NotNull
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String s, boolean b) {
        return originalSender.addAttachment(plugin, s, b);
    }

    @NotNull
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin) {
        return originalSender.addAttachment(plugin);
    }

    @Nullable
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String s, boolean b, int i) {
        return originalSender.addAttachment(plugin, s, b, i);
    }

    @Nullable
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, int i) {
        return originalSender.addAttachment(plugin, i);
    }

    @Override
    public void removeAttachment(@NotNull PermissionAttachment permissionAttachment) {
            originalSender.removeAttachment(permissionAttachment);
    }

    @Override
    public void recalculatePermissions() {
        originalSender.recalculatePermissions();
    }

    @NotNull
    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return originalSender.getEffectivePermissions();
    }

}
