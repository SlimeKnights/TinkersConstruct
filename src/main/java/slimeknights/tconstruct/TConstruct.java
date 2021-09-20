package slimeknights.tconstruct;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.event.RegistryEvent.MissingMappings;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.registration.RegistrationHelper;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.common.data.AdvancementsProvider;
import slimeknights.tconstruct.common.data.loot.TConstructLootTableProvider;
import slimeknights.tconstruct.common.data.tags.BlockTagProvider;
import slimeknights.tconstruct.common.data.tags.EntityTypeTagProvider;
import slimeknights.tconstruct.common.data.tags.FluidTagProvider;
import slimeknights.tconstruct.common.data.tags.ItemTagProvider;
import slimeknights.tconstruct.common.data.tags.TileEntityTypeTagProvider;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.gadgets.entity.FrameType;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionLoader;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayoutLoader;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.plugin.crt.CRTHelper;
import slimeknights.tconstruct.shared.TinkerClient;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock.TankType;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.TinkerWorld;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Random;

/**
 * TConstruct, the tool mod. Craft your tools with style, then modify until the original is gone!
 *
 * @author mDiyo
 */

@SuppressWarnings("unused")
@Mod(TConstruct.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class TConstruct {

  public static final String MOD_ID = "tconstruct";
  public static final Logger LOG = LogManager.getLogger(MOD_ID);
  public static final Random RANDOM = new Random();

  /* Instance of this mod, used for grabbing prototype fields */
  public static TConstruct instance;

  public TConstruct() {
    instance = this;

    Config.init();

    // initialize modules, done this way rather than with annotations to give us control over the order
    IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    // base
    bus.register(new TinkerCommons());
    bus.register(new TinkerFluids());
    bus.register(new TinkerGadgets());
    // world
    bus.register(new TinkerWorld());
    bus.register(new TinkerStructures());
    // tools
    bus.register(new TinkerTables());
    bus.register(new TinkerMaterials());
    bus.register(new TinkerModifiers());
    bus.register(new TinkerToolParts());
    bus.register(new TinkerTools());
    // smeltery
    bus.register(new TinkerSmeltery());

    // init deferred registers
    TinkerModule.initRegisters();
    TinkerNetwork.setup();
    TinkerTags.init();
    // init client logic
    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> TinkerClient::onConstruct);
    MinecraftForge.EVENT_BUS.register(this);
    if (ModList.get().isLoaded("crafttweaker")) {
      MinecraftForge.EVENT_BUS.register(new CRTHelper());
    }
  }

  @SubscribeEvent
  static void commonSetup(final FMLCommonSetupEvent event) {
    MaterialRegistry.init();
    ToolDefinitionLoader.init();
    StationSlotLayoutLoader.init();
  }

  @SubscribeEvent
  static void gatherData(final GatherDataEvent event) {
    if (event.includeServer()) {
      DataGenerator datagenerator = event.getGenerator();
      ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
      BlockTagProvider blockTags = new BlockTagProvider(datagenerator, existingFileHelper);
      datagenerator.addProvider(blockTags);
      datagenerator.addProvider(new ItemTagProvider(datagenerator, blockTags, existingFileHelper));
      datagenerator.addProvider(new FluidTagProvider(datagenerator, existingFileHelper));
      datagenerator.addProvider(new EntityTypeTagProvider(datagenerator, existingFileHelper));
      datagenerator.addProvider(new TileEntityTypeTagProvider(datagenerator, existingFileHelper));
      datagenerator.addProvider(new TConstructLootTableProvider(datagenerator));
      datagenerator.addProvider(new AdvancementsProvider(datagenerator));
    }
  }

  @Nullable
  private static Block missingBlock(String name) {
    switch (name) {
      // prefix with seared
      case "faucet": return TinkerSmeltery.searedFaucet.get();
      case "channel": return TinkerSmeltery.searedChannel.get();
      case "casting_table": return TinkerSmeltery.searedTable.get();
      case "casting_basin": return TinkerSmeltery.searedBasin.get();
      case "melter": return TinkerSmeltery.searedMelter.get();
      // tank renames
      case "seared_tank": return TinkerSmeltery.searedTank.get(TankType.FUEL_TANK);
      case "seared_gauge": return TinkerSmeltery.searedTank.get(TankType.INGOT_GAUGE);
      case "seared_window": return TinkerSmeltery.searedTank.get(TankType.INGOT_TANK);
      case "scorched_tank": return TinkerSmeltery.scorchedTank.get(TankType.FUEL_TANK);
      case "scorched_gauge": return TinkerSmeltery.scorchedTank.get(TankType.INGOT_GAUGE);
      case "scorched_window": return TinkerSmeltery.scorchedTank.get(TankType.INGOT_TANK);
      // magma cream -> magma
      case "magma_cream_fluid": return TinkerFluids.magma.getBlock();
      // molten blaze -> blazing blood
      case "molten_blaze_fluid": return TinkerFluids.blazingBlood.getBlock();
      // old gadgets that are removed
      case "stone_ladder": return Blocks.LADDER;
      case "stone_torch": return Blocks.TORCH;
      case "wall_stone_torch": return Blocks.WALL_TORCH;
      case "wooden_rail": case "wooden_dropper_rail": return Blocks.RAIL;
      // dried blocks to remove
      case "dried_clay": return Blocks.TERRACOTTA;
      case "dried_clay_bricks": return Blocks.BRICKS;
      case "dried_clay_slab": case "dried_clay_bricks_slab": return Blocks.BRICK_SLAB;
      case "dried_clay_stairs": case "dried_clay_bricks_stairs": return Blocks.BRICK_STAIRS;
      // chest rename
      case "modifier_chest": return TinkerTables.tinkersChest.get();
    }
    return null;
  }

  @SubscribeEvent
  void missingItems(final MissingMappings<Item> event) {
    RegistrationHelper.handleMissingMappings(event, MOD_ID, name -> {
      switch (name) {
        // kama head removed, sword blade to small blade
        case "sword_blade": return TinkerToolParts.smallBlade.get();
        case "sword_blade_cast": return TinkerSmeltery.smallBladeCast.get();
        case "sword_blade_sand_cast": return TinkerSmeltery.smallBladeCast.getSand();
        case "sword_blade_red_sand_cast": return TinkerSmeltery.smallBladeCast.getRedSand();
        //  reinforcement splitting
        case "reinforcement": return TinkerModifiers.ironReinforcement.get();
        // magma cream -> magma
        case "magma_cream_bucket": return TinkerFluids.magma.asItem();
        // molten blaze -> blazing blood
        case "molten_blaze_bucket": return TinkerFluids.blazingBlood.asItem();
        // old foods, some will move to natura
        case "monster_jerky": return Items.ROTTEN_FLESH;
        case "beef_jerky": return Items.COOKED_BEEF;
        case "chicken_jerky": return Items.COOKED_CHICKEN;
        case "pork_jerky": return Items.COOKED_PORKCHOP;
        case "mutton_jerky": return Items.COOKED_MUTTON;
        case "rabbit_jerky": return Items.COOKED_RABBIT;
        case "fish_jerky": return Items.COOKED_COD;
        case "salmon_jerky": return Items.COOKED_SALMON;
        case "clownfish_jerky": return Items.TROPICAL_FISH;
        case "pufferfish_jerky": return Items.PUFFERFISH;
        case "earth_slime_drop": return Items.SLIME_BALL;
        case "sky_slime_drop": return TinkerCommons.slimeball.get(SlimeType.SKY);
        case "ichor_slime_drop": return TinkerCommons.slimeball.get(SlimeType.ICHOR);
        case "blood_slime_drop": return TinkerCommons.slimeball.get(SlimeType.BLOOD);
        case "ender_slime_drop": return TinkerCommons.slimeball.get(SlimeType.ENDER);
        case "stone_stick": return Blocks.COBBLESTONE.asItem();
        case "dried_brick": return Items.BRICK;
        // removed ancient heads, use netherite directly
        case "ancient_axe_head": case "ancient_shovel_head": case "ancient_hoe_head": return Items.NETHERITE_SCRAP;
        // creative slot revamp
        case "creative_upgrade": case "creative_ability": return TinkerModifiers.creativeSlotItem.get();
        // frame migration
        case "cobalt_item_frame": return TinkerGadgets.itemFrame.get(FrameType.DIAMOND);
        case "jewel_item_frame": return TinkerGadgets.itemFrame.get(FrameType.GOLD);
        // slime boots to modifier
        case "earth_slime_boots": return TinkerWorld.slime.get(SlimeType.EARTH).asItem();
        case "sky_slime_boots": return TinkerWorld.slime.get(SlimeType.SKY).asItem();
        case "ichor_slime_boots": return TinkerWorld.slime.get(SlimeType.ICHOR).asItem();
        case "ender_slime_boots": return TinkerWorld.slime.get(SlimeType.ENDER).asItem();
        case "blood_slime_boots": return TinkerWorld.slime.get(SlimeType.BLOOD).asItem();
      }
      IItemProvider block = missingBlock(name);
      return block == null ? null : block.asItem();
    });
  }

  @SubscribeEvent
  void missingBlocks(final MissingMappings<Block> event) {
    RegistrationHelper.handleMissingMappings(event, MOD_ID, TConstruct::missingBlock);
  }

  @SubscribeEvent
  void missingFluids(final MissingMappings<Fluid> event) {
    RegistrationHelper.handleMissingMappings(event, MOD_ID, name -> {
      switch(name) {
        // magma cream -> magma
        case "magma_cream": return TinkerFluids.magma.get();
        case "flowing_magma_cream": return TinkerFluids.magma.getFlowing();
        // molten blaze -> blazing blood
        case "molten_blaze": return TinkerFluids.blazingBlood.get();
        case "flowing_molten_blaze": return TinkerFluids.blazingBlood.getFlowing();
      }
      return null;
    });
  }

  @SubscribeEvent
  void missingModifiers(final MissingMappings<Modifier> event) {
    RegistrationHelper.handleMissingMappings(event, MOD_ID, name -> {
      switch(name) {
        case "beheading": return TinkerModifiers.severing.get();
        case "bane_of_arthropods": return TinkerModifiers.baneOfSssss.get();
        case "duel_wielding": return TinkerModifiers.dualWielding.get();
        // creative modifier remake
        case "creative_upgrade": return TinkerModifiers.redExtraUpgrade.get();
        case "creative_ability": return TinkerModifiers.extraAbility.get();
      }
      return null;
    });
  }


  /* Utils */

  /**
   * Gets a resource location for Tinkers
   * @param name  Resource path
   * @return  Location for tinkers
   */
  public static ResourceLocation getResource(String name) {
    return new ResourceLocation(MOD_ID, name);
  }

  /**
   * Returns the given Resource prefixed with tinkers resource location. Use this function instead of hardcoding
   * resource locations.
   */
  public static String resourceString(String res) {
    return String.format("%s:%s", MOD_ID, res);
  }

  /**
   * Prefixes the given unlocalized name with tinkers prefix. Use this when passing unlocalized names for a uniform
   * namespace.
   */
  public static String prefix(String name) {
    return String.format("%s.%s", MOD_ID, name.toLowerCase(Locale.US));
  }

  /**
   * Makes a translation key for the given name
   * @param base  Base name, such as "block" or "gui"
   * @param name  Object name
   * @return  Translation key
   */
  public static String makeTranslationKey(String base, String name) {
    return Util.makeTranslationKey(base, getResource(name));
  }

  /**
   * Makes a translation text component for the given name
   * @param base  Base name, such as "block" or "gui"
   * @param name  Object name
   * @return  Translation key
   */
  public static IFormattableTextComponent makeTranslation(String base, String name) {
    return new TranslationTextComponent(makeTranslationKey(base, name));
  }

  /**
   * Makes a translation text component for the given name
   * @param base       Base name, such as "block" or "gui"
   * @param name       Object name
   * @param arguments  Additional arguments to the translation
   * @return  Translation key
   */
  public static IFormattableTextComponent makeTranslation(String base, String name, Object... arguments) {
    return new TranslationTextComponent(makeTranslationKey(base, name), arguments);
  }
}
