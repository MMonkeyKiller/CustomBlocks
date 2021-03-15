package me.monkeykiller.customblocks.utils;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import me.lucko.commodore.Commodore;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.PluginCommand;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

public class NMSUtils {
    public static Location getInteractionPoint(World world, MovingObjectPositionBlock movingobjectpositionblock) {
        Vec3D hitVec = movingobjectpositionblock.getPos();
        return hitVec == null ? null : new Location(world, hitVec.x, hitVec.y, hitVec.z);
    }

    public static Vec3D LocToVec(Location loc) {
        return new Vec3D(loc.getX(), loc.getY(), loc.getZ());
    }

    public static BlockPosition BlockToBlockPos(Block block) {
        return new BlockPosition(block.getX(), block.getY(), block.getZ());
    }

    public static EntityPlayer parseHuman(Player p) {
        return ((CraftPlayer) p).getHandle();
    }

    public static EnumHand parseEnumHand(EquipmentSlot es) {
        return es == EquipmentSlot.HAND ? EnumHand.MAIN_HAND : (es == EquipmentSlot.OFF_HAND ? EnumHand.OFF_HAND : null);

    }

    public static void registerCompletions(Commodore commodore, PluginCommand command) {
        try {
            commodore.register((PluginCommand) command, LiteralArgumentBuilder.literal(command.getName())
                    .then(LiteralArgumentBuilder.literal("reload"))
                    .then(LiteralArgumentBuilder.literal("add")
                            .then(RequiredArgumentBuilder.argument("id", StringArgumentType.greedyString())
                                    .then(RequiredArgumentBuilder.argument("itemModelData", IntegerArgumentType.integer())
                                            .then(RequiredArgumentBuilder.argument("instrument", StringArgumentType.string())
                                                    .then(RequiredArgumentBuilder.argument("note", IntegerArgumentType.integer(1, 24))
                                                            .then(RequiredArgumentBuilder.argument("powered", BoolArgumentType.bool()))
                                                    )
                                            )
                                    )
                            )
                    )
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
