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
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public final class CustomBlocksPlugin extends JavaPlugin {
    public static CustomBlocksPlugin plugin;
    public static configData configData;

    public WEListener weListener;

    @Override
    public void onEnable() {
        CustomBlock.clear();
        plugin = this;
        configData = new configData();
        configData.setPlugin(this);
        weListener = new WEListener();

        loadConfig();
        setupCommands();
        setupEvents();

        // TODO: Make WE Implementation nonDebug content
        if (config.debug_mode) weListener.loadWE();

        System.out.println(config.prefixes.prefix + "Plugin enabled (v." + getDescription().getVersion() + ")");
        Bukkit.getPluginManager().callEvent(new CustomBlocksLoadEvent(false));
    }

    @Override
    public void onDisable() {
        if (config.debug_mode) weListener.unloadWE();
        System.out.println(config.prefixes.prefix + "Plugin disabled");
    }

    public void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        configData.loadConfig();
    }

    private void setupEvents() {
        bulkRegisterEvents(new Events(), new InventoryEvents(), new WEListener());
    }

    private void setupCommands() {
        BaseCommand.REGISTRY.addAll(Arrays.asList(new CBCommand(), new CBMenu()));
    }

    private void bulkRegisterEvents(Listener... listeners) {
        for (Listener listener : listeners)
            Bukkit.getPluginManager().registerEvents(listener, this);
    }
}
