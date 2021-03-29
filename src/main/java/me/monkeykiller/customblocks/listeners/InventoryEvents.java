package me.monkeykiller.customblocks.listeners;

import me.monkeykiller.customblocks.Utils;
import me.monkeykiller.customblocks.utils.config;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryEvents extends BaseEvent {

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        Inventory inv = event.getClickedInventory();
        assert inv != null && !Utils.Item.isAirOrNull(inv.getItem(event.getSlot()));
        if (event.getView().getBottomInventory() == event.getClickedInventory()) return;
        ItemStack item = inv.getItem(event.getSlot()).clone();
        Player player = (Player) event.getView().getPlayer();

        if (!event.getView().getTitle().equals(config.cbksGUITitle)) return;
        event.setCancelled(true);
        if (event.getClick().isShiftClick())
            item.setAmount(64);
        player.getInventory().addItem(item);
    }

}
