package dev.mcloudtw.rwa;

import dev.mcloudtw.rwa.exception.AlreadyInTeamException;
import dev.mcloudtw.rwa.exception.TeamFullException;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public class Team {
    public enum TeamType {
        RED, WHITE
    }

    private static Location getCoreLocation(TeamType teamType) {
        World world = Main.getInstance().getServer().getWorld("game");
        int coreY = world.getHighestBlockYAt(78, -41);
        return teamType == TeamType.RED ? new Location(world, 78, coreY, -41) : new Location(world, -62, coreY, -41);
    }

    public static Location getLobbyLocation(TeamType teamType) {
        return teamType == TeamType.RED ?
                new Location(Main.gameLobby, 8, -59, 8) :
                new Location(Main.gameLobby, 59, -59, 8);
    }

    public static Team redTeam = new Team(TeamType.RED);
    public static Team whiteTeam = new Team(TeamType.WHITE);

    private static int teamMaxPlayers = 5;
    private TeamType teamType;
    public boolean isCoreDestroyed = false;
    public Location coreLocation;
    public Set<UUID> players = new HashSet<>();

    public Team(TeamType teamType) {
        this.teamType = teamType;
        this.coreLocation = getCoreLocation(teamType);
    }

    public void reConstruct() {
        this.coreLocation = getCoreLocation(teamType);
        isCoreDestroyed = false;
        players.clear();

    }

    public void resetPlayersItemsHealthFood() {
        players.forEach(player -> {
            Player p = Main.getInstance().getServer().getPlayer(player);
            if (p == null) return;
            p.getInventory().clear();
            p.setHealth(20);
            p.setFoodLevel(20);
        });
    }

    public void teleportToCore(Player player) {
        player.teleport(coreLocation.clone().add(0, 2, 0));
    }

    public void teleportAllToCore() {
        players.forEach(player -> {
            Player p = Main.getInstance().getServer().getPlayer(player);
            if (p == null) return;
            teleportToCore(p);
        });
    }

    public void join(Player player) throws TeamFullException, AlreadyInTeamException {
        if (players.size() >= teamMaxPlayers) throw new TeamFullException("team is full");
        if (redTeam.players.contains(player.getUniqueId())) throw new AlreadyInTeamException("already in team");
        if (whiteTeam.players.contains(player.getUniqueId())) throw new AlreadyInTeamException("already in team");
        players.add(player.getUniqueId());
    }

    public boolean leave(Player player) {
        return players.remove(player.getUniqueId());
    }

    public boolean leaveAll() {
        players.clear();
        return true;
    }

    public static TeamType getTeamType(Player player) {
        if (redTeam.players.contains(player.getUniqueId())) return TeamType.RED;
        if (whiteTeam.players.contains(player.getUniqueId())) return TeamType.WHITE;
        return null;
    }
}
