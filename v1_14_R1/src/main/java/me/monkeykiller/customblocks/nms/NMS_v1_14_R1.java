package me.monkeykiller.customblocks.nms;

import net.minecraft.server.v1_14_R1.*;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class NMS_v1_14_R1 extends NMSHandler {
    @Override
    public void swingHand(@NotNull Player player, EquipmentSlot slot) {
        EnumHand hand = slot == EquipmentSlot.HAND ? EnumHand.MAIN_HAND : slot == EquipmentSlot.OFF_HAND ? EnumHand.OFF_HAND : null;
        Validate.notNull(hand, "The given slot is not a hand slot");
        ((CraftPlayer) player).getHandle().a(hand);
    }

    @Override
    public void placeItem(@NotNull Player player, @NotNull ItemStack item, @NotNull Block block, EquipmentSlot slot) {
        EntityPlayer human = ((CraftPlayer) player).getHandle();
        EnumHand hand = slot == EquipmentSlot.HAND ? EnumHand.MAIN_HAND : slot == EquipmentSlot.OFF_HAND ? EnumHand.OFF_HAND : null;
        Validate.notNull(hand, "The given slot is not a hand slot");

        net.minecraft.server.v1_14_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        MovingObjectPositionBlock mopb = (MovingObjectPositionBlock) _genMOPB(player, block);

        nmsItem.placeItem(new ItemActionContext(human, hand, mopb), hand);
    }

    @Override
    public Object _genMOPB(@NotNull Player player, @NotNull Block block) {
        Location source = player.getEyeLocation();
        EntityHuman human = ((CraftPlayer) player).getHandle();
        return new MovingObjectPositionBlock(
                new Vec3D(source.getX(), source.getY(), source.getZ()),
                human.getDirection(),
                new BlockPosition(block.getX(), block.getY(), block.getZ()),
                false
        );
    }
}
