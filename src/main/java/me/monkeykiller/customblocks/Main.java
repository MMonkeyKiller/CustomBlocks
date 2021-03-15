package me.monkeykiller.customblocks;

import com.sk89q.worldedit.WorldEdit;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;
import me.monkeykiller.customblocks.commands.CBMenu;
import me.monkeykiller.customblocks.commands.Commands;
import me.monkeykiller.customblocks.commands.CustomBlocks;
import me.monkeykiller.customblocks.libs.worldedit.Parser;
import me.monkeykiller.customblocks.libs.worldedit.WEListener;
import me.monkeykiller.customblocks.listeners.Events;
import me.monkeykiller.customblocks.listeners.InventoryEvents;
import me.monkeykiller.customblocks.utils.NMSUtils;
import me.monkeykiller.customblocks.utils.configData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.logging.Level;

public final class Main extends JavaPlugin {
    public static Main plugin;
    public static configData configData;

    public static Commodore commodore;

    public PluginDescriptionFile pdfFile = getDescription();
    public PluginManager pm = Bukkit.getPluginManager();
    WEListener weListener;

    @Override
    public void onEnable() {
        plugin = this;
        configData = new configData(this);
        if (!checkNBTApi()) return;
        loadConfig();
        setupCommands();
        try {
            setupCommodore();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setupEvents();

        if (configData.debug_mode) {
            /*
            TEMPORAL
            WORLDEDIT IMPLEMENTATION IN DEVELOPMENT
            */
            this.weListener = new WEListener();
            loadWE();
        }
        Bukkit.getLogger().info(configData.prefix + "Plugin enabled (v." + pdfFile.getVersion() + ")");
    }

    @Override
    public void onDisable() {
        if (configData.debug_mode)
            unloadWE();
        Bukkit.getLogger().info(configData.prefix + "Plugin disabled");
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

    private void setupCommodore() throws IOException {
        /*List<PluginCommand> cmds = new ArrayList<>();
        Commands.REGISTRY.forEach(CBCommand -> cmds.add(CBCommand.parseCommand()));*/
        if (!CommodoreProvider.isSupported()) {
            Bukkit.getLogger().info(configData.prefix + "Commodore is not supported by Server, disabling TabCompleter feature!");
            //Commands.REGISTRY.forEach(cbc -> cbc.parseCommand().setTabCompleter(cbc));
            return;
        }

        Bukkit.getLogger().info(configData.prefix + "Using Commodore TabCompleter");
        commodore = CommodoreProvider.getCommodore(this);
        Commands.REGISTRY.forEach(a -> NMSUtils.registerCompletions(commodore, a.parseCommand()));
    }

    private void setupCommands() {
        Commands.REGISTRY.add(new CustomBlocks());
        Commands.REGISTRY.add(new CBMenu());
    }

    private boolean checkNBTApi() {
        if (pm.getPlugin("NBTAPI") != null) return true;
        getLogger().log(Level.SEVERE, "Dependency NBTAPI not found!");
        pm.disablePlugin(this);
        return false;
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
