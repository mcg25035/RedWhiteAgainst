package dev.mcloudtw.rwa;

import dev.mcloudtw.rwa.exception.AlreadyInTeamException;
import dev.mcloudtw.rwa.exception.TeamFullException;
import io.papermc.paper.event.entity.EntityMoveEvent;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

public class Events implements Listener {
    @EventHandler
    public void throwItem(PlayerDropItemEvent event) {
//        Material type = event.getItemDrop().getItemStack().getType();
        if (!event.getPlayer().getWorld().getName().equals("game_lobby")) return;
//        if (type == Material.DIAMOND) {
//            MapLoader.loadCore();
//            MapLoader.loadShop();
//        }
//        if (type == Material.EMERALD) MapLoader.loadTerrain();
//        if (type == Material.GOLD_INGOT) MapLoader.loadSandWallFall();
//        if (type == Material.SAND) MapLoader.loadSandWall();
//        if (type == Material.SHEEP_SPAWN_EGG) {
//            CompletableFuture.runAsync(()->{
//                World game = Bukkit.getWorld("game");
//                int sheepSpawnY = game.getHighestBlockYAt(8, -42)+1;
//                Location sheepSpawnLocation = new Location(game, 8.5, sheepSpawnY, -41.5);
//
//
//                Bukkit.getScheduler().runTask(Main.getInstance(), ()->{
//                    Sheep sheep = game.spawn(sheepSpawnLocation, Sheep.class);
//                    sheep.setColor(DyeColor.YELLOW);
//                    sheep.setInvulnerable(true);
//                    sheep.setGlowing(true);
//                });
//
//            }).thenAccept((ignored)->{
//                Main.broadcast("§a黃羊已在中間生成!");
//                Main.broadcastSound(Sound.BLOCK_NOTE_BLOCK_BELL, 1, 2);
//            });
//        }
//        if (type == Material.REDSTONE) {
//            MapProtector.removeAllProtectZone();
//            MapLoader.loadTerrain().thenAccept((ignored)->
//                    MapLoader.loadCore().thenAccept((ignored2)->
//                            MapLoader.loadShop().thenAccept((ignored3)->
//                                    MapLoader.loadSandWall().thenAccept((ignored4)->{
//                                        Main.broadcast("地形準備完成");
//                                    })
//                            )
//                    )
//            );
//        }

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
    public void chat(AsyncChatEvent event) {
        PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();
        String message = serializer.serialize(event.message());
        boolean isCommand = message.startsWith(".");

        Consumer<String> sendMsg = (msg)->{
            event.getPlayer().sendMessage(msg);
            event.setCancelled(true);
        };
        Consumer<String> broadcastMsg = (msg)->{
            Main.broadcast(msg);
            event.setCancelled(true);
        };

        if (message.startsWith(".g ")) {
            message = message.replace(".g ", "");
            broadcastMsg.accept("§e[§6全域-"+event.getPlayer().getName()+"§e] §f"+message);
            return;
        }

        if (Game.gameState != Game.GameState.WAITING && isCommand) {
            sendMsg.accept("§e[§c錯誤§e] §c遊戲正在開始或已經開始，無法使用指令");
            return;
        }
        if (message.startsWith(".leave")) {
            Team redTeam = Team.redTeam;
            Team whiteTeam = Team.whiteTeam;
            Player player = event.getPlayer();


            if (redTeam.leave(player)) {
                sendMsg.accept("§e[§a隊伍§e] §a成功離開隊伍");
                Main.broadcastTeam(Team.TeamType.RED, "§7"+player.getName() + " 離開了隊伍");
                return;
            }
            if (whiteTeam.leave(player)) {
                sendMsg.accept("§e[§a隊伍§e] §a成功離開隊伍");
                Main.broadcastTeam(Team.TeamType.WHITE, "§7"+player.getName() + " 離開了隊伍");
                return;
            }

            sendMsg.accept("§e[§c錯誤§e] §c你不在任何隊伍中");
            return;
        }
        if (message.startsWith(".join ")) {
            message = message.replace(".join ", "");
            message = message.trim();
            Team.TeamType teamType;
            try{
                teamType = Team.TeamType.valueOf(message.toUpperCase());
            }
            catch (IllegalArgumentException e) {
                sendMsg.accept("§e[§c錯誤§e] §c請輸入正確的隊伍名稱，使用方法：.join red/white");
                return;
            }

            try{
                Team team = teamType == Team.TeamType.RED ? Team.redTeam : Team.whiteTeam;
                team.join(event.getPlayer());
                sendMsg.accept("§e[§a隊伍§e] §a成功加入隊伍");
                Main.broadcastTeam(teamType, "§7"+event.getPlayer().getName() + " 加入了隊伍");
                return;
            }
            catch (TeamFullException e) {
                sendMsg.accept("§e[§c錯誤§e] §c隊伍已滿");
                return;
            }
            catch (AlreadyInTeamException e) {
                sendMsg.accept("§e[§c錯誤§e] §c你已經在隊伍中");
                return;
            }
        }

        Team.TeamType teamType = Team.getTeamType(event.getPlayer());
        if (teamType == null) {
            broadcastMsg.accept("§e[§6全域-"+event.getPlayer().getName()+"§e] §f"+message);
            return;
        }
        Main.broadcastTeam(teamType, "§e"+event.getPlayer().getName() + " §r" + message);
        event.setCancelled(true);
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
        if (Game.gameState != Game.GameState.STARTED) return;

        Player player = event.getPlayer();
        Team redTeam = Team.redTeam;
        Team whiteTeam = Team.whiteTeam;

        redTeam.leave(player);
        whiteTeam.leave(player);
    }

    @EventHandler
    public void joinServer(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), ()->{
            if (!event.getPlayer().isValid()) return;
            if (!event.getPlayer().isOnline()) return;
            Player player = event.getPlayer();
            player.teleport(Main.lobby);
        }, 20);
    }

    @EventHandler
    public void death(PlayerDeathEvent event) {
        Player player = event.getEntity();
        event.setKeepInventory(true);
        player.spigot().respawn();

        Function<BukkitTask, Boolean> checkPlayer = (task) -> {
            if (!player.isValid()) {
                if (task != null) task.cancel();
                return false;
            }

            if (!player.isOnline()) {
                if (task != null) task.cancel();
                return false;
            }

            return true;
        };
        Function<BukkitTask, Team.TeamType> getTeamType = (task) -> {
            if (!checkPlayer.apply(task)) return null;
            Team.TeamType teamType = Team.getTeamType(player);
            if (teamType == null) {
                player.teleport(Main.lobby);
                if (task != null) task.cancel();
                return null;
            }
            return teamType;
        };
        Function<BukkitTask, Boolean> playerTpTeamLobby = (task)->{
            if (!checkPlayer.apply(null)) return false;
            Team.TeamType teamType = getTeamType.apply(null);
            if (teamType == null) return false;

            player.teleport(Team.getLobbyLocation(teamType));
            return true;
        };

        if (!checkPlayer.apply(null)) return;
        if (!playerTpTeamLobby.apply(null)) return;

        Consumer<Void> respawnAfter5Seconds = (ignored)-> {
            AtomicInteger countdownTime = new AtomicInteger(5);
            Bukkit.getScheduler().runTaskTimer(Main.getInstance(), (task) -> {
                if (Game.gameState != Game.GameState.STARTED) {
                    task.cancel();
                    return;
                }

                if (!checkPlayer.apply(task)) return;
                Team.TeamType teamType = getTeamType.apply(task);
                if (teamType == null) return;

                if (countdownTime.get() <= 0) {
                    Team team = teamType == Team.TeamType.RED ? Team.redTeam : Team.whiteTeam;
                    player.teleport(team.coreLocation.clone().add(0, 2, 0));
                    task.cancel();
                    return;
                }

                countdownTime.getAndDecrement();
                player.sendActionBar("§a重生倒數: §e" + countdownTime.get() + " §a秒");
            }, 0, 20);
        };
        respawnAfter5Seconds.accept(null);
    }
}
