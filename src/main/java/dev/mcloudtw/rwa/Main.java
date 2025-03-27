package dev.mcloudtw.rwa;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    public static Main instance;
    private LocationFinder locationFinder;
    private MapLoader mapLoader;
    public static World gameLobby = Bukkit.getWorld("game_lobby");
    public static Location lobby = new Location(gameLobby, 32, -60, 60);

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

    public static void broadcastTeam(Team.TeamType teamType, String message) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            String teamName = teamType == Team.TeamType.RED ? "§c紅隊" : "§f白隊";

            if (Team.getTeamType(player) != teamType) return;
            player.sendMessage("§e["+teamName+"§e] §f" + message);
        });
    }


    public static void broadcastActionBar(String message) {
        Bukkit.getOnlinePlayers().forEach(player -> player.sendActionBar(message));
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        Bukkit.getPluginManager().registerEvents(new Events(), this);
        Bukkit.getScheduler().runTaskTimer(this, Game::tickSecond, 0, 20);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
