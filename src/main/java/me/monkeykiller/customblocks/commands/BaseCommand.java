package me.monkeykiller.customblocks.commands;

import me.monkeykiller.customblocks.CBPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class BaseCommand implements CommandExecutor {
    public static List<BaseCommand> REGISTRY = new ArrayList<>();

    public String name;
    public List<String> aliases;

    public BaseCommand(@NotNull String name, @Nullable List<String> aliases) {
        this.name = name;
        this.aliases = aliases;
        //Main.plugin.getCommand(name).setTabCompleter(this);
        PluginCommand cmd = CBPlugin.plugin.getCommand(name);
        assert cmd != null;
        cmd.setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return false;
    }

    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }

    public PluginCommand parseCommand() {
        return CBPlugin.plugin.getCommand(name);
    }



    public static BaseCommand getCommand(String cmd) {
        for (BaseCommand command : REGISTRY)
            if (cmd.equalsIgnoreCase(command.name) || command.aliases.contains(cmd.toLowerCase()))
                return command;
        return null;
    }

    public static boolean hasPermission(CommandSender sender, String permission, boolean op) {
        return !(sender instanceof Player) || ((((Player) sender).hasPermission(permission) || op) && ((Player) sender).isOp());
    }

}
