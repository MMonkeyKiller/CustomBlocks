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

import java.util.Objects;

public class InventoryEvents extends BaseEvent {

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        Inventory inv = event.getClickedInventory();
        assert inv != null;
        if (event.getCurrentItem() == null || inv.getItem(event.getSlot()) == null) return;
        ItemStack item = inv.getItem(event.getSlot());
        if (event.getView().getBottomInventory().equals(event.getClickedInventory()) || ItemUtils.isAirOrNull(item))
            return;
        if (!event.getView().getTitle().startsWith(config.cbksGUITitle)) return;
        item = Objects.requireNonNull(CustomBlock.getCustomBlockbyItem(item)).getItemStack(false);
        Player player = (Player) event.getView().getPlayer();

        event.setCancelled(true);

        if (event.getSlot() < 45) {
            if (event.getClick().isShiftClick())
                item.setAmount(64);
            player.getInventory().addItem(item);
        } else {
            int page = CustomBlocksGUI.savedPages.get(player.getUniqueId());
            if (event.getSlot() == 45)  // back btn
                CustomBlocksGUI.open(player, page - 1);
            else if (event.getSlot() == 49 && event.isLeftClick())  // info btn
                CustomBlocksGUI.open(player, 1);
            else if (event.getSlot() == 53)  // next btn
                CustomBlocksGUI.open(player, page + 1);
        }
    }

}
