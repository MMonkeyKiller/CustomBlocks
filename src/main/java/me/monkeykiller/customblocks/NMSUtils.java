package me.monkeykiller.customblocks;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
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
}
