package me.monkeykiller.customblocks.nms;

import me.monkeykiller.customblocks.utils.Utils;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CommonNMSHandler extends NMSHandler {
    private Class<?> enumHandClass, itemActionContextClass, mopbClass;
    private Object mainHand, offHand;
    private Class<?> vec3DClass, blockPositionClass;

    private Class<?> craftPlayerClass, craftItemStackClass;

    public CommonNMSHandler() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        final String nmsVersion = Utils.getNMSVersion();
        final String nmsPackageName = "net.minecraft.world.";
        final String cbPackageName = "org.bukkit.craftbukkit." + nmsVersion + ".";
        enumHandClass = Class.forName(nmsPackageName + "EnumHand");
        mainHand = enumHandClass.getDeclaredField("a").get(null);
        offHand = enumHandClass.getDeclaredField("b").get(null);
        itemActionContextClass = Class.forName(nmsPackageName + "item.context.ItemActionContext");
        mopbClass = Class.forName(nmsPackageName + "phys.MovingObjectPositionBlock");
        vec3DClass = Class.forName(nmsPackageName + "phys.Vec3D");
        blockPositionClass = Class.forName("net.minecraft.core.BlockPosition");

        craftPlayerClass = Class.forName(cbPackageName + "entity.CraftPlayer");
        craftItemStackClass = Class.forName(cbPackageName + "inventory.CraftItemStack");
    }

    @Override
    public void swingHand(@NotNull Player player, EquipmentSlot slot) {
        try {
            Object hand = getHand(slot);
            Validate.notNull(hand, "The given slot is not a hand slot");
            Object nmsPlayer = craftPlayerClass.getDeclaredMethod("getHandle").invoke(craftPlayerClass.cast(player));
            Method method = nmsPlayer.getClass().getDeclaredMethod("a", enumHandClass);
            method.invoke(nmsPlayer, hand);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void placeItem(@NotNull Player player, @NotNull ItemStack item, @NotNull Block block, EquipmentSlot slot) {
        try {
            Object human = craftPlayerClass.getDeclaredMethod("getHandle").invoke(craftPlayerClass.cast(player));
            Object hand = getHand(slot);
            Validate.notNull(hand, "The given slot is not a hand slot");

            Object nmsItem = craftItemStackClass.getDeclaredMethod("asNMSCopy", ItemStack.class).invoke(null, item);
            Object mopb = _genMOPB(player, block);

            Object ctx = itemActionContextClass.getConstructor(human.getClass(), hand.getClass(), mopb.getClass());
            nmsItem.getClass().getDeclaredMethod("placeItem", ctx.getClass(), hand.getClass());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object _genMOPB(@NotNull Player player, @NotNull Block block) {
        Location source = player.getEyeLocation();
        Object human = ((CraftPlayer) player).getHandle();

        try {
            Method method;
            try {
                method = human.getClass().getDeclaredMethod("getDirection");
            } catch (NoSuchMethodException | SecurityException e) {
                method = human.getClass().getDeclaredMethod("ct");
            }
            Object direction = method.invoke(human);
            Constructor constructor = mopbClass.getConstructor(vec3DClass, direction.getClass(), blockPositionClass, boolean.class);
            return constructor.newInstance(
                    toVec3D(source.toVector()),
                    direction, toBlockPos(block), false
            );
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException |
                 InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    //

    private Object getHand(@NotNull EquipmentSlot slot) {
        if (slot == EquipmentSlot.HAND) return mainHand;
        if (slot == EquipmentSlot.OFF_HAND) return offHand;
        return null;
    }

    private Object toVec3D(@NotNull Vector vec) {
        try {
            Constructor constructor = vec3DClass.getConstructor(double.class, double.class, double.class);
            return constructor.newInstance(vec.getX(), vec.getY(), vec.getZ());
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Object toBlockPos(@NotNull Block block) {
        try {
            Constructor constructor = blockPositionClass.getConstructor(int.class, int.class, int.class);
            return constructor.newInstance(block.getX(), block.getY(), block.getZ());
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
