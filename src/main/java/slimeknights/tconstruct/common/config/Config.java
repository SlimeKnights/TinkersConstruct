package slimeknights.tconstruct.common.config;

import com.google.common.collect.Lists;

import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.apache.logging.log4j.Logger;

import java.util.List;

import slimeknights.mantle.pulsar.config.ForgeCFG;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.Util;

public class Config {

  public static ForgeCFG pulseConfig = new ForgeCFG("TinkerModules", "Modules");
  public static Config instance = new Config();
  public static Logger log = Util.getLogger("Config");
  private Config() {}


  public static boolean forceRegisterAll = false; // enables all common items, even if their module is not present

  // Tools and general
  public static boolean reuseStencil = true;
  public static boolean craftCastableMaterials = false;
  public static boolean chestsKeepInventory = true;
  public static boolean autosmeltlapis = true;
  public static boolean obsidianAlloy = true;
  public static boolean claycasts = true;

  // Worldgen
  public static boolean genSlimeIslands = true;
  public static boolean genIslandsInSuperflat = false;
  public static int slimeIslandsRate = 730; // Every x-th chunk will have a slime island. so 1 = every chunk, 100 = every 100th
  public static int magmaIslandsRate = 100; // Every x-th chunk will have a slime island. so 1 = every chunk, 100 = every 100th
  public static int[] slimeIslandBlacklist = new int[] {-1, 1};
  public static boolean genCobalt = true;
  public static int cobaltRate = 16; // max. cobalt per chunk
  public static boolean genArdite = true;
  public static int arditeRate = 16; // max. ardite per chunk

  // Clientside configs
  public static boolean renderTableItems = true;
  public static boolean extraTooltips = true;
  public static boolean enableForgeBucketModel = true; // enables the forge bucket model by default


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
      prop.requiresMcRestart();
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
      prop.requiresMcRestart();
      propOrder.add(prop.getName());

      prop = configFile.get(cat, "obsidianAlloy", obsidianAlloy);
      prop.setComment("Allows the creation of obsidian in the smeltery, using a bucket of lava and water.");
      obsidianAlloy = prop.getBoolean();
      prop.requiresMcRestart();
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

      prop = configFile.get(cat, "renderTableItems", renderTableItems);
      prop.setComment("If true all of Tinkers' tables will render their contents on top of the table");
      renderTableItems = prop.getBoolean();
      propOrder.add(prop.getName());

      prop = configFile.get(cat, "extraTooltips", extraTooltips);
      prop.setComment("If true tools will show additional info in their tooltips");
      extraTooltips = prop.getBoolean();
      propOrder.add(prop.getName());

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
