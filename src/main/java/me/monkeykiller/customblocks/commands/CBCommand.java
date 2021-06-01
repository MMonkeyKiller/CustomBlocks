package me.monkeykiller.customblocks.commands;

import me.monkeykiller.customblocks.CBPlugin;
import me.monkeykiller.customblocks.CustomBlock;
import me.monkeykiller.customblocks.config.config;
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
        if (!BaseCommand.hasPermission(sender, "customblocks.command.customblocks", true)) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command (" + ChatColor.GOLD + "customblocks.command.customblocks" + ChatColor.RED + ")");
            return false;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            CBPlugin.configData.reloadConfig(); // /cb reload issue FIX
            sender.sendMessage(CBPlugin.colorify(config.prefixes.prefix + "&aConfiguration reloaded!"));
            Bukkit.getLogger().info(CBPlugin.colorify(config.prefixes.prefix + "&aConfiguration reloaded!"));
            return true;
        } else if (args[0].equalsIgnoreCase("add")) {
            if (args.length < 5) return false;
            try {
                config.blocks.add(new CustomBlock(args[1], parseInt(args[2]), Instrument.valueOf(args[3]), parseInt(args[4]), args[5].equalsIgnoreCase("true")).serialize());
                sender.sendMessage(CBPlugin.colorify(config.prefixes.prefix + "Block " + args[1] + " added sucessfully"));
            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage(CBPlugin.colorify(config.prefixes.prefix + "Block " + args[1] + " cannot be added, please check the logs"));
                return false;
            }
            CBPlugin.configData.saveBlocks();
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /customblocks <add/reload>");
            return true;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!BaseCommand.hasPermission(sender, "customblocks.command.customblocks", true))
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
                            CustomBlock.REGISTRY.stream().map(cb -> cb.id).collect(Collectors.toList()),    // block id
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
