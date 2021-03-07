package me.monkeykiller.customblocks;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands {
    private final Main plugin;

    public Commands(Main plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("customblocks") || label.equalsIgnoreCase("cb")) {
            if (args.length < 1) return false;
            if (args[0].equalsIgnoreCase("reload")) {
                if (!hasPermission(sender, "customblocks.command.customblocks", true)) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command (" + ChatColor.GOLD + "customblocks.command.customblocks" + ChatColor.RED + ")");
                    return false;
                }
                plugin.loadConfig();
                sender.sendMessage(ChatColor.GREEN + "Configuration reloaded!");
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /customblocks <reload>");
            }
        }
        return false;
    }

    private boolean hasPermission(CommandSender sender, String permission, boolean op) {
        return (sender instanceof Player && (((Player) sender).hasPermission(permission) || op ? ((Player) sender).isOp() : false)) || !(sender instanceof Player);
    }
}
