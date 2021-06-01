package me.monkeykiller.customblocks.commands;

import me.monkeykiller.customblocks.CustomBlocksGUI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class CBMenu extends BaseCommand {

    public CBMenu() {
        super("cblocks", Collections.singletonList("cbks"));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You're not a player!");
            return false;
        }
        if (!BaseCommand.hasPermission(sender, "customblocks.command.cblocks", true)) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command (" + ChatColor.GOLD + "customblocks.command.cblocks" + ChatColor.RED + ")");
            return false;
        }
        CustomBlocksGUI.open(((Player) sender));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
