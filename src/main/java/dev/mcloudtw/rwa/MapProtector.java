package dev.mcloudtw.rwa;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.CuboidArea;
import com.bekvon.bukkit.residence.protection.FlagPermissions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class MapProtector {
    public static void removeAllProtectZone() {
        Residence.getInstance().getResidenceManager().getResidences().forEach((name, residence)-> {
            List<String> belongGameResidence = List.of(
                    "shop",
                    "core",
                    "sand"
            );

            boolean isBelongGameResidence = belongGameResidence.stream().anyMatch(name::contains);
            if (!isBelongGameResidence) return;

            residence.remove();

        });
    }

    public static void createProtectZone(Location min, Location max, String name, boolean disableMove, boolean disableFireSpread) {
        Bukkit.getScheduler().runTask(Main.getInstance(), ()->{
            try{
                ClaimedResidence res = ResidenceAPI.createSystemResidence(name, min, max);
                if (disableMove) res.getPermissions().setFlag("move", FlagPermissions.FlagState.FALSE);
                if (disableFireSpread) res.getPermissions().setFlag("fire", FlagPermissions.FlagState.FALSE);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        });
    }
}
