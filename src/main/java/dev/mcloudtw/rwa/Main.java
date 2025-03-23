package dev.mcloudtw.rwa;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    public static Main instance;
    private LocationFinder locationFinder;
    private MapLoader mapLoader;

    public static Main getInstance() {
        return Main.instance;
    }

    public static void broadcastTitle(String title, String subtitle) {
        Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(title, subtitle));
    }

    public static void broadcastSound(Sound sound, float volume, float pitch) {
        Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), sound, volume, pitch));
    }

    public static void broadcast(String message) {
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(message));
    }

    public static void broadcastActionBar(String message) {
        Bukkit.getOnlinePlayers().forEach(player -> player.sendActionBar(message));
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
