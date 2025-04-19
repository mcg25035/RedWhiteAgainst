package dev.mcloudtw.rwa;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Game {
    private static int MIN_PLAYERS_PER_TEAM = 1;
    private static int START_COUNTDOWN_PLAYERS_PER_TEAM = 1;
    private static int countdownTime;
    private static int gameTime = 0;
    private static boolean prepareTaskStarted = false;

    public static GameState gameState = GameState.PREPARING;

    public static enum GameState {
        WAITING, STARTING, STARTED, ENDING, PREPARING
    }

    public static void tickSecondNonStarted() {
        if (!List.of(GameState.WAITING, GameState.STARTING).contains(gameState)) return;

        int redTeamPlayersCount = Team.redTeam.players.size();
        int whiteTeamPlayersCount = Team.whiteTeam.players.size();

        Main.broadcastActionBar("§c紅隊 §7(§e"+redTeamPlayersCount+"§7)  |  §7(§e"+whiteTeamPlayersCount+"§7) §f白隊");
        Team.redTeam.players.removeIf((uuid)->{
            if (Bukkit.getPlayer(uuid) == null) return true;
            if (!Bukkit.getPlayer(uuid).isValid()) return true;
            if (!Bukkit.getPlayer(uuid).isOnline()) return true;
            return false;
        });

        Team.whiteTeam.players.removeIf((uuid)->{
            if (Bukkit.getPlayer(uuid) == null) return true;
            if (!Bukkit.getPlayer(uuid).isValid()) return true;
            if (!Bukkit.getPlayer(uuid).isOnline()) return true;
            return false;
        });

        String redTeamPlayers = "";
        String whiteTeamPlayers = "";

        for (UUID uuid : Team.redTeam.players) {
            Player player = Bukkit.getPlayer(uuid);
            redTeamPlayers += player.getName() + " ";
        }

        for (UUID uuid : Team.whiteTeam.players) {
            Player player = Bukkit.getPlayer(uuid);
            whiteTeamPlayers += player.getName() + " ";
        }

        if (gameState == GameState.WAITING && redTeamPlayersCount >= START_COUNTDOWN_PLAYERS_PER_TEAM && whiteTeamPlayersCount >= START_COUNTDOWN_PLAYERS_PER_TEAM) {
            gameState = GameState.STARTING;
            countdownTime = 30;
            Main.broadcast("§a人數滿足，遊戲即將開始!");
            return;
        }

        if (gameState == GameState.STARTING) {
            if (redTeamPlayersCount < MIN_PLAYERS_PER_TEAM || whiteTeamPlayersCount < MIN_PLAYERS_PER_TEAM) {
                Main.broadcast("§c人數不足，遊戲取消倒數，進入等待狀態!");
                gameState = GameState.WAITING;
                return;
            }

            Main.broadcast("§a遊戲在 §e"+countdownTime+" §a秒後開始!");

            countdownTime--;
            if (countdownTime <= 0) {
                gameState = GameState.STARTED;
                Team.redTeam.resetPlayersItemsHealthFood();
                Team.whiteTeam.resetPlayersItemsHealthFood();
                Main.broadcast("§6遊戲開始!");
                Team.redTeam.teleportAllToCore();
                Team.whiteTeam.teleportAllToCore();
            }
        }
    }

    public static void tickSecondStarted() {
        if (gameState != GameState.STARTED) return;
        if (Team.redTeam.isSheepNear) Main.broadcast("§c羊正在靠近紅隊核心！");
        if (Team.whiteTeam.isSheepNear) Main.broadcast("§f羊正在靠近白隊核心！");
        Team.redTeam.isSheepNear = false;
        Team.whiteTeam.isSheepNear = false;

        gameTime++;

        Main.broadcastActionBar("§a遊戲時間: §e"+gameTime+" §a秒");

        int sandWallFallTime = 60*10;

        if (sandWallFallTime-60 < gameTime && gameTime < sandWallFallTime) {
            if (List.of(0, 30, 45, 55, 56, 57, 58, 59).contains(gameTime % 60)) {
                Main.broadcastSound(org.bukkit.Sound.BLOCK_NOTE_BLOCK_BELL, 1, 2);
                Main.broadcast("§a沙牆將於 §e"+(sandWallFallTime-gameTime)+" §a秒後倒塌!");
            }
        }

        if (gameTime == sandWallFallTime) {
            MapLoader.loadSandWallFall().thenAccept((ignored)->{
                Main.broadcastSound(Sound.ENTITY_GENERIC_EXPLODE, 1, 1F);
                Main.broadcast("§c沙牆已倒塌!");
                Main.broadcast("§e黃羊將在 §630 §e秒後生成!");
            });
        }

        if (gameTime == sandWallFallTime+30) {
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
    }

    public static void sheepMoveDetect() {
        World game = Bukkit.getWorld("game");
        if (Team.redTeam.isCoreDestroyed) {
            game.getEntities().forEach((entity)->{
                if (entity.getType() == EntityType.PLAYER) return;
                entity.remove();
            });
            Main.broadcastTitle("§f白隊§6獲勝!", "§6遊戲結束!");
            Main.broadcastSound(Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1, 1);
            gameState = GameState.ENDING;
            return;
        }

        if (Team.whiteTeam.isCoreDestroyed) {
            game.getEntities().forEach((entity)->{
                if (entity.getType() == EntityType.PLAYER) return;
                entity.remove();
            });
            Main.broadcastTitle("§c紅隊§6獲勝!", "§6遊戲結束!");
            Main.broadcastSound(Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1, 1);
            gameState = GameState.ENDING;
            return;
        }
    }

    public static void tickEnd() {
        if (gameState != GameState.ENDING) return;

        Team.redTeam.leaveAll();
        Team.whiteTeam.leaveAll();
        Bukkit.getOnlinePlayers().forEach(player -> player.teleport(Main.lobby));
        prepareTaskStarted = false;

        gameState = GameState.PREPARING;



    }

    public static void tickSecondPreparing() {
        if (gameState != GameState.PREPARING) return;

        int redTeamPlayersCount = Team.redTeam.players.size();
        int whiteTeamPlayersCount = Team.whiteTeam.players.size();

        Main.broadcastActionBar("§c紅隊 §7(§e"+redTeamPlayersCount+"§7)  |  §7(§e"+whiteTeamPlayersCount+"§7) §f白隊");

        if (prepareTaskStarted) return;
        prepareTaskStarted = true;

        Team.redTeam.coreLocation.getChunk().setForceLoaded(true);
        Team.whiteTeam.coreLocation.getChunk().setForceLoaded(true);

        World game = Bukkit.getWorld("game");

        game.setGameRule(GameRule.KEEP_INVENTORY, true);
        game.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        game.setDifficulty(Difficulty.EASY);

        Team.redTeam.coreLocation.getChunk().setForceLoaded(false);
        Team.whiteTeam.coreLocation.getChunk().setForceLoaded(false);



        MapProtector.removeAllProtectZone();
        MapLoader.loadTerrain().thenAccept((ignored)->
                MapLoader.loadCore().thenAccept((ignored2)->
                        MapLoader.loadShop().thenAccept((ignored3)->
                                MapLoader.loadSandWall().thenAccept((ignored4)->{
                                    gameState = GameState.WAITING;
                                    prepareTaskStarted = false;
                                    Team.redTeam.reConstruct();
                                    Team.whiteTeam.reConstruct();
                                    gameTime = 0;
                                    Main.broadcast("§a遊戲重置完成!");
                                    Main.broadcast("§a使用.join §cred §a加入§c紅隊");
                                    Main.broadcast("§a使用.join §fwhite §a加入§f白隊");

                                })
                        )
                )
        );


    }

    public static void tickSecond() {
        tickSecondNonStarted();
        tickSecondStarted();
        tickSecondPreparing();
        tickEnd();
    }
}
