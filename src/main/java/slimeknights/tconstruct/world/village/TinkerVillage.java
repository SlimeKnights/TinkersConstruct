package slimeknights.tconstruct.world.village;

import net.minecraft.entity.passive.EntityVillager.EmeraldForItems;
import net.minecraft.entity.passive.EntityVillager.PriceInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;

import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.loot.RandomMaterial;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.world.village.smeltery.ComponentSmeltery;
import slimeknights.tconstruct.world.village.smeltery.VillageSmelteryHandler;
import slimeknights.tconstruct.world.village.workshop.ComponentToolWorkshop;
import slimeknights.tconstruct.world.village.workshop.VillageToolWorkshopHandler;

public class TinkerVillage {
  public static VillagerProfession villagerProfession_tools;
  public static VillagerProfession villagerProfession_smeltery;

  public static final ResourceLocation PATTERN_CHEST_LOOT_TABLE = Util.getResource("village/pattern_chest");
  public static final ResourceLocation CRAFTING_STATION_LOOT_TABLE = Util.getResource("village/crafting_station");
  public static final ResourceLocation PART_CHEST_LOOT_TABLE = Util.getResource("village/part_chest");

  public static void initVillage() {
    VillagerRegistry villageRegistry = VillagerRegistry.instance();

    if(Config.enableVillagers) {
      villagerProfession_tools = new VillagerProfession(Util.resource("tools"), "tconstruct:textures/entity/villager_tools.png", "tconstruct:textures/entity/villager_tools_zombie.png");
      villagerProfession_smeltery = new VillagerProfession(Util.resource("smeltery"), "tconstruct:textures/entity/villager_smeltery.png", "tconstruct:textures/entity/villager_smeltery_zombie.png");
      ForgeRegistries.VILLAGER_PROFESSIONS.registerAll(villagerProfession_tools, villagerProfession_smeltery);

      VillagerCareer career_toolmaster = new VillagerCareer(villagerProfession_tools, Util.prefix("toolmaster"));

      career_toolmaster.addTrade(1, new EmeraldForItems(TinkerTools.binding, new PriceInfo(4, 7)));

      VillagerCareer career_smelterymaster = new VillagerCareer(villagerProfession_smeltery, Util.prefix("smelterymaster"));

      career_smelterymaster.addTrade(1, new EmeraldForItems(TinkerSmeltery.cast, new PriceInfo(4, 7)));
    }

    LootFunctionManager.registerFunction(new RandomMaterial.Serializer());

    LootTableList.register(PATTERN_CHEST_LOOT_TABLE);
    LootTableList.register(CRAFTING_STATION_LOOT_TABLE);
    LootTableList.register(PART_CHEST_LOOT_TABLE);

    villageRegistry.registerVillageCreationHandler(new VillageToolWorkshopHandler());
    MapGenStructureIO.registerStructureComponent(ComponentToolWorkshop.class, Util.resource("ToolWorkshopStructure"));

    villageRegistry.registerVillageCreationHandler(new VillageSmelteryHandler());
    MapGenStructureIO.registerStructureComponent(ComponentSmeltery.class, Util.resource("SmelteryStructure"));
  }
}
