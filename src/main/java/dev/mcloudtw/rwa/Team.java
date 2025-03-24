package dev.mcloudtw.rwa;

import dev.mcloudtw.rwa.exception.TeamFullException;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class Team {
    public enum TeamType {
        RED, WHITE
    }

    private static Location getCoreLocation(TeamType teamType) {
        World world = Main.getInstance().getServer().getWorld("game");
        int coreY = world.getHighestBlockYAt(78, -41);
        return teamType == TeamType.RED ? new Location(world, 78, coreY, -41) : new Location(world, -62, coreY, -41);
    }

    public static Team redTeam = new Team(TeamType.RED);
    public static Team whiteTeam = new Team(TeamType.WHITE);

    private static int teamMaxPlayers = 5;
    private TeamType teamType;
    public boolean isCoreDestroyed = false;
    public Location coreLocation;
    public List<UUID> players;

    public Team(TeamType teamType) {
        this.teamType = teamType;
        this.coreLocation = getCoreLocation(teamType);
    }

    public void join(Player player) throws TeamFullException {
        if (players.size() >= teamMaxPlayers) throw new TeamFullException("team is full");
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
