package me.monkeykiller.customblocks.listeners;

import me.monkeykiller.customblocks.ItemUtils;
import me.monkeykiller.customblocks.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryEvents implements Listener {
    public Main plugin;

    public InventoryEvents(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        Inventory inv = event.getClickedInventory();
        assert inv != null;
        ItemStack item = inv.getItem(event.getSlot()).clone();
        Player player = (Player) event.getView().getPlayer();

        String title = event.getView().getTitle();
        if (ItemUtils.isAirOrNull(item) || !title.equals("CustomBlocks")) return;
        event.setCancelled(true);
        if (event.getView().getBottomInventory() == event.getClickedInventory())
            return;
        if (event.getClick().isShiftClick())
            item.setAmount(64);
        player.getInventory().addItem(item);
    }

}
