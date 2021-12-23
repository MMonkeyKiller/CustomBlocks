package me.monkeykiller.customblocks.utils;

import me.monkeykiller.customblocks.CustomBlocksPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;

@SuppressWarnings("unused")
public class Utils {

    public static String getNMSVersion() {
        String version = CustomBlocksPlugin.plugin.getServer().getClass().getPackage().getName();
        return version.substring(version.lastIndexOf('.') + 1);
    }

    public static String getMinecraftVersion() {
        String version = Bukkit.getBukkitVersion();
        return version.substring(0, version.indexOf('-'));
    }

    public static void log(@NotNull String text) {
        Bukkit.getLogger().log(Level.INFO, text);
    }

    public static Location getInteractionPoint(@NotNull Location start, int maxDistance, boolean ignorePassableBlocks) {
        if (start.getWorld() == null) return null;
        RayTraceResult rtr = start.getWorld().rayTraceBlocks(start,
                start.getDirection(), maxDistance, FluidCollisionMode.NEVER, ignorePassableBlocks);
        if (rtr == null || rtr.getHitBlock() == null) return null;
        return rtr.getHitPosition().subtract(rtr.getHitBlock().getLocation().toVector())
                .toLocation(start.getWorld());
    }

    public static List<Entity> getEntitiesOnBlock(@NotNull Block block, @NotNull Predicate<Entity> predicate) {
        Location loc = block.getLocation().add(.5, .5, .5); // center the start location in the middle of the block
        return (List<Entity>) block.getWorld().getNearbyEntities(loc, .5, .5, .5, predicate);
    }

    public static String colorize(@NotNull String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}

