package dev.mcloudtw.rwa;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import dev.mcloudtw.rwa.exception.LocationFinderTriesOverMaxTimes;
import dev.mcloudtw.rwa.worldedit.WeAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Material;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;

public class MapLoader {

    public static CompletableFuture<Void> loadTerrain() {
        JavaPlugin plugin = Main.getInstance();
        World game = plugin.getServer().getWorld("game");
        World world = plugin.getServer().getWorld("world");

        plugin.getLogger().info("正在尋找可接受地形...");
        Main.broadcast("正在尋找可接受地形...");

        CompletableFuture<Void> promise = new CompletableFuture<>();

        CompletableFuture.supplyAsync(() -> LocationFinder.findAcceptedLocation(world))
                .thenAccept(nonOceanLocation -> FaweAPI.getTaskManager().async(()->{
                    if (nonOceanLocation == null) {
                        plugin.getLogger().info("找不到非海洋地點");
                        Main.broadcast("找不到非海洋地點");
                        promise.completeExceptionally(
                                new LocationFinderTriesOverMaxTimes("找不到非海洋地點")
                        );
                        return;
                    }

                    plugin.getLogger().info("找到非海洋地點: " + nonOceanLocation.getX() + ", " + nonOceanLocation.getY() + ", " + nonOceanLocation.getZ());
                    Main.broadcast("找到非海洋地點: " + nonOceanLocation.getX() + ", " + nonOceanLocation.getY() + ", " + nonOceanLocation.getZ());

                    Location placeLocation1 = new Location(game, -92, -63, -91);
                    Location placeLocation2 = new Location(game, 108, -63, -91);

                    plugin.getLogger().info("正在複製地形...");
                    Main.broadcast("正在複製地形...");
                    Location min = new Location(world, nonOceanLocation.getX(), -63, nonOceanLocation.getZ());
                    Location max = new Location(world, nonOceanLocation.getX() + 99, 320, nonOceanLocation.getZ() + 99);
                    Clipboard clipboard1 = WeAPI.copyClipboard(min, max, false);
                    plugin.getLogger().info("地形複製完成");
                    Main.broadcast("地形複製完成");
                    plugin.getLogger().info("正在貼上A邊地形...");
                    Main.broadcast("正在貼上A邊地形...");
                    WeAPI.placeClipboard(clipboard1, placeLocation1, false, false);
                    plugin.getLogger().info("A邊地圖貼上完成");
                    Main.broadcast("A邊地圖貼上完成");

                    BlockVector3 direction = BlockVector3.UNIT_X;
                    plugin.getLogger().info("正在翻轉地形...");
                    Main.broadcast("正在翻轉地形...");
//                        Clipboard clipboard2 = WeAPI.flipClipboard(clipboard1, direction);

//                        plugin.getLogger().info("正在貼上B邊地形...");
                    WeAPI.placeFlippedClipboard(clipboard1, direction, placeLocation2, false, false);
                    plugin.getLogger().info("B邊地圖貼上完成");
                    Main.broadcast("B邊地圖貼上完成");

                    plugin.getLogger().info("地形準備完成");
                    Main.broadcast("地形準備完成");
                    promise.complete(null);
                }));
        return promise;
    }

    public static CompletableFuture<Void> loadCore() {
        JavaPlugin plugin = Main.getInstance();
        World gameResource = plugin.getServer().getWorld("game_resource");
        World game = plugin.getServer().getWorld("game");

        CompletableFuture<Void> promise = new CompletableFuture<>();

        FaweAPI.getTaskManager().async(() -> {
            if (gameResource == null || game == null) {
                plugin.getLogger().warning("Could not find world. " + (gameResource == null ? "gameResource " : "") + (game == null ? "game" : ""));
                promise.completeExceptionally(new RuntimeException("Could not find world"));
                return;
            }

            int sourceX1 = 6;
            int sourceY1 = -60;
            int sourceZ1 = 6;
            int sourceX2 = 10;
            int sourceY2 = 320;
            int sourceZ2 = 10;

            Location sourceMin = new Location(gameResource, sourceX1, sourceY1, sourceZ1);
            Location sourceMax = new Location(gameResource, sourceX2, sourceY2, sourceZ2);

            int destX1 = -64;
            int destZ1 = -43;

            int highestY = getHighestNonLeafLogYAtLocation(game, destX1, destZ1);
            if (highestY == -65) {
                plugin.getLogger().warning("Could not find valid Y coordinate for destination.");
                promise.completeExceptionally(new RuntimeException("Could not find valid Y coordinate for destination"));
                return;
            }
            Location destLocation1 = new Location(game, destX1, highestY, destZ1);

            Clipboard clipboard = WeAPI.copyClipboard(sourceMin, sourceMax, false);
            if (clipboard == null) {
                plugin.getLogger().warning("Could not copy clipboard.");
                promise.completeExceptionally(new RuntimeException("Could not copy clipboard"));
                return;
            }
            WeAPI.placeClipboard(clipboard, destLocation1, false, false);

            int destX2 = 76;
            int destZ2 = -43;

            Location destLocation2 = new Location(game, destX2, highestY, destZ2);

            WeAPI.placeClipboard(clipboard, destLocation2, false, false);

            MapProtector.createProtectZone(destLocation1, destLocation1.clone().add(4, 0, 4), "core-base-01", false);
            MapProtector.createProtectZone(destLocation2, destLocation2.clone().add(4, 0, 4), "core-base-02", false);
            MapProtector.createProtectZone(destLocation1.clone().add(2, 1, 2), destLocation1.clone().add(2, 1, 2), "core-01", false);
            MapProtector.createProtectZone(destLocation2.clone().add(2, 1, 2), destLocation2.clone().add(2, 1, 2), "core-02", false);
            plugin.getLogger().info("Core loaded to " + destX1 + ", " + destZ1 + " and " + destX2 + ", " + destZ2);
            promise.complete(null);
        });

        return promise;
    }

    public static CompletableFuture<Void> loadShop() {
        JavaPlugin plugin = Main.getInstance();
        World gameResource = plugin.getServer().getWorld("game_resource");
        World game = plugin.getServer().getWorld("game");

        CompletableFuture<Void> promise = new CompletableFuture<>();

        FaweAPI.getTaskManager().async(() -> {
            if (gameResource == null || game == null) {
                plugin.getLogger().warning("Could not find world. " + (gameResource == null ? "gameResource " : "") + (game == null ? "game" : ""));
                promise.completeExceptionally(new RuntimeException("Could not find world"));
                return;
            }

            int sourceX1 = 16;
            int sourceY1 = -60;
            int sourceZ1 = 4;
            int sourceX2 = 22;
            int sourceY2 = 320;
            int sourceZ2 = 11;

            Location sourceMin = new Location(gameResource, sourceX1, sourceY1, sourceZ1);
            Location sourceMax = new Location(gameResource, sourceX2, sourceY2, sourceZ2);

            int destX1 = -90;
            int destZ1 = -44;

            int highestY = getHighestNonLeafLogYAtLocation(game, destX1, destZ1);
            if (highestY == -65) {
                plugin.getLogger().warning("Could not find valid Y coordinate for destination.");
                promise.completeExceptionally(new RuntimeException("Could not find valid Y coordinate for destination"));
                return;
            }
            Location destLocation1 = new Location(game, destX1, highestY, destZ1);

            Clipboard clipboard = WeAPI.copyClipboard(sourceMin, sourceMax, false);
            if (clipboard == null) {
                plugin.getLogger().warning("Could not copy clipboard.");
                promise.completeExceptionally(new RuntimeException("Could not copy clipboard"));
                return;
            }
            WeAPI.placeClipboard(clipboard, destLocation1, false, false);

            int destX2 = 106; // 26
            int destZ2 = -44;

            Location destLocation2 = new Location(game, destX2, highestY, destZ2);

            WeAPI.placeFlippedClipboard(clipboard, BlockVector3.UNIT_X, destLocation2, false, false);

            MapProtector.createProtectZone(destLocation1, destLocation1.clone().add(6, 5, 7), "shop-base-01", false);
            MapProtector.createProtectZone(destLocation2, destLocation2.clone().add(-6, 5, 7), "shop-base-02", false);

            plugin.getLogger().info("Shop loaded to " + destX1 + ", " + destZ1 + " and " + destX2 + ", " + destZ2);
            promise.complete(null);
        });

        return promise;
    }

    public static CompletableFuture<Void> loadSandWallFall() {
        JavaPlugin plugin = Main.getInstance();
        World game = plugin.getServer().getWorld("game");

        CompletableFuture<Void> promise = new CompletableFuture<>();

        FaweAPI.getTaskManager().async(() -> {
            int sourceX1 = 9;
            int sourceY1 = -63;
            int sourceZ1 = -91;
            int sourceX2 = 9;
            int sourceY2 = 319;
            int sourceZ2 = 8;
            int destX = 8;
            int destY = -63;
            int destZ = -91;

            Location sourceMin = new Location(game, sourceX1, sourceY1, sourceZ1);
            Location sourceMax = new Location(game, sourceX2, sourceY2, sourceZ2);
            Location destLocation = new Location(game, destX, destY, destZ);
            Bukkit.getScheduler().runTask(plugin, () -> {
                System.out.println(Residence.getInstance().getResidenceManager().getResidences());
                Residence.getInstance().getResidenceManager().getResidences().forEach( (name, residence) -> {
                    System.out.println(name);
                    if (!name.contains("sand-wall")) return;
                    residence.remove();
                });
            });
            WeAPI.placeClipboard(WeAPI.copyClipboard(sourceMin, sourceMax, false), destLocation, false, false);

        });

        return promise;
    }

    public static CompletableFuture<Void> loadSandWall() {
        JavaPlugin plugin = Main.getInstance();
        World game = plugin.getServer().getWorld("game");

        CompletableFuture<Void> promise = new CompletableFuture<>();

        FaweAPI.getTaskManager().async(() -> {
            int sourceX1 = 8;
            int sourceY1 = -63;
            int sourceZ1 = -91;
            int sourceX2 = 8;
            int sourceY2 = 319;
            int sourceZ2 = 8;

            WeAPI.fillSand(new Location(game, sourceX1, sourceY1, sourceZ1), new Location(game, sourceX2, sourceY2, sourceZ2));
            Consumer<Integer> createProtectZone = (x) -> MapProtector.createProtectZone(new Location(game, x, sourceY1, sourceZ1), new Location(game, x, sourceY2, sourceZ2), "sand-wall-"+x, true);
            for (int x = 7; x <= 9; x++) {
                createProtectZone.accept(x);
            }



        });

        return promise;
    }

    private static int getHighestNonLeafLogYAtLocation(World world, int x, int z) {
        int highestY = world.getHighestBlockYAt(x, z);
        for (int y = highestY; y > -63; y--) {
            Material material = world.getBlockAt(x, y, z).getType();
            if (material == Material.BAMBOO) {
                continue;
            }
            if (material.isCompostable()) {
                continue;
            }
            if (material == Material.WATER || material == Material.LAVA) {
                return y;
            }
            if (!material.name().contains("LEAVES") && !material.name().contains("LOG") && material.isSolid()) {
                return y;
            }
        }
        return -65; // Should not happen
    }
}
