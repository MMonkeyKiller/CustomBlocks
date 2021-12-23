package me.monkeykiller.customblocks;

import me.monkeykiller.customblocks.api.CustomBlocksLoadEvent;
import me.monkeykiller.customblocks.blocks.CustomBlock;
import me.monkeykiller.customblocks.commands.BaseCommand;
import me.monkeykiller.customblocks.commands.CBCommand;
import me.monkeykiller.customblocks.commands.CBMenu;
import me.monkeykiller.customblocks.config.config;
import me.monkeykiller.customblocks.config.configData;
import me.monkeykiller.customblocks.libs.worldedit.WEListener;
import me.monkeykiller.customblocks.listeners.Events;
import me.monkeykiller.customblocks.listeners.InventoryEvents;
import me.monkeykiller.customblocks.nms.NMSHandler;
import me.monkeykiller.customblocks.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public final class CustomBlocksPlugin extends JavaPlugin {
    public static CustomBlocksPlugin plugin;
    public static me.monkeykiller.customblocks.config.configData configData;
    private static NMSHandler nmsHandler;
    public WEListener weListener;

    public static NMSHandler getNmsHandler() {
        return nmsHandler;
    }

    @Override
    public void onEnable() {
        CustomBlock.clear();
        plugin = this;
        configData = new configData();
        configData.setPlugin(this);
        loadConfig();
        final String mcVersion = Utils.getMinecraftVersion(),
                nmsVersion = Utils.getNMSVersion();
        Utils.log(config.prefixes.prefix + "Loading NMS Handler...");
        nmsHandler = NMSHandler.getHandler(mcVersion, nmsVersion);
        if (nmsHandler == null) {
            Utils.log(config.prefixes.prefix + Utils.colorize("&cNo NMSHandler loaded! Shutting down"));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        setupCommands();
        setupEvents();

        // TODO: Make WE Implementation nonDebug content
        if (config.debug_mode) {
            weListener = new WEListener();
            weListener.loadWE();
        }

        Bukkit.getPluginManager().callEvent(new CustomBlocksLoadEvent(false));
        Utils.log(config.prefixes.prefix + "Plugin enabled (v." + getDescription().getVersion() + ")");
    }

    @Override
    public void onDisable() {
        if (config.debug_mode) weListener.unloadWE();
        Utils.log(config.prefixes.prefix + "Plugin disabled");
    }

    public void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        configData.loadConfig();
    }

    private void setupEvents() {
        bulkRegisterEvents(new Events(), new InventoryEvents());
        if (config.debug_mode) bulkRegisterEvents(new WEListener());
    }

    private void setupCommands() {
        BaseCommand.REGISTRY.addAll(Arrays.asList(new CBCommand(), new CBMenu()));
    }

    private void bulkRegisterEvents(Listener... listeners) {
        for (Listener listener : listeners)
            Bukkit.getPluginManager().registerEvents(listener, this);
    }
}
