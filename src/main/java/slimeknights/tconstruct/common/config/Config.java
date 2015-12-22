package slimeknights.tconstruct.common.config;

import com.google.common.collect.Lists;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

import slimeknights.mantle.pulsar.pulse.PulseMeta;
import slimeknights.tconstruct.TConstruct;

public class Config {

  public static Config instance = new Config();
  private Config() {}

  public static boolean forceRegisterAll = false;

  // Tools and general
  public static boolean reuseStencil = true;
  public static boolean craftCastableMaterials = true;
  public static boolean chestsKeepInventory = true;

  // Worldgen
  public static boolean genSlimeIslands = true;
  public static boolean genIslandsInSuperflat = false;
  public static int slimeIslandsRate = 300; // Every x-th chunk will have a slime island. so 1 = every chunk, 100 = every 100th
  public static int[] slimeIslandBlacklist = new int[] {-1, 1};
  public static boolean genCobalt = true;
  public static int cobaltRate = 16; // max. cobalt per chunk
  public static boolean genArdite = true;
  public static int arditeRate = 16; // max. ardite per chunk

  // Clientside configs
  public static boolean renderTableItems = true;
  public static boolean extraTooltips = true;


  /* Config File */

  static Configuration configFile;

  static ConfigCategory Modules;
  static ConfigCategory Worldgen;
  static ConfigCategory ClientSide;

  public static void load(FMLPreInitializationEvent event) {
    configFile = new Configuration(event.getSuggestedConfigurationFile(), "0.1", false);

    MinecraftForge.EVENT_BUS.register(instance);

    syncConfig();
  }

  @SubscribeEvent
  public void update(ConfigChangedEvent.OnConfigChangedEvent event) {
    if(event.modID.equals(TConstruct.modID)) {
      syncConfig();
    }
  }


  public static boolean syncConfig() {
    Property prop;

    // Modules
    {
      List<String> propOrder = Lists.newArrayList();
      // convert pulse config to MC compatible config for GUI config
      Modules = new ConfigCategory("modules");
      for(PulseMeta pm : TConstruct.pulseManager.getAllPulseMetadata()) {
        if(pm.isForced()) continue;
        prop = new Property(pm.getId(), pm.isDefaultEnabled() ? "true" : "false", Property.Type.BOOLEAN);
        prop.setValue(pm.isEnabled());
        prop.requiresMcRestart();
        Modules.put(pm.getId(), prop);
        propOrder.add(prop.getName());
      }
      Modules.setPropertyOrder(propOrder);
    }
    // Worldgen
    {
      String cat = "worldgen";
      List<String> propOrder = Lists.newArrayList();
      Worldgen = configFile.getCategory(cat);

      // Slime Islands
      prop = configFile.get(cat, "generateSlimeIslands", genSlimeIslands);
      prop.comment = "If true slime islands will generate";
      genSlimeIslands = prop.getBoolean();
      propOrder.add(prop.getName());

      prop = configFile.get(cat, "generateIslandsInSuperflat", genIslandsInSuperflat);
      prop.comment = "If true slime islands generate in superflat worlds";
      genIslandsInSuperflat = prop.getBoolean();
      propOrder.add(prop.getName());

      prop = configFile.get(cat, "slimeIslandRate", slimeIslandsRate);
      prop.comment = "One in every X chunks will contain a slime island";
      slimeIslandsRate = prop.getInt();
      propOrder.add(prop.getName());

      prop = configFile.get(cat, "slimeIslandBlacklist", slimeIslandBlacklist);
      prop.comment = "Prevents generation of slime islands in the listed dimensions";
      slimeIslandBlacklist = prop.getIntList();
      propOrder.add(prop.getName());

      // Nether ore generation
      prop = configFile.get(cat, "genCobalt", genCobalt);
      prop.comment = "If true, cobalt ore will generate in the nether";
      genCobalt = prop.getBoolean();
      propOrder.add(prop.getName());

      prop = configFile.get(cat, "genArdite", genArdite);
      prop.comment = "If true, ardite ore will generate in the nether";
      genArdite = prop.getBoolean();
      propOrder.add(prop.getName());

      prop = configFile.get(cat, "cobaltRate", cobaltRate);
      prop.comment = "Approx Ores per chunk";
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
      prop.comment = "If true all of Tinkers' tables will render their contents on top of the table";
      renderTableItems = prop.getBoolean();
      propOrder.add(prop.getName());

      prop = configFile.get(cat, "extraTooltips", extraTooltips);
      prop.comment = "If true tools will show additional info in their tooltips";
      extraTooltips = prop.getBoolean();
      propOrder.add(prop.getName());

      ClientSide.setPropertyOrder(propOrder);
    }

    // save changes if any
    if(configFile.hasChanged()) {
      configFile.save();
      return true;
    }
    return false;
  }
}
