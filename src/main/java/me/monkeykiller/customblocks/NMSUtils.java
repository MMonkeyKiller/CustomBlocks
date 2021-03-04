package me.monkeykiller.customblocks;

import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.MovingObjectPositionBlock;
import net.minecraft.server.v1_16_R3.Vec3D;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

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
}
