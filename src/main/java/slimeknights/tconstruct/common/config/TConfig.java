package slimeknights.tconstruct.common.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import slimeknights.tconstruct.TConstruct;

import java.util.ArrayList;
import java.util.List;

public class TConfig {

  public static Common common;
  public static Client client;

  /**
   * Common specific configuration
   */
  @Config(name = TConstruct.modID + "_common")
  public static class Common implements ConfigData {

    @Comment("Set this to false to disable new players spawning with the Tinkers' Book.")
    public boolean shouldSpawnWithTinkersBook;

    // recipes
    @Comment("Add a recipe that allows you to craft a piece of flint using 3 gravel")
    public boolean addGravelToFlintRecipe;

    @Comment("Makes the recipe to alloy netherite in the smeltery only cost 2 gold per netherite ingot. If false uses the vanilla rate of 4 gold per ingot. Disable if there are crafting duplications.")
    public boolean cheaperNetheriteAlloy;

    @Comment("Makes wither skeletons drop necrotic bones")
    public boolean witherBoneDrop;

    @Comment("Allows converting wither bones to regular bones")
    public boolean witherBoneConversion;

    @Comment("Number of nuggets produced when an ore block is melted in the melter. 9 would give 1 ingot")
    public int melterNuggetsPerOre;

    @Comment("Number of nuggets produced when an ore block is melted in the smeltery. 9 nuggets would give 1 ingot")
    public int smelteryNuggetsPerOre;

    // worldgen

    @Comment("Generate Cobalt")
    public boolean generateCobalt;

    @Comment("Approx Ores per Chunk")
    public int veinCountCobalt;

    @Comment("Generate Copper")
    public boolean generateCopper;

    @Comment("Approx Ores per Chunk")
    public int veinCountCopper;

    @Comment("Set this to false to disable slime islands spawning in the world")
    public boolean generateSlimeIslands;

    @Comment("Pattern and Part chests keep their inventory when harvested.")
    public boolean chestsKeepInventory;

    @Comment("Blacklist of registry names for the crafting station to connect to. Mainly for compatibility.")
    public List<String> craftingStationBlacklist;

    @Comment("If true all material variants of the different tools will be listed in creative. Set to false to only have the first found material for all tools (usually wood).")
    public boolean listAllToolMaterials;

    @Comment("If true all material variants of the different parts will be listed in creative. Set to false to only have the first found material for all parts (usually wood).")
    public boolean listAllPartMaterials;
  }

  /**
   * Client specific configuration - only loaded clientside from tconstruct-client.toml
   */
  @Config(name = TConstruct.modID + "_client")
  public static class Client implements ConfigData{

    @Comment("If true, temperatures in the smeltery and in JEI will display in celsius. If false they will use the internal units of Kelvin, which may be better for developers")
    public boolean temperatureInCelsius;

    @Comment("Experimental. If true, renders fluids in tanks using a dynamic model, being more efficient when the tank is static\nIf false, renders fluids in tanks using a TESR, which is more efficient when the tank contents are changing")
    public boolean tankFluidModel;

    @Comment("If true tools will show additional info in their tooltips")
    public boolean extraToolTips;
  }
}
