package me.monkeykiller.customblocks.utils;

import org.bukkit.ChatColor;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class Utils {
    public static Location getInteractionPoint(@NotNull Location start, int maxDistance, boolean ignorePassableBlocks) {
        if (start.getWorld() == null) return null;
        RayTraceResult rtr = start.getWorld().rayTraceBlocks(start,
                start.getDirection(), maxDistance, FluidCollisionMode.NEVER, ignorePassableBlocks);
        if (rtr == null || rtr.getHitBlock() == null) return null;
        return rtr.getHitPosition().subtract(rtr.getHitBlock().getLocation().toVector())
                .toLocation(start.getWorld());
    }

    public static String colorize(@NotNull String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}

