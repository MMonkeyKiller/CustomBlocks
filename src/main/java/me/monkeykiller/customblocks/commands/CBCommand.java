package me.monkeykiller.customblocks.commands;

import me.monkeykiller.customblocks.CustomBlocks;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public class CBCommand implements CommandExecutor {
    public String name;
    public List<String> aliases;

    public CBCommand(@NotNull String name, @Nullable List<String> aliases) {
        this.name = name;
        this.aliases = aliases;
        //Main.plugin.getCommand(name).setTabCompleter(this);
        PluginCommand cmd = CustomBlocks.plugin.getCommand(name);
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
        return CustomBlocks.plugin.getCommand(name);
    }

}
