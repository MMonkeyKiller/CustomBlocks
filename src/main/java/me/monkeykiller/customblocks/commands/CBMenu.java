package me.monkeykiller.customblocks.commands;

import me.monkeykiller.customblocks.CustomBlock;
import me.monkeykiller.customblocks.utils.config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class CBMenu extends CBCommand {

    public CBMenu() {
        super("cblocks", Collections.singletonList("cbks"));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You're not a player!");
            return false;
        }
        if (!Commands.hasPermission(sender, "customblocks.command.cblocks", true)) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command (" + ChatColor.GOLD + "customblocks.command.cblocks" + ChatColor.RED + ")");
            return false;
        }
        Inventory inv = Bukkit.createInventory(null, 9 * 6, config.cbksGUITitle);
        int i = 0;
        for (CustomBlock CB : CustomBlock.REGISTRY) {
            if (i > inv.getSize()) break;
            inv.setItem(i, CB.getItemStack());
            i++;
        }

        ((Player) sender).openInventory(inv);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
