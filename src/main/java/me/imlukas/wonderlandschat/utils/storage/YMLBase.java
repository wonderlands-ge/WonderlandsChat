/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.InvalidConfigurationException
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.plugin.java.JavaPlugin
 */
package me.imlukas.wonderlandschat.utils.storage;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class YMLBase {
    protected File file;
    private final boolean existsOnSource;
    private final JavaPlugin plugin;
    private final FileConfiguration configuration;

    public YMLBase(JavaPlugin plugin, String name) {
        this(plugin, new File(plugin.getDataFolder(), name), true);
    }

    public YMLBase(JavaPlugin plugin, File file, boolean existsOnSource) {
        this.plugin = plugin;
        this.file = file;
        this.existsOnSource = existsOnSource;
        this.configuration = this.loadConfiguration();
    }

    public void save() {
        try {
            this.configuration.save(this.file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private FileConfiguration loadConfiguration() {
        YamlConfiguration cfg = new YamlConfiguration();
        if (!this.file.exists()) {
            this.file.getParentFile().mkdirs();
            if (this.existsOnSource) {
                this.plugin.saveResource(this.file.getAbsolutePath().replace(this.plugin.getDataFolder().getAbsolutePath() + File.separator, ""), false);
            } else {
                try {
                    this.file.createNewFile();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            cfg.load(this.file);
        }
        catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return cfg;
    }

    public File getFile() {
        return this.file;
    }

    public FileConfiguration getConfiguration() {
        return this.configuration;
    }
}

