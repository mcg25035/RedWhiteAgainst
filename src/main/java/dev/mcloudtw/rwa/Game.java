package dev.mcloudtw.rwa;

import org.bukkit.Bukkit;
import org.bukkit.Sound;

import java.util.List;

public class Game {
    public static boolean isGameStarted = false;
    private static int MIN_PLAYERS_PER_TEAM = 3;
    private static int START_COUNTDOWN_PLAYERS_PER_TEAM = 5;
    private static int countdownTime;
    private static int gameTime = 0;
    private static boolean prepareTaskStarted = false;

    public static GameState gameState = GameState.WAITING;

    public static enum GameState {
        WAITING, STARTING, STARTED, ENDING, PREPARING
    }

    public static void tickSecondNonStarted() {
        if (!List.of(GameState.WAITING, GameState.STARTING).contains(gameState)) return;

        int redTeamPlayersCount = Team.redTeam.players.size();
        int whiteTeamPlayersCount = Team.whiteTeam.players.size();

        if (gameState == GameState.WAITING && redTeamPlayersCount >= START_COUNTDOWN_PLAYERS_PER_TEAM && whiteTeamPlayersCount >= START_COUNTDOWN_PLAYERS_PER_TEAM) {
            gameState = GameState.STARTING;
            countdownTime = 30;
            Main.broadcast("§a遊戲即將開始!");
            return;
        }

        if (gameState == GameState.STARTING) {
            if (redTeamPlayersCount < MIN_PLAYERS_PER_TEAM || whiteTeamPlayersCount < MIN_PLAYERS_PER_TEAM) {
                gameState = GameState.WAITING;
                return;
            }

            Main.broadcast("§a遊戲在 §e"+countdownTime+" §a秒後開始!");

            countdownTime--;
            if (countdownTime <= 0) {
                gameState = GameState.STARTED;
                Main.broadcast("§6遊戲開始!");
            }
        }
    }

    public static void tickSecondStarted() {
        if (gameState != GameState.STARTED) return;

        gameTime++;

        Main.broadcastActionBar("§a遊戲時間: §e"+gameTime+" §a秒");

        if (60*4 < gameTime && gameTime < 60*5) {
            if (List.of(0, 30, 45, 55, 56, 57, 58, 59).contains(gameTime % 60)) {
                Main.broadcastSound(org.bukkit.Sound.BLOCK_NOTE_BLOCK_BELL, 1, 2);
                Main.broadcast("§a沙牆將於 §e"+(60*5-gameTime)+" §a秒後倒塌!");
            }
        }

        if (gameTime == 60*5) {
            MapLoader.loadSandWallFall().thenAccept((ignored)->{
                Main.broadcastSound(Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                Main.broadcast("§c沙牆已倒塌!");
            });
        }
    }

    public static void sheepMoveDetect() {
        if (Team.redTeam.isCoreDestroyed) {
            Main.broadcastTitle("§f白隊§6獲勝!", "§6遊戲結束!");
            Main.broadcastSound(Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1, 1);
            gameState = GameState.ENDING;
            return;
        }

        if (Team.whiteTeam.isCoreDestroyed) {
            Main.broadcastTitle("§c紅隊§6獲勝!", "§6遊戲結束!");
            Main.broadcastSound(Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1, 1);
            gameState = GameState.ENDING;
            return;
        }
    }

    public static void tickSecondPreparing() {
        if (gameState != GameState.PREPARING) return;

        if (prepareTaskStarted) return;
        prepareTaskStarted = true;

        Team.redTeam.leaveAll();
        Team.whiteTeam.leaveAll();

        MapLoader.loadTerrain().thenAccept((ignored)->
                MapLoader.loadCore().thenAccept((ignored2)->
                        MapLoader.loadShop().thenAccept((ignored3)->
                                MapLoader.loadSandWall().thenAccept((ignored4)->{
                                    gameState = GameState.WAITING;
                                    prepareTaskStarted = false;
                                })
                        )
                )
        );


    }

    public static void tickSecond() {
        tickSecondNonStarted();
        tickSecondStarted();
        tickSecondPreparing();
    }
}
