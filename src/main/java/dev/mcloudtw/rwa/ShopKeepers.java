package dev.mcloudtw.rwa;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.List;

public class ShopKeepers {
    public static MerchantRecipe getMerchantRecipe(ItemStack item1, ItemStack item2, ItemStack result) {
        MerchantRecipe recipe = new MerchantRecipe(result, 2147483647);
        if (item1 != null) recipe.addIngredient(item1);
        if (item2 != null) recipe.addIngredient(item2);
        return recipe;
    }

    public static Villager spawnVillager(Location location, String villagerName) {
        Villager villager = location.getWorld().spawn(location, Villager.class);
        villager.setCustomName(villagerName);
        villager.setCustomNameVisible(true);
        villager.setProfession(Villager.Profession.CLERIC);
        villager.setVillagerType(Villager.Type.TAIGA);
        villager.setVillagerLevel(5);
        villager.setVillagerExperience(0);
        villager.setInvulnerable(true);
        villager.setAI(false);
        return villager;
    }

    public static Villager baseItemsVillageer(Location location) {
        Villager villager = spawnVillager(location, "§6§l基本物資商人");
        List<MerchantRecipe> recipes = List.of(
                getMerchantRecipe(
                        new ItemStack(Material.CHERRY_LOG, 4), null, new ItemStack(Material.COBBLESTONE, 2)
                ),
                getMerchantRecipe(
                        new ItemStack(Material.ACACIA_LOG, 4), null, new ItemStack(Material.COBBLESTONE, 2)
                ),
                getMerchantRecipe(
                        new ItemStack(Material.BIRCH_LOG, 4), null, new ItemStack(Material.COBBLESTONE, 2)
                ),
                getMerchantRecipe(
                        new ItemStack(Material.DARK_OAK_LOG, 4), null, new ItemStack(Material.COBBLESTONE, 2)
                ),
                getMerchantRecipe(
                        new ItemStack(Material.JUNGLE_LOG, 4), null, new ItemStack(Material.COBBLESTONE, 2)
                ),
                getMerchantRecipe(
                        new ItemStack(Material.MANGROVE_LOG, 4), null, new ItemStack(Material.COBBLESTONE, 2)
                ),
                getMerchantRecipe(
                        new ItemStack(Material.OAK_LOG, 4), null, new ItemStack(Material.COBBLESTONE, 2)
                ),
                getMerchantRecipe(
                        new ItemStack(Material.SPRUCE_LOG, 4), null, new ItemStack(Material.COBBLESTONE, 2)
                ),
                getMerchantRecipe(
                        new ItemStack(Material.COBBLESTONE, 32), null, new ItemStack(Material.IRON_INGOT, 1)
                ),
                getMerchantRecipe(
                        new ItemStack(Material.IRON_INGOT, 6), null, new ItemStack(Material.GOLD_INGOT, 1)
                ),
                getMerchantRecipe(
                        new ItemStack(Material.GOLD_INGOT, 12), null, new ItemStack(Material.DIAMOND, 1)
                )
        );

        villager.setRecipes(recipes);
        return villager;
    }

    public static Villager coreProtectItemsVillageer(Location location) {
        Villager villager = spawnVillager(location, "§6§l核心保護商人");
        List<MerchantRecipe> recipes = List.of(
                getMerchantRecipe(
                        new ItemStack(Material.GOLD_INGOT, 6), null, new ItemStack(Material.OBSIDIAN, 1)
                ),
                getMerchantRecipe(
                        new ItemStack(Material.GOLD_INGOT, 4), null, new ItemStack(Material.LAVA_BUCKET, 1)
                ),
                getMerchantRecipe(
                        new ItemStack(Material.GOLD_INGOT, 1), null, new ItemStack(Material.WATER_BUCKET, 1)
                ),
                getMerchantRecipe(
                        new ItemStack(Material.IRON_INGOT, 6), null, new ItemStack(Material.END_STONE, 1)
                ),
                getMerchantRecipe(
                        new ItemStack(Material.IRON_INGOT, 3), null, new ItemStack(Material.COBWEB, 2)
                )
        );

        villager.setRecipes(recipes);
        return villager;
    }

    public static Villager tractionSheepItemsVillageer(Location location) {
        Villager villager = spawnVillager(location, "§6§l拉羊道具商人");
        List<MerchantRecipe> recipes = List.of(
                getMerchantRecipe(
                        new ItemStack(Material.IRON_INGOT, 1), null, new ItemStack(Material.RAIL, 1)
                ),
                getMerchantRecipe(
                        new ItemStack(Material.IRON_INGOT, 3), null, new ItemStack(Material.POWERED_RAIL, 1)
                ),
                getMerchantRecipe(
                        new ItemStack(Material.IRON_INGOT, 1), null, new ItemStack(Material.MINECART, 1)
                ),
                getMerchantRecipe(
                        new ItemStack(Material.IRON_INGOT, 3), null, new ItemStack(Material.SLIME_BALL, 1)
                ),
                getMerchantRecipe(
                        new ItemStack(Material.IRON_INGOT, 1), null, new ItemStack(Material.REDSTONE, 9)
                ),
                getMerchantRecipe(
                        new ItemStack(Material.IRON_INGOT, 1), null, new ItemStack(Material.STRING, 1)
                ),
                getMerchantRecipe(
                        new ItemStack(Material.GOLD_INGOT, 1), null, new ItemStack(Material.PISTON, 1)
                ),
                getMerchantRecipe(
                        new ItemStack(Material.GOLD_INGOT, 2), null, new ItemStack(Material.STICKY_PISTON, 1)
                ),
                getMerchantRecipe(
                        new ItemStack(Material.IRON_INGOT, 3), null, new ItemStack(Material.TRIPWIRE_HOOK, 1)
                )
        );

        villager.setRecipes(recipes);
        return villager;
    }

    public static Villager offensiveItemsVillageer(Location location) {
        Villager villager = spawnVillager(location, "§6§l進攻道具商人");
        List<MerchantRecipe> recipes = List.of(
                getMerchantRecipe(
                        new ItemStack(Material.GOLD_INGOT, 1), null, new ItemStack(Material.TNT, 1)
                ),
                getMerchantRecipe(
                        new ItemStack(Material.GOLD_INGOT, 1), null, new ItemStack(Material.FLINT_AND_STEEL, 1)
                ),
                getMerchantRecipe(
                        new ItemStack(Material.IRON_INGOT, 1), null, new ItemStack(Material.AZALEA, 1)
                ),
                 getMerchantRecipe(
                        new ItemStack(Material.GOLD_INGOT, 1), null, new ItemStack(Material.ARROW, 12)
                ),
                getMerchantRecipe(
                        new ItemStack(Material.IRON_INGOT, 3), null, new ItemStack(Material.SNOWBALL, 8)
                ),
                getMerchantRecipe(
                        new ItemStack(Material.GOLD_INGOT, 1), null, new ItemStack(Material.WIND_CHARGE, 1)
                )
        );

        villager.setRecipes(recipes);
        return villager;
    }

    public static Villager specialOffensiveItemsVillageer(Location location) {
        Villager villager = spawnVillager(location, "§6§l特殊進攻道具商人");
        List<MerchantRecipe> recipes = List.of(
                getMerchantRecipe(
                        new ItemStack(Material.GOLD_INGOT, 1), null, new ItemStack(Material.ENDER_PEARL, 1)
                ),
                getMerchantRecipe(
                        new ItemStack(Material.IRON_INGOT, 3), null, new ItemStack(Material.MILK_BUCKET, 1)
                ),
                getMerchantRecipe(
                        new ItemStack(Material.DIAMOND, 1), null, new ItemStack(Material.MACE, 1)
                ),
                getMerchantRecipe(
                        new ItemStack(Material.DIAMOND, 1), null, new ItemStack(Material.TRIDENT, 1)
                )
        );

        villager.setRecipes(recipes);
        return villager;
    }

    public static Villager infrastructureAndToolsVillageer(Location location) {
        Villager villager = spawnVillager(location, "§6§l基礎設施與道具商人");
        List<MerchantRecipe> recipes = List.of(
                getMerchantRecipe(
                        new ItemStack(Material.COBBLESTONE, 16), null, new ItemStack(Material.CRAFTING_TABLE, 1)
                ),
                getMerchantRecipe(
                        new ItemStack(Material.COBBLESTONE, 8), null, new ItemStack(Material.FURNACE, 1)
                ),
                getMerchantRecipe(
                        new ItemStack(Material.IRON_INGOT, 8), null, new ItemStack(Material.SMITHING_TABLE, 1)
                ),
                getMerchantRecipe(
                        new ItemStack(Material.IRON_INGOT, 16), null, new ItemStack(Material.ANVIL, 1)
                ),
                getMerchantRecipe(
                        new ItemStack(Material.IRON_INGOT, 16), null, new ItemStack(Material.GRINDSTONE, 1)
                ),
                getMerchantRecipe(
                        new ItemStack(Material.DIAMOND, 3), null, new ItemStack(Material.ENCHANTING_TABLE, 1)
                ),
                getMerchantRecipe(
                        new ItemStack(Material.GOLD_INGOT, 2), null, new ItemStack(Material.BOOKSHELF, 1)
                )
        );
        return villager;
    }
}
