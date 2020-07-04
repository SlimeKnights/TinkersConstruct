package slimeknights.tconstruct.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import slimeknights.mantle.pulsar.config.PulsarConfig;

import java.util.ArrayList;
import java.util.List;

public class Config {

  public static PulsarConfig pulseConfig = new PulsarConfig("TinkerModules", "Modules");

  /**
   * Common specific configuration
   */
  public static class Common {

    public final ForgeConfigSpec.BooleanValue shouldSpawnWithTinkersBook;

    public final ForgeConfigSpec.BooleanValue addGravelToFlintRecipe;

    public final ForgeConfigSpec.BooleanValue requireSlimeballsToMatchInVanillaRecipe;

    public final ForgeConfigSpec.BooleanValue registerAllRecipes;

    public final ForgeConfigSpec.BooleanValue generateCobalt;
    public final ForgeConfigSpec.ConfigValue<Integer> veinCountCobalt;

    public final ForgeConfigSpec.BooleanValue generateArdite;
    public final ForgeConfigSpec.ConfigValue<Integer> veinCountArdite;

    public final ForgeConfigSpec.BooleanValue generateCopper;
    public final ForgeConfigSpec.ConfigValue<Integer> veinCountCopper;

    public final ForgeConfigSpec.BooleanValue generateSlimeIslands;

    public final ForgeConfigSpec.BooleanValue chestsKeepInventory;

    public final ForgeConfigSpec.ConfigValue<List<String>> craftingStationBlacklist;

    public final ForgeConfigSpec.BooleanValue listAllToolMaterials;

    public final ForgeConfigSpec.BooleanValue listAllPartMaterials;

    Common(ForgeConfigSpec.Builder builder) {
      builder.comment("Everything to do with gameplay").push("gameplay");

      this.shouldSpawnWithTinkersBook = builder
        .comment("Set this to false to disable new players spawning with the Tinkers' Book.")
        .translation("tconstruct.configgui.shouldSpawnWithTinkersBook")
        .worldRestart()
        .define("shouldSpawnWithTinkersBook", true);

      this.addGravelToFlintRecipe = builder
        .comment("Add a recipe that allows you to craft a piece of flint using 3 gravel")
        .translation("tconstruct.configgui.addGravelToFlintRecipe")
        .worldRestart()
        .define("addGravelToFlintRecipe", true);

      this.requireSlimeballsToMatchInVanillaRecipe = builder
        .comment("If true, requires slimeballs in the vanilla slimeblock recipe to match in color, otherwise gives a pink slimeblock")
        .translation("tconstruct.configgui.requireSlimeballsToMatchInVanillaRecipe")
        .worldRestart()
        .define("requireSlimeballsToMatchInVanillaRecipe", true);

      this.registerAllRecipes = builder
        .comment("If true, all recipes will be added even if the modules required are disabled.")
        .translation("tconstruct.configgui.registerAllRecipes")
        .worldRestart()
        .define("registerAllRecipes", false);

      this.chestsKeepInventory = builder
        .comment("Pattern and Part chests keep their inventory when harvested.")
        .translation("tconstruct.configgui.chestsKeepInventory")
        .worldRestart()
        .define("chestsKeepInventory", true);

      this.craftingStationBlacklist = builder
        .comment("Blacklist of registry names or TE class names for the crafting station to connect to. Mainly for compatibility.")
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
        .define("veinCountCobalt", 20);

      this.generateArdite = builder
        .comment("Generate Ardite")
        .translation("tconstruct.configgui.generateArdite")
        .worldRestart()
        .define("generateArdite", true);

      this.veinCountArdite = builder
        .comment("Approx Ores per Chunk")
        .translation("tconstruct.configgui.veinCountArdite")
        .worldRestart()
        .define("veinCountArdite", 20);

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

    public final ForgeConfigSpec.BooleanValue renderInventoryInWorld;
    public final ForgeConfigSpec.BooleanValue temperatureInCelsius;

    public final ForgeConfigSpec.BooleanValue renderTableItems;
    public final ForgeConfigSpec.BooleanValue tankFluidModel;

    public final ForgeConfigSpec.BooleanValue extraToolTips;

    Client(ForgeConfigSpec.Builder builder) {
      builder.comment("Client only settings").push("client");

      this.renderInventoryInWorld = builder
        .comment("If true all of Tinkers' blocks with contents (tables, basin, drying racks,...) will render their contents in the world")
        .translation("tconstruct.configgui.renderInventoryInWorld")
        .define("renderInventoryInWorld", true);

      this.temperatureInCelsius = builder
        .comment("If true, temperatures in the smeltery and in JEI will display in celsius. If false they will use the internal units of Kelvin, which may be better for developers")
        .translation("tconstruct.configgui.temperatureInCelsius")
        .define("temperatureInCelsius", true);

      this.renderTableItems = builder
        .comment("If true all of Tinkers' blocks with contents (tables, basin, drying racks,...) will render their contents in the world")
        .translation("tconstruct.configgui.renderTableItems")
        .define("renderTableItems", true);

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

  public static final ForgeConfigSpec clientSpec;
  public static final Client CLIENT;

  static {
    final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
    clientSpec = specPair.getRight();
    CLIENT = specPair.getLeft();
  }

  public static final ForgeConfigSpec commonSpec;
  public static final Common COMMON;

  static {
    final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
    commonSpec = specPair.getRight();
    COMMON = specPair.getLeft();
  }

}
