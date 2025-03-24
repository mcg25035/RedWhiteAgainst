package dev.mcloudtw.rwa;

import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.CompletableFuture;

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
        if (type == Material.SHEEP_SPAWN_EGG) {
            CompletableFuture.runAsync(()->{
                World game = Bukkit.getWorld("game");
                int sheepSpawnY = game.getHighestBlockYAt(8, -42)+1;
                Location sheepSpawnLocation = new Location(game, 8.5, sheepSpawnY, -41.5);


                Bukkit.getScheduler().runTask(Main.getInstance(), ()->{
                    Sheep sheep = game.spawn(sheepSpawnLocation, Sheep.class);
                    sheep.setColor(DyeColor.YELLOW);
                    sheep.setInvulnerable(true);
                    sheep.setGlowing(true);
                });

            }).thenAccept((ignored)->{
                Main.broadcast("§a黃羊已在中間生成!");
                Main.broadcastSound(Sound.BLOCK_NOTE_BLOCK_BELL, 1, 2);
            });
        }
        if (type == Material.REDSTONE) {
            MapProtector.removeAllProtectZone();
            MapLoader.loadTerrain().thenAccept((ignored)->
                    MapLoader.loadCore().thenAccept((ignored2)->
                            MapLoader.loadShop().thenAccept((ignored3)->
                                    MapLoader.loadSandWall().thenAccept((ignored4)->{
                                    })
                            )
                    )
            );
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void attacked(EntityDamageByEntityEvent event) {
        Entity attacker = event.getDamager();
        Entity victim = event.getEntity();

        if (attacker instanceof Arrow arrow) {
            if (!(arrow.getShooter() instanceof Entity entity)) return;
            attacker = entity;
        }

        if (!(attacker instanceof Player)) return;

        if (victim instanceof Sheep) {
            event.setCancelled(true);
            return;
        }

        if (!(victim instanceof Player)) return;

        Team.TeamType attackerTeam = Team.getTeamType((Player) attacker);
        Team.TeamType victimTeam = Team.getTeamType((Player) victim);

        if (attackerTeam == null || victimTeam == null) return;
        if (attackerTeam == victimTeam) event.setCancelled(true);
    }

    @EventHandler
    public void sheepMove(EntityMoveEvent event) {
        if (Game.gameState != Game.GameState.STARTED) return;
        if (!(event.getEntity() instanceof Sheep sheep)) return;

        int sheepY = sheep.getLocation().getBlockY();
        int coreY = Team.redTeam.coreLocation.getBlockY();

        if (sheepY < coreY) return;

        double distanceRed = Team.redTeam.coreLocation.distance(sheep.getLocation());
        double distanceWhite = Team.whiteTeam.coreLocation.distance(sheep.getLocation());


        if (distanceRed < 3) Team.redTeam.isCoreDestroyed = true;
        if (distanceWhite < 3) Team.whiteTeam.isCoreDestroyed = true;

        Game.sheepMoveDetect();
    }

    @EventHandler
    public void leaveServer(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        Team redTeam = Team.redTeam;
        Team whiteTeam = Team.whiteTeam;

        redTeam.leave(player);
        whiteTeam.leave(player);
    }
}
