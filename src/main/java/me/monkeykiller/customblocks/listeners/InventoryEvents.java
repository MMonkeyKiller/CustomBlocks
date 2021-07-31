package me.monkeykiller.customblocks.listeners;

import me.monkeykiller.customblocks.CustomBlocksGUI;
import me.monkeykiller.customblocks.blocks.CustomBlock;
import me.monkeykiller.customblocks.config.config;
import me.monkeykiller.customblocks.utils.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class InventoryEvents implements Listener {

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().startsWith(config.cbksGUITitle)) return;
        event.setCancelled(true);

        Inventory inv = event.getClickedInventory();
        if (!Objects.equals(inv, event.getView().getTopInventory())) return;
        int slot = event.getSlot();
        if (slot < 0) return;
        ItemStack item = inv.getItem(slot);
        if (ItemUtils.isAirOrNull(item)) return;

        CustomBlock cb = CustomBlock.getCustomBlockbyItem(item);
        Player player = (Player) event.getView().getPlayer();

        if (cb != null && slot < 45) {
            item = cb.getItemStack();
            if (event.getClick().isShiftClick()) item.setAmount(64);
            player.getInventory().addItem(item);
            return;
        }

        int page = CustomBlocksGUI.savedPages.getOrDefault(player.getUniqueId(), 1);
        switch (slot) {
            case 45:
                CustomBlocksGUI.open(player, page - 1);
                break;
            case 49:
                if (!event.isShiftClick()) break;
                CustomBlocksGUI.open(player, 1);
                break;
            case 53:
                CustomBlocksGUI.open(player, page + 1);
                break;
        }
    }

}
