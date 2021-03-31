package me.monkeykiller.customblocks.libs.worldedit;

import com.sk89q.worldedit.EditSession.Stage;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.util.eventbus.EventHandler.Priority;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import me.monkeykiller.customblocks.CustomBlock;
import me.monkeykiller.customblocks.CBPlugin;
import me.monkeykiller.customblocks.config.config;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

@SuppressWarnings("unused")
public class WEListener implements Listener {

    public WEListener() {
        Bukkit.getPluginManager().registerEvents(this, CBPlugin.plugin);
    }

    @Subscribe(priority = Priority.VERY_LATE)
    public void onEditSession(EditSessionEvent event) {
        if (event.getStage() == Stage.BEFORE_CHANGE)
            event.setExtent(new CBDelegate(this, event));
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (Bukkit.getPluginManager().getPlugin("WorldEdit") == null) return;

        String message = event.getMessage();
        String[] args = message.split(" ");
        if (!message.startsWith("//replace") || message.startsWith("//replacenear") || args.length < 1) return;
        String CBId = args[message.startsWith("//replacenear") ? 2 : 1];
        if (!CustomBlock.isCustomBlock(CBId)) return;
        CustomBlock CB = CustomBlock.getCustomBlockbyId(CBId);
        assert CB != null;
        String id = String.format("%s[note=%s,instrument=%s,powered=%s]", Material.NOTE_BLOCK.toString().toLowerCase(), CB.note, CB.instrument.toString().toLowerCase(), CB.powered ? "true" : "false");
        event.setMessage(message.replace(CBId, id));
    }

    @EventHandler
    @SuppressWarnings("unchecked")
    public void onPluginEnable(PluginEnableEvent event) {
        if (!event.getPlugin().getName().equals("WorldEdit")) return;
        WorldEdit.getInstance().getEventBus().register(this);
        WorldEdit.getInstance().getBlockFactory().register(new Parser(WorldEdit.getInstance()));
        System.out.println(config.prefixes.prefix + "WorldEdit found! Setting up CustomBlocks-WorldEdit...");

    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin().getName().equals("WorldEdit"))
            WorldEdit.getInstance().getEventBus().unregister(this);
    }
}
