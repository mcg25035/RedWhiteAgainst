package dev.mcloudtw.rwa;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.*;
import org.bukkit.Location;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;

public class ResidenceAPI {
    public static ClaimedResidence createSystemResidence(String name, Location loc1, Location loc2) throws NoSuchFieldException, IllegalAccessException {
        CuboidArea newArea = new CuboidArea(loc1, loc2);
        ClaimedResidence newRes = new ClaimedResidence("CONSOLE", UUID.fromString("f78a4d8d-d51b-4b39-98a3-230f2de0c670"), loc1.getWorld().getName());

        ResidencePermissions permissions = newRes.getPermissions();
        permissions.setFlag("destroy", FlagPermissions.FlagState.FALSE);
        permissions.setFlag("place", FlagPermissions.FlagState.FALSE);
        permissions.setFlag("build", FlagPermissions.FlagState.FALSE);
        permissions.setFlag("waterflow", FlagPermissions.FlagState.FALSE);
        permissions.setFlag("lavaflow", FlagPermissions.FlagState.FALSE);
        permissions.setFlag("fire", FlagPermissions.FlagState.FALSE);
        permissions.setFlag("tnt", FlagPermissions.FlagState.FALSE);
        permissions.setFlag("tp", FlagPermissions.FlagState.FALSE);
        permissions.setFlag("pvp", FlagPermissions.FlagState.TRUE);
        permissions.setFlag("move", FlagPermissions.FlagState.TRUE);
        permissions.setFlag("piston", FlagPermissions.FlagState.FALSE);
        permissions.setFlag("pistonprotection", FlagPermissions.FlagState.TRUE);
        newRes.setEnterMessage("");
        newRes.setLeaveMessage("");
        newRes.setName(name);
        newRes.setCreateTime();
        ResidenceManager rm = Residence.getInstance().getResidenceManager();

        // newRes.areas.put("main", newArea);
        {
            rm.removeChunkList(newRes);
            Field field = newRes.getClass().getDeclaredField("areas");
            field.setAccessible(true);
            Map<String, CuboidArea> areas = (Map<String, CuboidArea>) field.get(newRes);
            areas.put("main", newArea);
            rm.calculateChunks(newRes);
        }
        // #end


        // Residence.getInstance().getResidenceManager().residences.put(name.toLowerCase(), newRes);
        {
            Field field = rm.getClass().getDeclaredField("residences");
            field.setAccessible(true);
            Map<String, ClaimedResidence> residences = (Map<String, ClaimedResidence>) field.get(rm);
            residences.put(name.toLowerCase(), newRes);
            rm.calculateChunks(newRes);
        }
        // #end

        return newRes;
    }
}
