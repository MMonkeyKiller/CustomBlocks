package me.monkeykiller.customblocks.listeners;

import me.monkeykiller.customblocks.CustomBlock;
import me.monkeykiller.customblocks.CustomBlocksGUI;
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
        int slot = event.getSlot();
        if (!event.getView().getTitle().startsWith(config.cbksGUITitle)
                || inv == null || event.getCurrentItem() == null || inv.getItem(slot) == null) return;
        ItemStack item = inv.getItem(slot);
        if (inv != event.getView().getTopInventory() || ItemUtils.isAirOrNull(item))
            return;

        CustomBlock cb = CustomBlock.getCustomBlockbyItem(item);
        Player player = (Player) event.getView().getPlayer();

        event.setCancelled(true);
        if (cb != null && event.getSlot() < 45) {
            item = cb.getItemStack();
            if (event.getClick().isShiftClick())
                item.setAmount(64);
            player.getInventory().addItem(item);
            return;
        }

        int page = CustomBlocksGUI.savedPages.getOrDefault(player.getUniqueId(), 1);
        if (event.getSlot() == 45) CustomBlocksGUI.open(player, page - 1);
        else if (event.getSlot() == 49 && event.isLeftClick())
            CustomBlocksGUI.open(player, 1);
        else if (event.getSlot() == 53) CustomBlocksGUI.open(player, page + 1);
    }

}
