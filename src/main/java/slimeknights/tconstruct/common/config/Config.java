package slimeknights.tconstruct.common.config;

import org.apache.logging.log4j.Logger;
import slimeknights.mantle.pulsar.config.PulsarConfig;
import slimeknights.tconstruct.library.Util;

import java.util.Collections;
import java.util.Set;

public class Config {

  public static PulsarConfig pulseConfig = new PulsarConfig("TinkerModules", "Modules");
  public static Config instance = new Config();
  public static Logger log = Util.getLogger("Config");

  private Config() {
  }

  public static boolean forceRegisterAll = true; // enables all common items, even if their module is not present

  // Tools and general
  public static boolean spawnWithBook = true;
  public static boolean reuseStencil = true;
  public static boolean craftCastableMaterials = false;
  public static boolean chestsKeepInventory = true;
  public static boolean autosmeltlapis = true;
  public static boolean obsidianAlloy = true;
  public static boolean claycasts = true;
  public static boolean castableBricks = true;
  public static boolean leatherDryingRecipe = true;
  public static boolean gravelFlintRecipe = true;
  public static boolean matchVanillaSlimeblock = false;
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
  public static boolean genSlimeIslands = true;
  public static boolean genIslandsInSuperflat = false;
  public static int slimeIslandsRate = 730; // Every x-th chunk will have a slime island. so 1 = every chunk, 100 = every 100th
  public static int magmaIslandsRate = 100; // Every x-th chunk will have a slime island. so 1 = every chunk, 100 = every 100th
  public static int[] slimeIslandBlacklist = new int[] { -1, 1 };
  public static boolean slimeIslandsOnlyGenerateInSurfaceWorlds = true;
  public static boolean genCobalt = true;
  public static int cobaltRate = 20; // max. cobalt per chunk
  public static boolean genArdite = true;
  public static int arditeRate = 20; // max. ardite per chunk

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
