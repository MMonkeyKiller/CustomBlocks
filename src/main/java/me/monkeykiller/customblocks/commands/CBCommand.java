package me.monkeykiller.customblocks.commands;

import me.monkeykiller.customblocks.Main;
import org.bukkit.command.*;
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
        Main.plugin.getCommand(name).setExecutor(this);
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
        return Main.plugin.getCommand(name);
    }

}
