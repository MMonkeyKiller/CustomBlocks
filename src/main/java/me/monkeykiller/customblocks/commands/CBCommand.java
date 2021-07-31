package me.monkeykiller.customblocks.commands;

import me.monkeykiller.customblocks.CustomBlocksPlugin;
import me.monkeykiller.customblocks.blocks.CustomBlock;
import me.monkeykiller.customblocks.config.config;
import me.monkeykiller.customblocks.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Instrument;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

public class CBCommand extends BaseCommand {
    public CBCommand() {
        super("customblocks", Collections.singletonList("cb"));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) return false;
        if (!sender.hasPermission("customblocks.command.customblocks")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command (" + ChatColor.GOLD + "customblocks.command.customblocks" + ChatColor.RED + ")");
            return false;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            CustomBlocksPlugin.configData.reloadConfig();
            sender.sendMessage(Utils.colorize(config.prefixes.prefix + "&aConfiguration reloaded!"));
            Bukkit.getLogger().info(Utils.colorize(config.prefixes.prefix + "&aConfiguration reloaded!"));
            return true;
        } else if (args[0].equalsIgnoreCase("add")) {
            if (args.length < 5) return false;
            try {
                CustomBlock cb = new CustomBlock(args[1], parseInt(args[2]), Instrument.valueOf(args[3]), parseInt(args[4]), args[5].equalsIgnoreCase("true"));
                CustomBlock.register(cb, false);
                config.blocks.add(cb.serialize());
                sender.sendMessage(Utils.colorize(config.prefixes.prefix + "Block " + args[1] + " added sucessfully"));
            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage(Utils.colorize(config.prefixes.prefix + "Block " + args[1] + " cannot be added, please check the logs"));
                return false;
            }
            CustomBlocksPlugin.configData.saveBlocks();
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /customblocks <add/reload>");
            return true;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission("customblocks.command.customblocks"))
            return null;
        switch (args.length) {
            case 0:
                return null;
            case 1:
                return Arrays.asList("add", "reload");
            default:
                if (args[0].equalsIgnoreCase("reload"))
                    return null;
                if (args[0].equalsIgnoreCase("add"))
                    return Arrays.asList(
                            CustomBlock.getRegistry().stream().map(CustomBlock::getId).collect(Collectors.toList()),    // block id
                            Collections.singletonList("(example) 0"), // itemModelData
                            Arrays.stream(Instrument.values()).map(Enum::toString).collect(Collectors.toList()),           // instruments
                            allPitchNumbers(),  // note
                            Arrays.asList("false", "true")).get(args.length - 2);
                break;
        }
        return null;
    }

    private List<String> allPitchNumbers() {
        List<String> values = new ArrayList<>();
        for (int i = 0; i < 25; i++) values.add((i < 10 ? "0" : "") + i);
        return values;
    }
}
