package me.monkeykiller.customblocks.nms;

import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.EnumHand;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.context.ItemActionContext;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class NMS_v1_18_R1 extends NMSHandler {
    @Override
    public void swingHand(@NotNull Player player, EquipmentSlot slot) {
        switch (slot) {
            case HAND:
                player.swingMainHand();
                break;
            case OFF_HAND:
                player.swingOffHand();
                break;
        }
    }

    @Override
    public void placeItem(@NotNull Player player, @NotNull ItemStack item, @NotNull Block block, EquipmentSlot slot) {
        EntityPlayer human = ((CraftPlayer) player).getHandle();
        EnumHand hand = slot == EquipmentSlot.HAND ? EnumHand.a : slot == EquipmentSlot.OFF_HAND ? EnumHand.b : null;
        Validate.notNull(hand, "The given slot is not a hand slot");

        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        MovingObjectPositionBlock mopb = (MovingObjectPositionBlock) _genMOPB(player, block);

        nmsItem.useOn(new ItemActionContext(human, hand, mopb), hand);
    }

    @Override
    public Object _genMOPB(@NotNull Player player, @NotNull Block block) {
        Location source = player.getEyeLocation();
        EntityHuman human = ((CraftPlayer) player).getHandle();
        return new MovingObjectPositionBlock(
                new Vec3D(source.getX(), source.getY(), source.getZ()),
                human.ct(),
                new BlockPosition(block.getX(), block.getY(), block.getZ()),
                false
        );
    }
}
