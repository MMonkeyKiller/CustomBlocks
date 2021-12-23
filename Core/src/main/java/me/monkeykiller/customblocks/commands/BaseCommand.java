package me.monkeykiller.customblocks.commands;

import me.monkeykiller.customblocks.CustomBlocksPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class BaseCommand implements TabExecutor {
    public static List<BaseCommand> REGISTRY = new ArrayList<>();

    public String name;
    public List<String> aliases;

    public BaseCommand(@NotNull String name, @Nullable List<String> aliases) {
        this.name = name;
        this.aliases = aliases;
        PluginCommand cmd = CustomBlocksPlugin.plugin.getCommand(name);
        assert cmd != null;
        cmd.setExecutor(this);
        cmd.setTabCompleter(this);
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
        return CustomBlocksPlugin.plugin.getCommand(name);
    }


    public static BaseCommand getCommand(String cmd) {
        for (BaseCommand command : REGISTRY)
            if (cmd.equalsIgnoreCase(command.name) || command.aliases.contains(cmd.toLowerCase()))
                return command;
        return null;
    }

}
