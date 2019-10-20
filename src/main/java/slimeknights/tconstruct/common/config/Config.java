package slimeknights.tconstruct.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.pulsar.config.PulsarConfig;
import slimeknights.tconstruct.library.Util;

import java.util.Collections;
import java.util.Set;

public class Config {

  public static PulsarConfig pulseConfig = new PulsarConfig("TinkerModules", "Modules");

  private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
  public static final General CONFIG = new General(BUILDER);

  public static class General {

    public final ForgeConfigSpec.BooleanValue shouldRegisterAllItems;

    public final ForgeConfigSpec.BooleanValue shouldSpawnWithTinkersBook;
    public final ForgeConfigSpec.BooleanValue addGravelToFlintRecipe;
    public final ForgeConfigSpec.BooleanValue requireSlimeballsToMatchInVanillaRecipe;

    public final ForgeConfigSpec.BooleanValue generateCobalt;
    public final ForgeConfigSpec.ConfigValue<Integer> veinCountCobalt;

    public final ForgeConfigSpec.BooleanValue generateArdite;
    public final ForgeConfigSpec.ConfigValue<Integer> veinCountArdite;

    public final ForgeConfigSpec.BooleanValue generateSlimeIslands;

    General(ForgeConfigSpec.Builder builder) {
      builder.push("game play").comment("Everything to do with gameplay");
      this.shouldRegisterAllItems = builder.comment("Enables all items, even if the Module needed to obtain them is not active").define("shouldRegisterAllItems", true);
      this.shouldSpawnWithTinkersBook = builder.comment("Players who enter the world for the first time get a Tinkers' Book").define("shouldSpawnWithTinkersBook", true);
      this.addGravelToFlintRecipe = builder.comment("Add a recipe that allows you to craft a piece of flint using 3 gravel").define("addGravelToFlintRecipe", true);
      this.requireSlimeballsToMatchInVanillaRecipe = builder.comment("If true, requires slimeballs in the vanilla slimeblock recipe to match in color, otherwise gives a pink slimeblock").define("requireSlimeballsToMatchInVanillaRecipe", false);

      builder.push("world gen").comment("Toggle blocks being generated into the world");
      this.generateCobalt = builder.comment("Generate Cobalt").define("generateCobalt", true);
      this.veinCountCobalt = builder.comment("Approx Ores per Chunk").define("veinCountCobalt", 20);

      this.generateArdite = builder.comment("Generate Ardite").define("generateArdite", true);
      this.veinCountArdite = builder.comment("Approx Ores per Chunk").define("veinCountArdite", 20);

      this.generateSlimeIslands = builder.comment("Generate Slime Islands").define("generateSlimeIslands", true);
    }
  }

  public static final ForgeConfigSpec spec = BUILDER.build();

  public static Config instance = new Config();
  public static Logger log = Util.getLogger("Config");

  private Config() {
  }
  // Tools and general
  public static boolean reuseStencil = true;
  public static boolean craftCastableMaterials = false;
  public static boolean chestsKeepInventory = true;
  public static boolean autosmeltlapis = true;
  public static boolean obsidianAlloy = true;
  public static boolean claycasts = true;
  public static boolean castableBricks = true;
  public static boolean leatherDryingRecipe = true;
  private static String[] craftingStationBlacklistArray = new String[] {
          "de.ellpeck.actuallyadditions.mod.tile.TileEntityItemViewer"
  };
  private static String[] orePreference = {
          "minecraft",
          "tconstruct",
          "thermalfoundation",
          "forestry",
          "immersiveengineering",
          "embers",
          "ic2"
  };
  public static Set<String> craftingStationBlacklist = Collections.emptySet();
  public static String[] oredictMeltingIgnore = {
          "dustRedstone",
          "plankWood",
          "stickWood",
          "stickTreatedWood",
          "string",
          "minecraft:chest:0"
  };

  // Worldgen
  public static boolean genIslandsInSuperflat = false;
  public static int slimeIslandsRate = 730; // Every x-th chunk will have a slime island. so 1 = every chunk, 100 = every 100th
  public static int magmaIslandsRate = 100; // Every x-th chunk will have a slime island. so 1 = every chunk, 100 = every 100th
  public static int[] slimeIslandBlacklist = new int[] { -1, 1 };
  public static boolean slimeIslandsOnlyGenerateInSurfaceWorlds = true;

  // Clientside configs
  public static boolean renderTableItems = true;
  public static boolean renderInventoryNullLayer = true;
  public static boolean extraTooltips = true;
  public static boolean listAllTables = true;
  public static boolean listAllMaterials = true;
  public static boolean enableForgeBucketModel = true; // enables the forge bucket model by default
  public static boolean dumpTextureMap = false; // requires debug module
  public static boolean testIMC = false; // requires debug module
  public static boolean temperatureCelsius = true;
}
