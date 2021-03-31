package me.monkeykiller.customblocks.utils;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.monkeykiller.customblocks.CBPlugin;
import org.bukkit.command.PluginCommand;

public class BrigadierUtils {
    public static void registerCompletions(PluginCommand command) {
        try {
            LiteralCommandNode<Object> add = LiteralArgumentBuilder.literal("add")
                    .then(RequiredArgumentBuilder.argument("id", StringArgumentType.greedyString())
                            .then(RequiredArgumentBuilder.argument("itemModelData", IntegerArgumentType.integer())
                                    .then(RequiredArgumentBuilder.argument("instrument", StringArgumentType.string())
                                            .then(RequiredArgumentBuilder.argument("note", IntegerArgumentType.integer(1, 24))
                                                    .then(RequiredArgumentBuilder.argument("powered", BoolArgumentType.bool()))
                                            )
                                    )
                            )
                    ).build();
            CBPlugin.commodore.register(command, LiteralArgumentBuilder.literal(command.getName())
                    .then(LiteralArgumentBuilder.literal("reload"))
                    .then(add)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
