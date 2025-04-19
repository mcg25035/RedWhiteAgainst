package dev.mcloudtw.rwa;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class ScoreboardManager {
    public static Objective redTeam;
    public static Scoreboard redScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    public static Objective whiteTeam;
    public static Scoreboard whiteScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
//    public static Objective spectators;

    public static void clearObj(Objective obj) {
        Scoreboard scoreboard = obj.getScoreboard();
        scoreboard.getEntries().forEach((entire)-> obj.getScore(entire).resetScore());
    }

    public static void init() {
        redTeam = redScoreboard.getObjective("redTeam");
        if (redTeam == null) {
            redTeam = redScoreboard.registerNewObjective("redTeam", Criteria.DUMMY, "§c紅隊");
            redTeam.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        whiteTeam = whiteScoreboard.getObjective("whiteTeam");
        if (whiteTeam == null) {
            whiteTeam = whiteScoreboard.registerNewObjective("whiteTeam", Criteria.DUMMY, "§f白隊");
            whiteTeam.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        clearObj(redTeam);
        clearObj(whiteTeam);
        Bukkit.getScheduler().runTaskTimer(Main.getInstance(), ()-> {
            ScoreboardManager.updateTeamScoreboard(Team.TeamType.RED);
            ScoreboardManager.updateTeamScoreboard(Team.TeamType.WHITE);
        }, 0, 3);
    }

    public static void updateTeamScoreboard(Team.TeamType teamType) {
        AtomicInteger index = new AtomicInteger();
        Team team = teamType == Team.TeamType.RED ? Team.redTeam : Team.whiteTeam;
        Objective obj = teamType == Team.TeamType.RED ? redTeam : whiteTeam;
        Bukkit.getOnlinePlayers().forEach((player)-> {
            if (Team.getTeamType(player) == null) {
                player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            }

        });
        clearObj(obj);
        team.players.forEach((player)-> {

            obj.getScore("§a"+Bukkit.getOfflinePlayer(player).getName()).setScore(index.getAndIncrement());

            Player onlinePlayer = Bukkit.getPlayer(player);
            if (onlinePlayer == null) return;
            if (!onlinePlayer.isOnline() || !onlinePlayer.isValid()) return;
            onlinePlayer.setScoreboard(teamType == Team.TeamType.RED ? redScoreboard : whiteScoreboard);
        });
    }
}
