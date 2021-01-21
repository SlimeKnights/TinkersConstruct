package slimeknights.tconstruct.common.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.pulsar.config.ForgeCFG;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.utils.RecipeUtil;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class Config {

  public static ForgeCFG pulseConfig = new ForgeCFG("TinkerModules", "Modules");
  public static Config instance = new Config();
  public static Logger log = Util.getLogger("Config");

  private Config() {
  }


  public static boolean forceRegisterAll = false; // enables all common items, even if their module is not present

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
  public static double oreToIngotRatio = 2;
  public static boolean matchVanillaSlimeblock = false;
  public static boolean limitPiggybackpack = false;
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
  public static int[] slimeIslandBlacklist = new int[]{-1, 1};
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
  public static boolean listAllToolMaterials = true;
  public static boolean listAllPartMaterials = true;
  public static boolean enableForgeBucketModel = true; // enables the forge bucket model by default
  public static boolean dumpTextureMap = false; // requires debug module
  public static boolean testIMC = false; // requires debug module
  public static boolean temperatureCelsius = true;

  /* Config File */

  static Configuration configFile;

  static ConfigCategory Modules;
  static ConfigCategory Gameplay;
  static ConfigCategory Worldgen;
  static ConfigCategory ClientSide;

  public static void load(FMLPreInitializationEvent event) {
    configFile = new Configuration(event.getSuggestedConfigurationFile(), "0.1", false);

    MinecraftForge.EVENT_BUS.register(instance);

    syncConfig();
  }

  @SubscribeEvent
  public void update(ConfigChangedEvent.OnConfigChangedEvent event) {
    if(event.getModID().equals(TConstruct.modID)) {
      syncConfig();
    }
  }


  public static boolean syncConfig() {
    Property prop;

    // Modules
    {
      Modules = pulseConfig.getCategory();
      /*
      List<String> propOrder = Lists.newArrayList();
      // convert pulse config to MC compatible config for GUI config
      Modules = new ConfigCategory("modules");
      for(PulseMeta pm : TConstruct.pulseManager.getAllPulseMetadata()) {
        if(pm.isForced()) continue;
        prop = new Property(pm.getId(), pm.isDefaultEnabled() ? "true" : "false", Property.Type.BOOLEAN);
        prop.setValue(pm.isEnabled());
        prop.setRequiresMcRestart(true);
        Modules.put(pm.getId(), prop);
        propOrder.add(prop.getName());
      }
      Modules.setPropertyOrder(propOrder);*/
    }
    // Gameplay
    {
      String cat = "gameplay";
      List<String> propOrder = Lists.newArrayList();
      Gameplay = configFile.getCategory(cat);

      prop = configFile.get(cat, "spawnWithBook", spawnWithBook);
      prop.setComment("Players who enter the world for the first time get a Tinkers' Book");
      spawnWithBook = prop.getBoolean();
      propOrder.add(prop.getName());

      prop = configFile.get(cat, "reuseStencils", reuseStencil);
      prop.setComment("Allows to reuse stencils in the stencil table to turn them into other stencils");
      reuseStencil = prop.getBoolean();
      propOrder.add(prop.getName());

      prop = configFile.get(cat, "chestsKeepInventory", chestsKeepInventory);
      prop.setComment("Pattern and Part chests keep their inventory when harvested.");
      chestsKeepInventory = prop.getBoolean();
      propOrder.add(prop.getName());

      prop = configFile.get(cat, "enableClayCasts", claycasts);
      prop.setComment("Adds single-use clay casts.");
      claycasts = prop.getBoolean();
      prop.setRequiresMcRestart(true);
      propOrder.add(prop.getName());

      prop = configFile.get(cat, "allowBrickCasting", castableBricks);
      prop.setComment("Allows the creation of bricks from molten clay");
      castableBricks = prop.getBoolean();
      prop.setRequiresMcRestart(true);
      propOrder.add(prop.getName());

      prop = configFile.get(cat, "AutosmeltFortuneInteraction", autosmeltlapis);
      prop.setComment("Fortune increases drops after harvesting a block with autosmelt");
      autosmeltlapis = prop.getBoolean();
      propOrder.add(prop.getName());

      prop = configFile.get(cat, "craftCastableMaterials", craftCastableMaterials);
      prop.setComment("Allows to craft all tool parts of all materials in the part builder, including materials that normally have to be cast with a smeltery.");
      craftCastableMaterials = prop.getBoolean();
      propOrder.add(prop.getName());

      prop = configFile.get(cat, "registerAllItems", forceRegisterAll);
      prop.setComment("Enables all items, even if the Module needed to obtain them is not active");
      forceRegisterAll = prop.getBoolean();
      prop.setRequiresMcRestart(true);
      propOrder.add(prop.getName());

      prop = configFile.get(cat, "obsidianAlloy", obsidianAlloy);
      prop.setComment("Allows the creation of obsidian in the smeltery, using a bucket of lava and water.");
      obsidianAlloy = prop.getBoolean();
      prop.setRequiresMcRestart(true);
      propOrder.add(prop.getName());

      prop = configFile.get(cat, "addLeatherDryingRecipe", leatherDryingRecipe);
      prop.setComment("Adds a recipe that allows you to get leather from drying cooked meat");
      leatherDryingRecipe = prop.getBoolean();
      prop.setRequiresMcRestart(true);
      propOrder.add(prop.getName());

      prop = configFile.get(cat, "addFlintRecipe", gravelFlintRecipe);
      prop.setComment("Adds a recipe that allows you to craft 3 gravel into a flint");
      gravelFlintRecipe = prop.getBoolean();
      prop.setRequiresMcRestart(true);
      propOrder.add(prop.getName());

      prop = configFile.get(cat, "oreToIngotRatio", oreToIngotRatio);
      prop.setComment("Determines the ratio of ore to ingot, or in other words how many ingots you get out of an ore. This ratio applies to all ores (including poor and dense). The ratio can be any decimal, including 1.5 and the like, but can't go below 1. THIS ALSO AFFECTS MELTING TEMPERATURE!");
      prop.setMinValue(1);
      oreToIngotRatio = prop.getDouble();
      prop.setRequiresMcRestart(true);
      propOrder.add(prop.getName());

      prop = configFile.get(cat, "matchVanillaSlimeblock", matchVanillaSlimeblock);
      prop.setComment("If true, requires slimeballs in the vanilla slimeblock recipe to match in color, otherwise gives a pink slimeblock");
      matchVanillaSlimeblock = prop.getBoolean();
      prop.setRequiresMcRestart(true);
      propOrder.add(prop.getName());

      prop = configFile.get(cat, "limitPiggybackpack", limitPiggybackpack);
      prop.setComment("If true, piggybackpacks can only pick up players and mobs that can be leashed in vanilla. If false any mob can be picked up.");
      limitPiggybackpack = prop.getBoolean();
      propOrder.add(prop.getName());

      prop = configFile.get(cat, "craftingStationBlacklist", craftingStationBlacklistArray);
      prop.setComment("Blacklist of registry names or TE classnames for the crafting station to connect to. Mainly for compatibility.");
      craftingStationBlacklistArray = prop.getStringList();
      craftingStationBlacklist = Sets.newHashSet(craftingStationBlacklistArray);
      propOrder.add(prop.getName());

      prop = configFile.get(cat, "orePreference", orePreference);
      prop.setComment("Preferred mod ID for oredictionary outputs. Top most mod ID will be the preferred output ID, and if none is found the first output stack is used.");
      orePreference = prop.getStringList();
      RecipeUtil.setOrePreferences(orePreference);
      propOrder.add(prop.getName());

      prop = configFile.get(cat, "oredictMeltingIgnore", oredictMeltingIgnore);
      prop.setComment("List of items to ignore when generating melting recipes from the crafting registry. For example, ignoring sticks allows metal pickaxes to melt down.\nFormat: oreName or modid:item[:meta]. If meta is unset, uses wildcard");
      oredictMeltingIgnore = prop.getStringList();
      propOrder.add(prop.getName());

      prop = configFile.get(cat, "testIMC", testIMC);
      prop.setComment("REQUIRES DEBUG MODULE. Tests all IMC integrations with dummy recipes. May significantly impact gameplay, so its advised you disable this outside of dev environements.");
      testIMC = prop.getBoolean();
      propOrder.add(prop.getName());
    }
    // Worldgen
    {
      String cat = "worldgen";
      List<String> propOrder = Lists.newArrayList();
      Worldgen = configFile.getCategory(cat);

      // Slime Islands
      prop = configFile.get(cat, "generateSlimeIslands", genSlimeIslands);
      prop.setComment("If true slime islands will generate");
      genSlimeIslands = prop.getBoolean();
      propOrder.add(prop.getName());

      prop = configFile.get(cat, "generateIslandsInSuperflat", genIslandsInSuperflat);
      prop.setComment("If true slime islands generate in superflat worlds");
      genIslandsInSuperflat = prop.getBoolean();
      propOrder.add(prop.getName());

      prop = configFile.get(cat, "slimeIslandRate", slimeIslandsRate);
      prop.setComment("One in every X chunks will contain a slime island");
      slimeIslandsRate = prop.getInt();
      propOrder.add(prop.getName());

      prop = configFile.get(cat, "magmaIslandRate", magmaIslandsRate);
      prop.setComment("One in every X chunks will contain a magma island in the nether");
      magmaIslandsRate = prop.getInt();
      propOrder.add(prop.getName());

      prop = configFile.get(cat, "slimeIslandBlacklist", slimeIslandBlacklist);
      prop.setComment("Prevents generation of slime islands in the listed dimensions");
      slimeIslandBlacklist = prop.getIntList();
      propOrder.add(prop.getName());

      prop = configFile.get(cat, "slimeIslandsOnlyGenerateInSurfaceWorlds", slimeIslandsOnlyGenerateInSurfaceWorlds);
      prop.setComment("If false, slime islands only generate in dimensions which are of type surface. This means they won't generate in modded cave dimensions like the Deep Dark. Note that the name of this property is inverted: It must be set to false to prevent slime islands from generating in non-surface dimensions.");
      slimeIslandsOnlyGenerateInSurfaceWorlds = prop.getBoolean();
      propOrder.add(prop.getName());

      // Nether ore generation
      prop = configFile.get(cat, "genCobalt", genCobalt);
      prop.setComment("If true, cobalt ore will generate in the nether");
      genCobalt = prop.getBoolean();
      propOrder.add(prop.getName());

      prop = configFile.get(cat, "genArdite", genArdite);
      prop.setComment("If true, ardite ore will generate in the nether");
      genArdite = prop.getBoolean();
      propOrder.add(prop.getName());

      prop = configFile.get(cat, "cobaltRate", cobaltRate);
      prop.setComment("Approx Ores per chunk");
      cobaltRate = prop.getInt();
      propOrder.add(prop.getName());

      prop = configFile.get(cat, "arditeRate", arditeRate);
      arditeRate = prop.getInt();
      propOrder.add(prop.getName());

      Worldgen.setPropertyOrder(propOrder);
    }
    // Clientside
    {
      String cat = "clientside";
      List<String> propOrder = Lists.newArrayList();
      ClientSide = configFile.getCategory(cat);

      // rename renderTableItems to renderInventoryInWorld
      configFile.renameProperty(cat, "renderTableItems", "renderInventoryInWorld");

      prop = configFile.get(cat, "renderInventoryInWorld", renderTableItems);
      prop.setComment("If true all of Tinkers' blocks with contents (tables, basin, drying racks,...) will render their contents in the world");
      renderTableItems = prop.getBoolean();
      propOrder.add(prop.getName());

      prop = configFile.get(cat, "renderInventoryNullLayer", renderInventoryNullLayer);
      prop.setComment("If true use a null render layer when building the models to render tables. Fixes an issue with chisel, but the config is provide in case it breaks something.");
      renderInventoryNullLayer = prop.getBoolean();
      propOrder.add(prop.getName());

      prop = configFile.get(cat, "extraTooltips", extraTooltips);
      prop.setComment("If true tools will show additional info in their tooltips");
      extraTooltips = prop.getBoolean();
      propOrder.add(prop.getName());

      prop = configFile.get(cat, "listAllTables", listAllTables);
      prop.setComment("If true all variants of the different tables will be listed in creative. Set to false to only have the oak variant for all tables.");
      listAllTables = prop.getBoolean();
      propOrder.add(prop.getName());

      configFile.renameProperty(cat, "listAllMaterials", "listAllToolMaterials");
      prop = configFile.get(cat, "listAllToolMaterials", listAllToolMaterials);
      prop.setComment("If true all material variants of the different tools will be listed in creative. Set to false to only have the first found material for all tools (usually wood).");
      listAllToolMaterials = prop.getBoolean();
      propOrder.add(prop.getName());

      prop = configFile.get(cat, "listAllPartMaterials", listAllToolMaterials); // property was split, so defailt to the value of tool materials
      prop.setComment("If true all material variants of the different parts will be listed in creative. Set to false to only have the first found material for all parts (usually wood).");
      listAllPartMaterials = prop.getBoolean();
      propOrder.add(prop.getName());

      prop = configFile.get(cat, "temperatureCelsius", temperatureCelsius);
      prop.setComment("If true, temperatures in the smeltery and in JEI will display in celsius. If false they will use the internal units of Kelvin, which may be better for devs");
      temperatureCelsius = prop.getBoolean();
      propOrder.add(prop.getName());
      Util.setTemperaturePref(temperatureCelsius);

      prop = configFile.get(cat, "enableForgeBucketModel", enableForgeBucketModel);
      prop.setComment("If true tools will enable the forge bucket model on startup and then turn itself off. This is only there so that a fresh install gets the buckets turned on by default.");
      enableForgeBucketModel = prop.getBoolean();
      if(enableForgeBucketModel) {
        prop.set(false);
        ForgeModContainer.replaceVanillaBucketModel = true;
        Property forgeProp = ForgeModContainer.getConfig().getCategory(Configuration.CATEGORY_CLIENT).get("replaceVanillaBucketModel");
        if(forgeProp != null) {
          forgeProp.set(true);
          ForgeModContainer.getConfig().save();
        }
      }
      propOrder.add(prop.getName());

      prop = configFile.get(cat, "dumpTextureMap", dumpTextureMap);
      prop.setComment("REQUIRES DEBUG MODULE. Will do nothing if debug module is disabled. If true the texture map will be dumped into the run directory, just like old forge did.");
      dumpTextureMap = prop.getBoolean();
      propOrder.add(prop.getName());

      ClientSide.setPropertyOrder(propOrder);
    }

    // save changes if any
    boolean changed = false;
    if(configFile.hasChanged()) {
      configFile.save();
      changed = true;
    }
    if(pulseConfig.getConfig().hasChanged()) {
      pulseConfig.flush();
      changed = true;
    }
    return changed;
  }
}
