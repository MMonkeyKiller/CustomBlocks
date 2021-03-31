package me.monkeykiller.customblocks.utils;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

public class NMSUtils {
    public static Vec3D LocToVec(Location loc) {
        return new Vec3D(loc.getX(), loc.getY(), loc.getZ());
    }

    public static BlockPosition BlockToBlockPos(Location location) {
        return new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static EntityPlayer parseHuman(Player p) {
        return ((CraftPlayer) p).getHandle();
    }

    public static EnumHand parseEnumHand(EquipmentSlot es) {
        return es == EquipmentSlot.HAND ? EnumHand.MAIN_HAND : (es == EquipmentSlot.OFF_HAND ? EnumHand.OFF_HAND : null);
    }

    public static MovingObjectPositionBlock getMOPB(@NotNull Player player, @NotNull Location blockLoc, boolean var3) {
        return new MovingObjectPositionBlock(LocToVec(player.getEyeLocation()), parseHuman(player).getDirection(), BlockToBlockPos(blockLoc), var3);
    }
}
