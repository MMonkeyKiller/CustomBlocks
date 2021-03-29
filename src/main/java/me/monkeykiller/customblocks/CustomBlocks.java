package me.monkeykiller.customblocks;

import com.sk89q.worldedit.WorldEdit;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;
import me.monkeykiller.customblocks.commands.CBMenu;
import me.monkeykiller.customblocks.commands.Commands;
import me.monkeykiller.customblocks.libs.worldedit.Parser;
import me.monkeykiller.customblocks.libs.worldedit.WEListener;
import me.monkeykiller.customblocks.listeners.Events;
import me.monkeykiller.customblocks.listeners.InventoryEvents;
import me.monkeykiller.customblocks.utils.brigadierUtils;
import me.monkeykiller.customblocks.utils.config;
import me.monkeykiller.customblocks.utils.configData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class CustomBlocks extends JavaPlugin {
    public static CustomBlocks plugin;
    public static configData configData;
    public static Commodore commodore;

    public PluginDescriptionFile pdfFile = getDescription();

    public static class pluginInfo {
        public static String name = CustomBlocks.plugin.pdfFile.getPrefix() != null ?
                CustomBlocks.plugin.pdfFile.getPrefix() :
                CustomBlocks.plugin.pdfFile.getName();
        public static String version = CustomBlocks.plugin.pdfFile.getVersion();
    }

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
        setupCommodore();
        setupEvents();

        // TODO: Make WE Implementation nonDebug content
        if (config.debug_mode) loadWE();

        System.out.println(config.prefixes.prefix + "Plugin enabled (v." + pluginInfo.version + ")");
    }

    @Override
    public void onDisable() {
        if (config.debug_mode) unloadWE();
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

    private void setupCommodore() {
        if (!CommodoreProvider.isSupported()) {
            System.out.println(config.prefixes.prefix + "Commodore is not supported by Server, disabling TabCompleter feature!");
            return;
        }

        System.out.println(config.prefixes.prefix + "Using Commodore TabCompleter");
        commodore = CommodoreProvider.getCommodore(this);
        /*Commands.REGISTRY.stream()
                .map(CBCommand::parseCommand)
                .forEach(brigadierUtils::registerCompletions);*/
        brigadierUtils.registerCompletions(Objects.requireNonNull(Commands.getCommand("CustomBlocks"))
                .parseCommand());
    }

    private void setupCommands() {
        Commands.REGISTRY.add(new me.monkeykiller.customblocks.commands.CustomBlocks());
        Commands.REGISTRY.add(new CBMenu());
    }

    private void checkNBTApi() {
        if (pm.getPlugin("NBTAPI") != null) return;
        System.out.println(config.prefixes.warn + "Dependency NBTAPI not found!");
        pm.disablePlugin(this);
    }

    public static String colorify(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }


    @SuppressWarnings("unchecked")
    public void loadWE() {
        WorldEdit.getInstance().getEventBus().register(weListener);
        WorldEdit.getInstance().getBlockFactory().register(new Parser(WorldEdit.getInstance()));
    }

    public void unloadWE() {
        WorldEdit.getInstance().getEventBus().unregister(weListener);
    }
}
