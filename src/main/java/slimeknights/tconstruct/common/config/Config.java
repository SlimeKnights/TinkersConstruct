package slimeknights.tconstruct.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.utils.Orientation2D;
import slimeknights.tconstruct.world.TinkerHeadType;
import slimeknights.tconstruct.world.TinkerStructures;

import java.util.EnumMap;
import java.util.Map;

public class Config {
  /**
   * Common specific configuration
   */
  public static class Common {

    public final BooleanValue shouldSpawnWithTinkersBook;

    // recipes
    public final BooleanValue addGravelToFlintRecipe;
    public final BooleanValue cheaperNetheriteAlloy;
    public final BooleanValue witherBoneDrop;
    public final BooleanValue witherBoneConversion;
    public final BooleanValue slimeRecipeFix;
    public final BooleanValue glassRecipeFix;
    public final Map<TinkerHeadType,BooleanValue> headDrops;

    // loot
    public final BooleanValue slimyLootChests;
    public final IntValue barterBlazingBlood;
    public final IntValue tinkerToolBonusChest;

    public final ConfigValue<Integer> melterNuggetsPerOre;
    public final ConfigValue<Integer> smelteryNuggetsPerOre;
    public final ConfigValue<Integer> foundryNuggetsPerOre;

    public final BooleanValue generateCobalt;
    public final ConfigValue<Integer> veinCountCobalt;

    public final BooleanValue generateCopper;
    public final ConfigValue<Integer> veinCountCopper;

    // overworld
    public final BooleanValue generateEarthSlimeIslands;
    public final BooleanValue generateSkySlimeIslands;
    public final BooleanValue generateClayIslands;
    public final IntValue earthSlimeIslandSeparation;
    public final IntValue skySlimeIslandSeparation;
    public final IntValue clayIslandSeparation;
    // nether
    public final BooleanValue generateBloodIslands;
    public final IntValue bloodIslandSeparation;
    // end
    public final BooleanValue generateEndSlimeIslands;
    public final IntValue endSlimeIslandSeparation;

    public final ConfigValue<String> showOnlyToolMaterial;
    public final ConfigValue<String> showOnlyPartMaterial;
    public final BooleanValue showAllTableVariants;
    public final BooleanValue showAllAnvilVariants;

    public final BooleanValue forceIntegrationMaterials;

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

      this.showOnlyToolMaterial = builder
        .comment("If non-empty, only this material will be shown on tools in creative and JEI (or the first valid material if this is invalid for the tool).", "If empty, all materials will show")
        .translation("tconstruct.configgui.showOnlyToolMaterial")
        .worldRestart()
        .define("showOnlyToolMaterial", "");

      this.showOnlyPartMaterial = builder
        .comment("If non-empty, only material will be shown on parts in creative and JEI (or the first valid material if this is invalid for the part).", "If empty, all materials will show")
        .translation("tconstruct.configgui.showOnlyPartMaterial")
        .worldRestart()
        .define("showOnlyPartMaterial", "");

      this.showAllTableVariants = builder
        .comment("If true, tables such as the part builder and tinker station will show all variants. If false they will show just the first entry in the tag, typically oak.")
        .translation("tconstruct.configgui.showAllTableVariants")
        .define("showAllTableVariants", true);

      this.showAllAnvilVariants = builder
        .comment("If true, anvils will show all metal variants. If false, only the first variant in the tag will show (typically tinkers bronze)")
        .translation("tconstruct.configgui.showAllAnvilVariants")
        .define("showAllAnvilVariants", true);

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

      this.slimeRecipeFix = builder
        .comment("Slimealls not being usable in vanilla recipes that require slimeballs. Config option exists to disable easily in case this fix is redundant to another mod")
        .worldRestart()
        .define("slimeRecipeFix", true);

      this.glassRecipeFix = builder
        .comment("Fixes clear glass not being usable in vanilla recipes that require glass. Config option exists to disable easily in case this fix is redundant to another mod")
        .translation("tconstruct.configgui.glassRecipeFix")
        .worldRestart()
        .define("glassRecipeFix", true);

      this.melterNuggetsPerOre = builder
        .comment("Number of nuggets produced when an ore block is melted in the melter. 9 would give 1 ingot")
        .translation("tconstruct.configgui.melterNuggetsPerOre")
        .defineInRange("melterNuggetsPerOre", 12, 1, 45);
      this.smelteryNuggetsPerOre = builder
        .comment("Number of nuggets produced when an ore block is melted in the smeltery. 9 nuggets would give 1 ingot")
        .translation("tconstruct.configgui.smelteryNuggetsPerOre")
        .defineInRange("smelteryNuggetsPerOre", 18, 1, 45);
      this.foundryNuggetsPerOre = builder
        .comment("Number of nuggets produced when an ore block is melted in the foundry. 9 nuggets would give 1 ingot")
        .translation("tconstruct.configgui.foundryNuggetsPerOre")
        .defineInRange("foundryNuggetsPerOre", 15, 1, 45);

      builder.comment("Entity head drops when killed by a charged creeper").push("heads");
      headDrops = new EnumMap<>(TinkerHeadType.class);
      for (TinkerHeadType headType : TinkerHeadType.values()) {
        headDrops.put(headType, builder
          .translation("tconstruct.configgui.heads." + headType.getString())
          .define(headType.getString(), true));
      }

      builder.pop(2);

      builder.comment(
        "Options related to loot table injections. Note some of the changes are done via global loot managers, these only control injecting loot into loot pools",
        "If your modpack makes extensive loot table changes, many of these may be automatically disabled. You can also manually set up tables for more control.").push("loot");

      slimyLootChests = builder
        .comment("Adds slimy saplings and seeds into various loot chests. Helps for worlds without slime islands")
        .worldRestart()
        .define("slimy_loot", true);
      barterBlazingBlood = builder
        .comment("Weight of blazing blood in the piglin bartering tables. Set to 0 to disable")
        .worldRestart()
        .defineInRange("barter_blazing_blood", 20, 0, 100);
      tinkerToolBonusChest = builder
        .comment("Weight of tinker tools in the vanilla spawn bonus chest, randomly replacing the vanilla axe or shovel. Tool will have a random tier 1 head and binding, plus a wooden handle. Set to 0 to disable.",
                 "For comparison, vanilla wooden axes and pickaxes have a weight of 3, and stone axes/pickaxes have a weight of 1")
        .worldRestart()
        .defineInRange("tinker_tool_bonus_chest", 2, 0, 25);

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

      builder.comment("Options related to slime islands").push("slime_islands");
      builder.comment("Options related to earth slime islands spawning in the oceans").push("earth");
      this.generateEarthSlimeIslands = builder
        .comment("If true, this island generates")
        .worldRestart()
        .define("generate", true);
      this.earthSlimeIslandSeparation = builder
        .comment("How many chunks on average between islands")
        .worldRestart()
        .defineInRange("separation", 25, 10, 500);
      builder.pop();

      builder.comment("Settings for sky slime islands in the overworld sky").push("sky");
      this.generateSkySlimeIslands = builder
        .comment("If true, this island generates")
        .worldRestart()
        .define("generate", true);
      this.skySlimeIslandSeparation = builder
        .comment("How many chunks on average between islands")
        .worldRestart()
        .defineInRange("separation", 30, 10, 500);
      builder.pop();

      builder.comment("Settings for clay islands in the overworld sky").push("clay");
      this.generateClayIslands = builder
        .comment("If true, this island generates")
        .worldRestart()
        .define("generate", true);
      this.clayIslandSeparation = builder
        .comment("How many chunks on average between islands")
        .worldRestart()
        .defineInRange("separation", 100, 10, 500);
      builder.pop();

      builder.comment("Settings for blood islands in the nether lava ocean").push("blood");
      this.generateBloodIslands = builder
        .comment("If true, this island generates")
        .worldRestart()
        .define("generate", true);
      this.bloodIslandSeparation = builder
        .comment("How many chunks on average between islands")
        .worldRestart()
        .defineInRange("separation", 13, 10, 500);
      builder.pop();

      builder.comment("Settings for end slime islands in the outer end islands").push("end");
      this.generateEndSlimeIslands = builder
        .comment("If true, this island generates")
        .worldRestart()
        .define("generate", true);
      this.endSlimeIslandSeparation = builder
        .comment("How many chunks on average between islands")
        .worldRestart()
        .defineInRange("separation", 25, 10, 500);
      builder.pop(2);

      builder.pop();

      builder.comment("Features to use in debugging gameplay and mechanics, generally should not be enabled in packs").push("debug");
      this.forceIntegrationMaterials = builder
        .comment("If true, forces integration materials to be enabled, even if the relevant metal is missing. Useful for testing material balance.",
                 "Does not provide recipes for any of them, they will only be available to cheat in creative.")
        .worldRestart()
        .define("forceIntegrationMaterials", false);
      builder.pop();
    }
  }

  /**
   * Client specific configuration - only loaded clientside from tconstruct-client.toml
   */
  public static class Client {
    //public final ForgeConfigSpec.BooleanValue temperatureInCelsius;
    public final ForgeConfigSpec.BooleanValue tankFluidModel;
    public final ForgeConfigSpec.BooleanValue extraToolTips;
    public final ForgeConfigSpec.BooleanValue logMissingMaterialTextures;
    public final ForgeConfigSpec.BooleanValue logMissingModifierTextures;
    public final ForgeConfigSpec.BooleanValue showModifiersInJEI;
    public final ForgeConfigSpec.BooleanValue renderShieldSlotItem;
    public final ForgeConfigSpec.IntValue maxSmelteryItemQuads;

    // framed modifier
    public final ForgeConfigSpec.BooleanValue renderItemFrame;
    public final ForgeConfigSpec.IntValue itemFrameXOffset;
    public final ForgeConfigSpec.IntValue itemFrameYOffset;
    public final ForgeConfigSpec.EnumValue<Orientation2D> itemFrameLocation;
    public final ForgeConfigSpec.IntValue itemsPerRow;

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

      this.logMissingMaterialTextures = builder
        .comment("If true, the game will log all material textures which do not exist in resource packs but can be added, can be helpful for moddevs or resourcepack makers")
        .translation("tconstruct.configgui.logMissingMaterialTextures")
        .define("logMissingMaterialTextures", false);

      this.logMissingModifierTextures = builder
        .comment("If true, the game will log all modifier textures which do not exist in resource packs but can be added, can be helpful for moddevs or resourcepack makers")
        .translation("tconstruct.configgui.logMissingMaterialTextures")
        .define("logMissingModifierTextures", false);

      this.showModifiersInJEI = builder
        .comment("If true, modifiers will be added to the JEI ingredient list. If false, they will only be visible in the modifiers recipe tab.")
        .translation("tconstruct.configgui.showModifiersInJEI")
        .define("showModifiersInJEI", true);

      this.maxSmelteryItemQuads = builder
        .comment("Maximum number of quads to render for items in the smeltery. Most blocks are about 6 quads, items like ingots are around 26.",
                 "Setting this lower will cause fewer items to be renderer (but never a partial item). Set to -1 to allow unlimited quads, and 0 to disable the item renderer.")
        .defineInRange("maxSmelteryItemQuads", 3500, -1, Short.MAX_VALUE);

      builder.comment("Settings related to modifiers").push("modifiers");
      {

        this.renderShieldSlotItem = builder
          .comment("If true, the shield slot legging modifier will render the next offhand item above the offhand slot.")
          .define("renderShieldSlotItem", true);

        builder.comment("Settings related to the frame helmet modifier").push("itemFrame");
        {
          this.renderItemFrame = builder
            .comment("If true, the item frame modifier for helmets will render its items. Turning this to false makes the modifier useless.")
            .define("render", true);
          this.itemFrameXOffset = builder
            .comment("Offset in the X direction for the frame items.")
            .defineInRange("xOffset", 0, Short.MIN_VALUE, Short.MAX_VALUE);
          this.itemFrameYOffset = builder
            .comment("Offset in the Y direction for the frame items.")
            .defineInRange("yOffset", 0, Short.MIN_VALUE, Short.MAX_VALUE);
          this.itemFrameLocation = builder
            .comment("Location of the frame on the screen.")
            .defineEnum("location", Orientation2D.TOP_LEFT);
          this.itemsPerRow = builder
            .comment("Number of items to display in each row of the item frame.")
            .defineInRange("itemsPerRow", 5, 0, 100);
        }
        builder.pop();
      }
      builder.pop();

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

  /** Registers any relevant listeners for config */
  public static void init() {
    ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.commonSpec);
    ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.clientSpec);

    IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    bus.addListener(Config::configChanged);
  }

  /** Called when config reloaded to update cached settings */
  private static void configChanged(ModConfig.Reloading event) {
    ModConfig config = event.getConfig();
    if (config.getModId().equals(TConstruct.MOD_ID)) {
      ForgeConfigSpec spec = config.getSpec();
      if (spec == Config.commonSpec) {
        TinkerStructures.addStructureSeparation();
      }
    }
  }
}
