package me.monkeykiller.customblocks.listeners;

import me.monkeykiller.customblocks.Main;
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
        if (inv.getItem(event.getSlot()) == null) return;
        ItemStack item = Objects.requireNonNull(inv.getItem(event.getSlot())).clone();
        Player player = (Player) event.getView().getPlayer();
        if (event.getClick().isCreativeAction())
            player.sendMessage(item.toString());

        String title = Main.configData.cbksGUITitle;
        if (ItemUtils.isAirOrNull(item) || !title.equals("CustomBlocks")) return;
        event.setCancelled(true);
        if (event.getView().getBottomInventory() == event.getClickedInventory())
            return;
        if (event.getClick().isShiftClick())
            item.setAmount(64);
        player.getInventory().addItem(item);
    }

}
