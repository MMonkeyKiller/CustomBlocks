package me.monkeykiller.customblocks;

import com.sk89q.worldedit.WorldEdit;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;
import me.monkeykiller.customblocks.commands.BaseCommand;
import me.monkeykiller.customblocks.commands.CBCommand;
import me.monkeykiller.customblocks.commands.CBMenu;
import me.monkeykiller.customblocks.config.config;
import me.monkeykiller.customblocks.config.configData;
import me.monkeykiller.customblocks.libs.worldedit.Parser;
import me.monkeykiller.customblocks.libs.worldedit.WEListener;
import me.monkeykiller.customblocks.listeners.Events;
import me.monkeykiller.customblocks.listeners.InventoryEvents;
import me.monkeykiller.customblocks.utils.BrigadierUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class CBPlugin extends JavaPlugin {
    public static CBPlugin plugin;
    public static configData configData;
    public static Commodore commodore;

    public PluginDescriptionFile pdfFile = getDescription();

    public static class pluginInfo {
        public static String name = CBPlugin.plugin.pdfFile.getPrefix() != null ?
                CBPlugin.plugin.pdfFile.getPrefix() :
                CBPlugin.plugin.pdfFile.getName();
        public static String version = CBPlugin.plugin.pdfFile.getVersion();
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
        BrigadierUtils.registerCompletions(Objects.requireNonNull(BaseCommand.getCommand("customblocks"))
                .parseCommand());
    }

    private void setupCommands() {
        BaseCommand.REGISTRY.add(new CBCommand());
        BaseCommand.REGISTRY.add(new CBMenu());
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
