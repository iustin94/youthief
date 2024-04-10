package net.whiteheket.youthief.util;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.util.Identifier;
import net.whiteheket.youthief.YouThief;

import java.util.Set;

public class VillageLootTableReplace {
    public static void init() {
        Set<Identifier> villageHouseChestsId = Set.of(
                LootTables.VILLAGE_ARMORER_CHEST,
                LootTables.VILLAGE_BUTCHER_CHEST,
                LootTables.VILLAGE_FISHER_CHEST,
                LootTables.VILLAGE_CARTOGRAPHER_CHEST,
                LootTables.VILLAGE_FLETCHER_CHEST,
                LootTables.VILLAGE_MASON_CHEST,
                LootTables.VILLAGE_SHEPARD_CHEST,
                LootTables.VILLAGE_TANNERY_CHEST,
                LootTables.VILLAGE_TEMPLE_CHEST,
                LootTables.VILLAGE_TOOLSMITH_CHEST,
                LootTables.VILLAGE_WEAPONSMITH_CHEST,
                LootTables.VILLAGE_PLAINS_CHEST);

        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            Identifier injectId = new Identifier(YouThief.MOD_ID, "inject/" + id.getPath());
            if (villageHouseChestsId.contains(id)) {
                tableBuilder.pool(LootPool.builder().with(LootTableEntry.builder(injectId).weight(1).quality(0)).build());
            }
        });
    }
}
