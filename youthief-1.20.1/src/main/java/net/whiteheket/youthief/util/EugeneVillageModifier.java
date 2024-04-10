package net.whiteheket.youthief.util;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.util.Identifier;

public class EugeneVillageModifier {
    private static final Identifier VILLAGE_PLAINS_HOUSE_RICH_ID = new Identifier("minecraft ", "chests/village/village_plains_house_rich");

    public static void modifyLootTablesRich() {
    LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) ->{
        if (VILLAGE_PLAINS_HOUSE_RICH_ID.equals(id)){
            tableBuilder.pool(LootPool.builder().with(LootTableEntry.builder(VILLAGE_PLAINS_HOUSE_RICH_ID).weight(1).quality(0)).build());
        }
        });
    }
}
