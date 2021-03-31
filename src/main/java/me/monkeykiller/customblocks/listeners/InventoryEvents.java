package me.monkeykiller.customblocks.listeners;

import me.monkeykiller.customblocks.config.config;
import me.monkeykiller.customblocks.utils.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryEvents extends BaseEvent {

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        Inventory inv = event.getClickedInventory();
        assert inv != null;
        if (event.getCurrentItem() == null || inv.getItem(event.getSlot()) == null) return;
        ItemStack item = inv.getItem(event.getSlot());
        if (event.getView().getBottomInventory().equals(event.getClickedInventory()) || ItemUtils.isAirOrNull(item))
            return;
        if (!event.getView().getTitle().equals(config.cbksGUITitle)) return;
        item = item.clone();
        Player player = (Player) event.getView().getPlayer();

        event.setCancelled(true);
        if (event.getClick().isShiftClick())
            item.setAmount(64);
        player.getInventory().addItem(item);
    }

}
