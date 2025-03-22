package dev.mcloudtw.rwa;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.Material;

import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;

public class LocationFinder {

    public static Location findAcceptedLocation(World world) {
        Random random = new Random();
        int maxTries = 100;
        int tries = 0;

        BiFunction<Integer, Integer, Boolean> isAcceptedLocation = (x, z) -> {
            int highestY = world.getHighestBlockYAt(x, z);
            Location loc = new Location(world, x, highestY, z);

            if (loc.getBlock().getType() == Material.WATER) {
                return false;
            }

            Biome biome = loc.getBlock().getBiome();
            return !isDeniedBiomes(biome);
        };

        BiFunction<Integer, Integer, Integer[]> createCoord = (a, b) -> new Integer[] { a, b };

        for (int i = 0; i < maxTries; i++) {
            tries++;
            int x = random.nextInt(30000 - 200) + 100;
            int z = random.nextInt(30000 - 200) + 100;

            List<Integer[]> coordinates = List.of(
                createCoord.apply(x, z),
                createCoord.apply(x + 100, z),
                createCoord.apply(x + 100 + 50, z + 50),
                createCoord.apply(x, z + 100),
                createCoord.apply(x + 100, z + 100)
            );

            if (!coordinates.stream().allMatch(coord -> isAcceptedLocation.apply(coord[0], coord[1]))) continue;
            Location loc = new Location(world, x, world.getHighestBlockYAt(x, z), z);
            System.out.println("地形尋找次數: " + tries);
            Main.logToAnyPlayer("地形尋找次數: " + tries);
            return loc;
        }
        return null;
    }

    private static boolean isDeniedBiomes(Biome biome) {
        List<Biome> deniedBiomes = List.of(
            Biome.OCEAN,
            Biome.DEEP_OCEAN,
            Biome.FROZEN_OCEAN,
            Biome.COLD_OCEAN,
            Biome.LUKEWARM_OCEAN,
            Biome.WARM_OCEAN,
            Biome.DEEP_COLD_OCEAN,
            Biome.DEEP_LUKEWARM_OCEAN,
            Biome.RIVER,
            Biome.FROZEN_RIVER,
            Biome.BADLANDS,
            Biome.DESERT,
            Biome.PLAINS,
            Biome.SNOWY_PLAINS,
            Biome.SUNFLOWER_PLAINS,
            Biome.ICE_SPIKES,
            Biome.ERODED_BADLANDS,
            Biome.WOODED_BADLANDS,
            Biome.FROZEN_PEAKS,
            Biome.SNOWY_SLOPES,
            Biome.MEADOW
        );
        return deniedBiomes.contains(biome);
    }
}
