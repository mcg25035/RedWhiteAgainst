package dev.mcloudtw.rwa;

import com.fastasyncworldedit.core.FaweAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    public static Main instance;
    private LocationFinder locationFinder;
    private MapLoader mapLoader;

    public static Main getInstance() {
        return Main.instance;
    }

    public static void logToAnyPlayer(String message) {
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(message));
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        Bukkit.getPluginManager().registerEvents(new Events(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
