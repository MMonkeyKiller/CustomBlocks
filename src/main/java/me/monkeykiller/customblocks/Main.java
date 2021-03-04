package me.monkeykiller.customblocks;

import org.bukkit.Bukkit;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    PluginDescriptionFile pdfFile = this.getDescription();

    @Override
    public void onEnable() {
        getLogger().info("Plugin enabled (v." + pdfFile.getVersion() + ")");
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new Events(this), this);

        for (int i = 1; i <= 16; i++)
            new CustomBlock("survitroll:cloth_" + i, Instrument.BANJO, new Note(i), false);

        new CustomBlock("survitroll:rock", Instrument.BANJO, new Note(17), false);
        new CustomBlock("survitroll:mossy_rock", Instrument.BANJO, new Note(18), false);

        new CustomBlock("survitroll:cut_copper", Instrument.BASS_DRUM, new Note(1), false);
        new CustomBlock("survitroll:exposed_cut_copper", Instrument.BASS_DRUM, new Note(3), false);
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin disabled");
    }
}
