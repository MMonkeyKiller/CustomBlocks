package me.monkeykiller.customblocks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Instrument;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import static java.lang.Integer.parseInt;

public class Commands {
    private final Main plugin;

    public Commands(Main plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) throws Exception {
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
            } else if (args[0].equalsIgnoreCase("addBlock")) {
                if (args.length < 5) return false;
                plugin.configData.blocks.add(new CustomBlock(args[1], parseInt(args[2]), Instrument.valueOf(args[3]), parseInt(args[4]), args[5].equalsIgnoreCase("true")).serialize());
                plugin.config.set("blocks", plugin.configData.blocks);
                plugin.saveConfig();
                sender.sendMessage("Block added correctly");
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /customblocks <reload>");
            }
        } else if (label.equalsIgnoreCase("cblocks") || label.equalsIgnoreCase("cbks")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You're not a player!");
                return false;
            }
            if (!hasPermission(sender, "customblocks.command.cblocks", true)) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command (" + ChatColor.GOLD + "customblocks.command.cblocks" + ChatColor.RED + ")");
                return false;
            }
            Inventory inv = Bukkit.createInventory(null, 9 * 6, "CustomBlocks");
            int i = 0;
            for (CustomBlock CB : CustomBlock.REGISTRY) {
                if (i > inv.getSize()) break;
                inv.setItem(i, CB.getItemStack());
                i++;
            }
            ((Player) sender).openInventory(inv);

        }
        return false;
    }

    private boolean hasPermission(CommandSender sender, String permission, boolean op) {
        return (sender instanceof Player && (((Player) sender).hasPermission(permission) || op ? ((Player) sender).isOp() : false)) || !(sender instanceof Player);
    }
}
