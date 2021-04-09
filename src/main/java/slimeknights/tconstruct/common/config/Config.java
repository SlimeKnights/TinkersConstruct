package slimeknights.tconstruct.common.config;

import java.util.ArrayList;
import java.util.List;

public class Config {

  public static Common common;

  /**
   * Common specific configuration
   */
  public static class Common {

    public final boolean shouldSpawnWithTinkersBook;

    // recipes
    public final boolean addGravelToFlintRecipe;
    public final boolean cheaperNetheriteAlloy;
    public final boolean witherBoneDrop;
    public final boolean witherBoneConversion;

    public final int melterNuggetsPerOre;
    public final int smelteryNuggetsPerOre;

    public final boolean generateCobalt;
    public final int veinCountCobalt;

    public final boolean generateCopper;
    public final int veinCountCopper;

    public final boolean generateSlimeIslands;

    // public final boolean chestsKeepInventory;

    public final List<String> craftingStationBlacklist;

    public final boolean listAllToolMaterials;

    public final boolean listAllPartMaterials;

    Common(ForgeConfigSpec.Builder builder) {
      builder.comment("Everything to do with gameplay").push("gameplay");

      this.shouldSpawnWithTinkersBook = builder
        .comment("Set this to false to disable new players spawning with the Tinkers' Book.")
        .translation("tconstruct.configgui.shouldSpawnWithTinkersBook")
        .worldRestart()
        .define("shouldSpawnWithTinkersBook", true);

//      this.chestsKeepInventory = builder
//        .comment("Pattern and Part chests keep their inventory when harvested.")
//        .translation("tconstruct.configgui.chestsKeepInventory")
//        .worldRestart()
//        .define("chestsKeepInventory", true);

      this.craftingStationBlacklist = builder
        .comment("Blacklist of registry names for the crafting station to connect to. Mainly for compatibility.")
        .translation("tconstruct.configgui.craftingStationBlacklist")
        .worldRestart()
        .define("craftingStationBlacklist", new ArrayList<>());

      this.listAllToolMaterials = builder
        .comment("If true all material variants of the different tools will be listed in creative. Set to false to only have the first found material for all tools (usually wood).")
        .translation("tconstruct.configgui.listAllToolMaterials")
        .worldRestart()
        .define("listAllToolMaterials", true);

      this.listAllPartMaterials = builder
        .comment("If true all material variants of the different parts will be listed in creative. Set to false to only have the first found material for all parts (usually wood).")
        .translation("tconstruct.configgui.listAllPartMaterials")
        .worldRestart()
        .define("listAllPartMaterials", true);

      builder.pop();

      builder.comment("Options related to recipes, limited options as a datapack allows most recipes to be modified").push("recipes");

      this.addGravelToFlintRecipe = builder
        .comment("Add a recipe that allows you to craft a piece of flint using 3 gravel")
        .translation("tconstruct.configgui.addGravelToFlintRecipe")
        .worldRestart()
        .define("addGravelToFlintRecipe", true);

      this.cheaperNetheriteAlloy = builder
        .comment("Makes the recipe to alloy netherite in the smeltery only cost 2 gold per netherite ingot. If false uses the vanilla rate of 4 gold per ingot. Disable if there are crafting duplications.")
        .translation("tconstruct.configgui.cheaperNetheriteAlloy")
        .worldRestart()
        .define("cheaperNetheriteAlloy", true);

      this.witherBoneDrop = builder
        .comment("Makes wither skeletons drop necrotic bones")
        .translation("tconstruct.configgui.witherBoneDrop")
        .worldRestart()
        .define("witherBoneDrop", true);

      this.witherBoneConversion = builder
        .comment("Allows converting wither bones to regular bones")
        .translation("tconstruct.configgui.witherBoneConversion")
        .worldRestart()
        .define("witherBoneConversion", true);

      this.melterNuggetsPerOre = builder
        .comment("Number of nuggets produced when an ore block is melted in the melter. 9 would give 1 ingot")
        .translation("tconstruct.configgui.melterNuggetsPerOre")
        .defineInRange("melterNuggetsPerOre", 12, 1, 45);
      this.smelteryNuggetsPerOre = builder
        .comment("Number of nuggets produced when an ore block is melted in the smeltery. 9 nuggets would give 1 ingot")
        .translation("tconstruct.configgui.smelteryNuggetsPerOre")
        .defineInRange("smelteryNuggetsPerOre", 18, 1, 45);

      builder.pop();

      builder.comment("Everything to do with world generation").push("worldgen");

      this.generateCopper = builder
        .comment("Generate Copper")
        .translation("tconstruct.configgui.generateCopper")
        .worldRestart()
        .define("generateCopper", true);
      this.veinCountCopper = builder
        .comment("Approx Ores per Chunk")
        .translation("tconstruct.configgui.veinCountCopper")
        .worldRestart()
        .define("veinCountCopper", 20);

      this.generateCobalt = builder
        .comment("Generate Cobalt")
        .translation("tconstruct.configgui.generateCobalt")
        .worldRestart()
        .define("generateCobalt", true);
      this.veinCountCobalt = builder
        .comment("Approx Ores per Chunk")
        .translation("tconstruct.configgui.veinCountCobalt")
        .worldRestart()
        .define("veinCountCobalt", 8);

      this.generateSlimeIslands = builder
        .comment("Set this to false to disable slime islands spawning in the world")
        .translation("tconstruct.configgui.generateSlimeIslands")
        .worldRestart()
        .define("generateSlimeIslands", true);

      builder.pop();
    }
  }

  /**
   * Client specific configuration - only loaded clientside from tconstruct-client.toml
   */
  public static class Client {
    //public final ForgeConfigSpec.boolean temperatureInCelsius;

    public final ForgeConfigSpec.boolean tankFluidModel;

    public final ForgeConfigSpec.boolean extraToolTips;

    Client(ForgeConfigSpec.Builder builder) {
      builder.comment("Client only settings").push("client");

//      this.temperatureInCelsius = builder
//        .comment("If true, temperatures in the smeltery and in JEI will display in celsius. If false they will use the internal units of Kelvin, which may be better for developers")
//        .translation("tconstruct.configgui.temperatureInCelsius")
//        .define("temperatureInCelsius", true);

      this.tankFluidModel = builder
        .comment(
          "Experimental. If true, renders fluids in tanks using a dynamic model, being more efficient when the tank is static",
          "If false, renders fluids in tanks using a TESR, which is more efficient when the tank contents are changing"
         )
        .translation("tconstruct.configgui.tankFluidModel")
        .define("tankFluidModel", false);

      this.extraToolTips = builder
        .comment("If true tools will show additional info in their tooltips")
        .translation("tconstruct.configgui.extraToolTips")
        .define("extraToolTips", true);

      builder.pop();
    }
  }
}
