package me.monkeykiller.customblocks.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Commands {
    public static List<CBCommand> REGISTRY = new ArrayList<>();

    public static CBCommand getCommand(String cmd) {
        for (CBCommand command : REGISTRY)
            if (cmd.equalsIgnoreCase(command.name) || command.aliases.contains(cmd.toLowerCase()))
                return command;
        return null;
    }

    public static boolean hasPermission(CommandSender sender, String permission, boolean op) {
        return !(sender instanceof Player) || ((((Player) sender).hasPermission(permission) || op) && ((Player) sender).isOp());
    }
}
