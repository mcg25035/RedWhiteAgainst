package dev.mcloudtw.rwa;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class Events implements Listener {
    @EventHandler
    public void trigger(PlayerDropItemEvent event) {
        Material type = event.getItemDrop().getItemStack().getType();
        if (type == Material.DIAMOND) {
            MapLoader.loadCore();
            MapLoader.loadShop();
        }
        if (type == Material.EMERALD) MapLoader.loadTerrain();
        if (type == Material.GOLD_INGOT) MapLoader.loadSandWallFall();
        if (type == Material.SAND) MapLoader.loadSandWall();

        event.setCancelled(true);
    }
}
