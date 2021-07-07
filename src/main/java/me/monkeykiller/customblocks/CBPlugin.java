package me.monkeykiller.customblocks;

import me.monkeykiller.customblocks.api.CBLoadEvent;
import me.monkeykiller.customblocks.commands.BaseCommand;
import me.monkeykiller.customblocks.commands.CBCommand;
import me.monkeykiller.customblocks.commands.CBMenu;
import me.monkeykiller.customblocks.config.config;
import me.monkeykiller.customblocks.config.configData;
import me.monkeykiller.customblocks.libs.worldedit.WEListener;
import me.monkeykiller.customblocks.listeners.Events;
import me.monkeykiller.customblocks.listeners.InventoryEvents;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public final class CBPlugin extends JavaPlugin {
    public static CBPlugin plugin;
    public static configData configData;

    public PluginDescriptionFile pdfFile = getDescription();

    public PluginManager pm = Bukkit.getPluginManager();
    public WEListener weListener;

    @Override
    public void onEnable() {
        plugin = this;
        configData = new configData();
        weListener = new WEListener();
        configData.setPlugin(this);
        checkNBTApi();

        loadConfig();
        setupCommands();
        setupEvents();

        // TODO: Make WE Implementation nonDebug content
        if (config.debug_mode)
            weListener.loadWE();

        System.out.println(config.prefixes.prefix + "Plugin enabled (v." + pdfFile.getVersion() + ")");
        Bukkit.getPluginManager().callEvent(new CBLoadEvent(false));
    }

    @Override
    public void onDisable() {
        if (config.debug_mode)
            weListener.unloadWE();
        System.out.println(config.prefixes.prefix + "Plugin disabled");
    }

    public void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        configData.loadConfig();
    }

    private void setupEvents() {
        new Events();
        new InventoryEvents();
        new WEListener();
    }

    private void setupCommands() {
        BaseCommand.REGISTRY.addAll(Arrays.asList(new CBCommand(), new CBMenu()));
    }

    private void checkNBTApi() {
        if (pm.getPlugin("NBTAPI") != null) return;
        System.out.println(config.prefixes.warn + "Dependency NBTAPI not found!");
        pm.disablePlugin(this);
    }
}
