package net.whiteheket.youthief;


import net.fabricmc.api.ModInitializer;

import net.whiteheket.youthief.util.EugeneVillageModifier;
import net.whiteheket.youthief.util.VillageLootTableReplace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YouThief implements ModInitializer {
	public static final String MOD_ID ="youthief";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		VillageLootTableReplace.init();
		EugeneVillageModifier.modifyLootTablesRich();
	}
}