package me.monkeykiller.customblocks.utils;

import me.monkeykiller.customblocks.CustomBlock;
import me.monkeykiller.customblocks.CustomBlocks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static me.monkeykiller.customblocks.utils.config.clickable;

@SuppressWarnings("unchecked")
public class configData {
    public static CustomBlocks plugin;

    public void setPlugin(CustomBlocks javaPlugin) {
        plugin = javaPlugin;
    }

    public void loadConfig() {
        try {
            config.prefixes.prefix = CustomBlocks.colorify("&e" + CustomBlocks.pluginInfo.name + " &8> &7");    // TODO: TEMPORAL, WAITING FOR messages.yml
            config.prefixes.warn = CustomBlocks.colorify("&eWarning &8> &7");
            config.cbksGUITitle = CustomBlocks.colorify("&6Custom Blocks GUI");
            config.debug_mode = config().getBoolean("debug_mode");
            if (config.debug_mode)
                System.out.println(config.prefixes.warn + "debug_mode has been enabled.");

            config.cbiMaterial = Material.valueOf(config().getString("cb_item_material"));

            loadClickableMaterials();
            loadBlocks();
            System.out.println(config.prefixes.prefix + "Configuration loaded");

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(config.prefixes.warn + "An exception ocurred while loading config.yml, disabling plugin\n");
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }

    public void reloadConfig() {
        plugin.reloadConfig();  // reload config.yml file
        loadConfig();
        System.out.println(config.prefixes.prefix + "Configuration reloaded");
    }

    public void loadBlocks() {
        try {
            config.blocks = (ArrayList<Map<String, Object>>) config().getList("blocks");
            CustomBlock.REGISTRY.clear();
            assert config.blocks != null && !config.blocks.isEmpty();
            config.blocks.forEach(CustomBlock::deserialize);
            System.out.println(config.prefixes.prefix + "Loaded " + CustomBlock.REGISTRY.size() + " Custom Blocks");
            CustomBlock.REGISTRY.stream()
                    .map(cb -> " - " + cb.id)
                    .forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveBlocks() {
        config().set("blocks", config.blocks);
        plugin.saveConfig();
    }

    public void loadClickableMaterials() {
        clickable.clear();
        Objects.requireNonNull((List<String>) config().getList("clickable_materials")).stream()
                .map(Material::valueOf)
                .forEach(clickable::add);
        System.out.println(config.prefixes.prefix + "Loaded " + clickable.size() + " Clickable Materials");
    }

    public static FileConfiguration config() {
        return plugin.getConfig();
    }

}