package slimeknights.tconstruct.shared;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.block.StairsBaseBlock;
import slimeknights.mantle.item.EdibleItem;
import slimeknights.mantle.item.GeneratedItem;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.ServerProxy;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.common.conditions.ConfigOptionEnabledCondition;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.common.item.TinkerBookItem;
import slimeknights.tconstruct.library.TinkerPulseIds;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.shared.block.*;
import slimeknights.tconstruct.shared.item.AlubrassItem;

/**
 * Contains items and blocks and stuff that is shared by multiple pulses, but might be required individually
 */
@Pulse(id = TinkerPulseIds.TINKER_COMMONS_PULSE_ID, forced = true)
@ObjectHolder(TConstruct.modID)
public class TinkerCommons extends TinkerPulse {

  static final Logger log = Util.getLogger(TinkerPulseIds.TINKER_COMMONS_PULSE_ID);

  public static ServerProxy proxy = DistExecutor.runForDist(() -> CommonsClientProxy::new, () -> ServerProxy::new);

  public static final GroutBlock grout = null;

  public static final GraveyardSoilBlock graveyard_soil = null;
  public static final ConsecratedSoilBlock consecrated_soil = null;

  public static final SlimyMudBlock slimy_mud_green = null;
  public static final SlimyMudBlock slimy_mud_blue = null;
  public static final SlimyMudBlock slimy_mud_magma = null;

  public static final SlimeBlock blue_slime = null;
  public static final SlimeBlock purple_slime = null;
  public static final SlimeBlock blood_slime = null;
  public static final SlimeBlock magma_slime = null;
  public static final SlimeBlock pink_slime = null;

  public static final CongealedSlimeBlock congealed_green_slime = null;
  public static final CongealedSlimeBlock congealed_blue_slime = null;
  public static final CongealedSlimeBlock congealed_purple_slime = null;
  public static final CongealedSlimeBlock congealed_blood_slime = null;
  public static final CongealedSlimeBlock congealed_magma_slime = null;
  public static final CongealedSlimeBlock congealed_pink_slime = null;

  public static final OreBlock cobalt_ore = null;
  public static final OreBlock ardite_ore = null;

  public static FirewoodBlock lavawood;
  public static FirewoodBlock firewood;

  public static DecoGroundBlock mud_bricks;

  public static final ClearGlassBlock clear_glass = null;

  public static final ClearStainedGlassBlock white_clear_stained_glass = null;
  public static final ClearStainedGlassBlock orange_clear_stained_glass = null;
  public static final ClearStainedGlassBlock magenta_clear_stained_glass = null;
  public static final ClearStainedGlassBlock light_blue_clear_stained_glass = null;
  public static final ClearStainedGlassBlock yellow_clear_stained_glass = null;
  public static final ClearStainedGlassBlock lime_clear_stained_glass = null;
  public static final ClearStainedGlassBlock pink_clear_stained_glass = null;
  public static final ClearStainedGlassBlock gray_clear_stained_glass = null;
  public static final ClearStainedGlassBlock light_gray_clear_stained_glass = null;
  public static final ClearStainedGlassBlock cyan_clear_stained_glass = null;
  public static final ClearStainedGlassBlock purple_clear_stained_glass = null;
  public static final ClearStainedGlassBlock blue_clear_stained_glass = null;
  public static final ClearStainedGlassBlock brown_clear_stained_glass = null;
  public static final ClearStainedGlassBlock green_clear_stained_glass = null;
  public static final ClearStainedGlassBlock red_clear_stained_glass = null;
  public static final ClearStainedGlassBlock black_clear_stained_glass = null;

  public static final DecoGroundSlabBlock mud_bricks_slab = null;
  public static final FirewoodSlabBlock lavawood_slab = null;
  public static final FirewoodSlabBlock firewood_slab = null;

  public static final StairsBaseBlock mud_bricks_stairs = null;
  public static final StairsBaseBlock firewood_stairs = null;
  public static final StairsBaseBlock lavawood_stairs = null;

  public static final MetalBlock cobalt_block = null;
  public static final MetalBlock ardite_block = null;
  public static final MetalBlock manyullyn_block = null;
  public static final MetalBlock knightslime_block = null;
  public static final MetalBlock pigiron_block = null;
  public static final MetalBlock alubrass_block = null;
  public static final MetalBlock silky_jewel_block = null;

  public static final GlowBlock glow = null;

  public static final TinkerBookItem book = null;

  public static final GeneratedItem seared_brick = null;
  public static final GeneratedItem mud_brick = null;

  public static final EdibleItem blue_slime_ball = null;
  public static final EdibleItem purple_slime_ball = null;
  public static final EdibleItem blood_slime_ball = null;
  public static final EdibleItem magma_slime_ball = null;
  public static final EdibleItem pink_slime_ball = null;

  public static final GeneratedItem cobalt_nugget = null;
  public static final GeneratedItem cobalt_ingot = null;
  public static final GeneratedItem ardite_nugget = null;
  public static final GeneratedItem ardite_ingot = null;
  public static final GeneratedItem manyullyn_nugget = null;
  public static final GeneratedItem manyullyn_ingot = null;
  public static final GeneratedItem pigiron_nugget = null;
  public static final GeneratedItem pigiron_ingot = null;
  public static final GeneratedItem alubrass_nugget = null;
  public static final AlubrassItem alubrass_ingot = null;

  public static final EdibleItem bacon = null;

  public static final GeneratedItem green_slime_crystal = null;
  public static final GeneratedItem blue_slime_crystal = null;
  public static final GeneratedItem magma_slime_crystal = null;
  public static final GeneratedItem width_expander = null;
  public static final GeneratedItem height_expander = null;
  public static final GeneratedItem reinforcement = null;
  public static final GeneratedItem silky_cloth = null;
  public static final GeneratedItem silky_jewel = null;
  public static final GeneratedItem necrotic_bone = null;
  public static final GeneratedItem moss = null;
  public static final GeneratedItem mending_moss = null;
  public static final GeneratedItem creative_modifier = null;

  public static final GeneratedItem knightslime_nugget = null;
  public static final GeneratedItem knightslime_ingot = null;

  public static final GeneratedItem dried_brick = null;

  public static final EdibleItem monster_jerky = null;
  public static final EdibleItem beef_jerky = null;
  public static final EdibleItem chicken_jerky = null;
  public static final EdibleItem pork_jerky = null;
  public static final EdibleItem mutton_jerky = null;
  public static final EdibleItem rabbit_jerky = null;
  public static final EdibleItem fish_jerky = null;
  public static final EdibleItem salmon_jerky = null;
  public static final EdibleItem clownfish_jerky = null;
  public static final EdibleItem pufferfish_jerky = null;
  public static final EdibleItem green_slime_drop = null;
  public static final EdibleItem blue_slime_drop = null;
  public static final EdibleItem purple_slime_drop = null;
  public static final EdibleItem blood_slime_drop = null;
  public static final EdibleItem magma_slime_drop = null;

  @SubscribeEvent
  public void registerBlocks(final RegistryEvent.Register<Block> event) {
    IForgeRegistry<Block> registry = event.getRegistry();
    boolean forced = Config.forceRegisterAll; // causes to always register all items

    // Soils
    register(registry, new GroutBlock(), "grout");

    register(registry, new GraveyardSoilBlock(), "graveyard_soil");
    register(registry, new ConsecratedSoilBlock(), "consecrated_soil");

    register(registry, new SlimyMudBlock(SlimyMudBlock.MudType.SLIMY_MUD_GREEN), "slimy_mud_green");
    register(registry, new SlimyMudBlock(SlimyMudBlock.MudType.SLIMY_MUD_BLUE), "slimy_mud_blue");
    register(registry, new SlimyMudBlock(SlimyMudBlock.MudType.SLIMY_MUD_MAGMA), "slimy_mud_magma");

    // Slimes
    register(registry, new SlimeBlock(true), "blue_slime");
    register(registry, new SlimeBlock(true), "purple_slime");
    register(registry, new SlimeBlock(true), "blood_slime");
    register(registry, new SlimeBlock(true), "magma_slime");
    register(registry, new SlimeBlock(false), "pink_slime");

    register(registry, new CongealedSlimeBlock(true), "congealed_green_slime");
    register(registry, new CongealedSlimeBlock(true), "congealed_blue_slime");
    register(registry, new CongealedSlimeBlock(true), "congealed_purple_slime");
    register(registry, new CongealedSlimeBlock(true), "congealed_blood_slime");
    register(registry, new CongealedSlimeBlock(true), "congealed_magma_slime");
    register(registry, new CongealedSlimeBlock(false), "congealed_pink_slime");

    // Ores
    register(registry, new OreBlock(), "cobalt_ore");
    register(registry, new OreBlock(), "ardite_ore");

    // Firewood
    lavawood = register(registry, new FirewoodBlock(), "lavawood");
    firewood = register(registry, new FirewoodBlock(), "firewood");

    // Decorative Stuff
    mud_bricks = register(registry, new DecoGroundBlock(), "mud_bricks");

    register(registry, new ClearGlassBlock(), "clear_glass");

    register(registry, new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.WHITE), "white_clear_stained_glass");
    register(registry, new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.ORANGE), "orange_clear_stained_glass");
    register(registry, new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.MAGENTA), "magenta_clear_stained_glass");
    register(registry, new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.LIGHT_BLUE), "light_blue_clear_stained_glass");
    register(registry, new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.YELLOW), "yellow_clear_stained_glass");
    register(registry, new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.LIME), "lime_clear_stained_glass");
    register(registry, new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.PINK), "pink_clear_stained_glass");
    register(registry, new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.GRAY), "gray_clear_stained_glass");
    register(registry, new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.LIGHT_GRAY), "light_gray_clear_stained_glass");
    register(registry, new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.CYAN), "cyan_clear_stained_glass");
    register(registry, new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.PURPLE), "purple_clear_stained_glass");
    register(registry, new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.BLUE), "blue_clear_stained_glass");
    register(registry, new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.BROWN), "brown_clear_stained_glass");
    register(registry, new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.GREEN), "green_clear_stained_glass");
    register(registry, new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.RED), "red_clear_stained_glass");
    register(registry, new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.BLACK), "black_clear_stained_glass");

    // Slabs
    register(registry, new DecoGroundSlabBlock(), "mud_bricks_slab");
    register(registry, new FirewoodSlabBlock(), "lavawood_slab");
    register(registry, new FirewoodSlabBlock(), "firewood_slab");

    // Stairs
    register(registry, new StairsBaseBlock(mud_bricks), "mud_bricks_stairs");
    register(registry, new StairsBaseBlock(lavawood), "lavawood_stairs");
    register(registry, new StairsBaseBlock(firewood), "firewood_stairs");

    // Metals
    if (isToolsLoaded() || isSmelteryLoaded() || forced) {
      register(registry, new MetalBlock(MetalBlock.MetalType.COBALT), "cobalt_block");
      register(registry, new MetalBlock(MetalBlock.MetalType.ARDITE), "ardite_block");
      register(registry, new MetalBlock(MetalBlock.MetalType.MANYULLYN), "manyullyn_block");
      register(registry, new MetalBlock(MetalBlock.MetalType.KNIGHTSLIME), "knightslime_block");
      register(registry, new MetalBlock(MetalBlock.MetalType.PIGIRON), "pigiron_block");
      register(registry, new MetalBlock(MetalBlock.MetalType.ALUBRASS), "alubrass_block");
      register(registry, new MetalBlock(MetalBlock.MetalType.SILKY_JEWEL), "silky_jewel_block");
    }

    if (isToolsLoaded() || isGadgetsLoaded() || forced) {
      register(registry, new GlowBlock(), "glow");
    }
  }

  @SubscribeEvent
  public void registerItems(final RegistryEvent.Register<Item> event) {
    IForgeRegistry<Item> registry = event.getRegistry();
    boolean forced = Config.forceRegisterAll; // causes to always register all items

    register(registry, new TinkerBookItem(), "book");

    // Soils
    registerBlockItem(registry, grout, TinkerRegistry.tabGeneral);

    registerBlockItem(registry, graveyard_soil, TinkerRegistry.tabGeneral);
    registerBlockItem(registry, consecrated_soil, TinkerRegistry.tabGeneral);

    registerBlockItem(registry, slimy_mud_green, TinkerRegistry.tabGeneral);
    registerBlockItem(registry, slimy_mud_blue, TinkerRegistry.tabGeneral);
    registerBlockItem(registry, slimy_mud_magma, TinkerRegistry.tabGeneral);

    // Slimes
    registerBlockItem(registry, blue_slime, TinkerRegistry.tabWorld);
    registerBlockItem(registry, purple_slime, TinkerRegistry.tabWorld);
    registerBlockItem(registry, blood_slime, TinkerRegistry.tabWorld);
    registerBlockItem(registry, magma_slime, TinkerRegistry.tabWorld);
    registerBlockItem(registry, pink_slime, TinkerRegistry.tabWorld);

    registerBlockItem(registry, congealed_green_slime, TinkerRegistry.tabWorld);
    registerBlockItem(registry, congealed_blue_slime, TinkerRegistry.tabWorld);
    registerBlockItem(registry, congealed_purple_slime, TinkerRegistry.tabWorld);
    registerBlockItem(registry, congealed_blood_slime, TinkerRegistry.tabWorld);
    registerBlockItem(registry, congealed_magma_slime, TinkerRegistry.tabWorld);
    registerBlockItem(registry, congealed_pink_slime, TinkerRegistry.tabWorld);

    // Ores
    registerBlockItem(registry, cobalt_ore, TinkerRegistry.tabWorld);
    registerBlockItem(registry, ardite_ore, TinkerRegistry.tabWorld);

    // Firewood
    registerBlockItem(registry, lavawood, TinkerRegistry.tabGeneral);
    registerBlockItem(registry, firewood, TinkerRegistry.tabGeneral);

    // Decorative Stuff
    registerBlockItem(registry, mud_bricks, TinkerRegistry.tabGeneral);

    registerBlockItem(registry, clear_glass, TinkerRegistry.tabGeneral);

    registerBlockItem(registry, white_clear_stained_glass, TinkerRegistry.tabGeneral);
    registerBlockItem(registry, orange_clear_stained_glass, TinkerRegistry.tabGeneral);
    registerBlockItem(registry, magenta_clear_stained_glass, TinkerRegistry.tabGeneral);
    registerBlockItem(registry, light_blue_clear_stained_glass, TinkerRegistry.tabGeneral);
    registerBlockItem(registry, yellow_clear_stained_glass, TinkerRegistry.tabGeneral);
    registerBlockItem(registry, lime_clear_stained_glass, TinkerRegistry.tabGeneral);
    registerBlockItem(registry, pink_clear_stained_glass, TinkerRegistry.tabGeneral);
    registerBlockItem(registry, gray_clear_stained_glass, TinkerRegistry.tabGeneral);
    registerBlockItem(registry, light_gray_clear_stained_glass, TinkerRegistry.tabGeneral);
    registerBlockItem(registry, cyan_clear_stained_glass, TinkerRegistry.tabGeneral);
    registerBlockItem(registry, purple_clear_stained_glass, TinkerRegistry.tabGeneral);
    registerBlockItem(registry, blue_clear_stained_glass, TinkerRegistry.tabGeneral);
    registerBlockItem(registry, brown_clear_stained_glass, TinkerRegistry.tabGeneral);
    registerBlockItem(registry, green_clear_stained_glass, TinkerRegistry.tabGeneral);
    registerBlockItem(registry, red_clear_stained_glass, TinkerRegistry.tabGeneral);
    registerBlockItem(registry, black_clear_stained_glass, TinkerRegistry.tabGeneral);

    // Slabs
    registerBlockItem(registry, mud_bricks_slab, TinkerRegistry.tabGeneral);
    registerBlockItem(registry, lavawood_slab, TinkerRegistry.tabGeneral);
    registerBlockItem(registry, firewood_slab, TinkerRegistry.tabGeneral);

    // Stairs
    registerBlockItem(registry, mud_bricks_stairs, TinkerRegistry.tabGeneral);
    registerBlockItem(registry, lavawood_stairs, TinkerRegistry.tabGeneral);
    registerBlockItem(registry, firewood_stairs, TinkerRegistry.tabGeneral);

    // Metals
    if (isToolsLoaded() || isSmelteryLoaded() || forced) {
      registerBlockItem(registry, cobalt_block, TinkerRegistry.tabGeneral);
      registerBlockItem(registry, ardite_block, TinkerRegistry.tabGeneral);
      registerBlockItem(registry, manyullyn_block, TinkerRegistry.tabGeneral);
      registerBlockItem(registry, knightslime_block, TinkerRegistry.tabGeneral);
      registerBlockItem(registry, pigiron_block, TinkerRegistry.tabGeneral);
      registerBlockItem(registry, alubrass_block, TinkerRegistry.tabGeneral);
      registerBlockItem(registry, silky_jewel_block, TinkerRegistry.tabGeneral);
    }

    if (isToolsLoaded() || isGadgetsLoaded() || forced) {
      registerBlockItem(registry, glow);
    }

    register(registry, new EdibleItem(TinkerFood.BLUE_SLIME_BALL, TinkerRegistry.tabGeneral), "blue_slime_ball");
    register(registry, new EdibleItem(TinkerFood.PURPLE_SLIME_BALL, TinkerRegistry.tabGeneral), "purple_slime_ball");
    register(registry, new EdibleItem(TinkerFood.BLOOD_SLIME_BALL, TinkerRegistry.tabGeneral), "blood_slime_ball");
    register(registry, new EdibleItem(TinkerFood.MAGMA_SLIME_BALL, TinkerRegistry.tabGeneral), "magma_slime_ball");
    register(registry, new EdibleItem(TinkerFood.PINK_SLIME_BALL, TinkerRegistry.tabGeneral), "pink_slime_ball");

    if (isSmelteryLoaded() || forced) {
      register(registry, new GeneratedItem(TinkerRegistry.tabGeneral), "seared_brick");
      register(registry, new GeneratedItem(TinkerRegistry.tabGeneral), "mud_brick");
    }

    // Ingots and nuggets
    if (isToolsLoaded() || isSmelteryLoaded() || forced) {
      register(registry, new GeneratedItem(TinkerRegistry.tabGeneral), "cobalt_nugget");
      register(registry, new GeneratedItem(TinkerRegistry.tabGeneral), "cobalt_ingot");
      register(registry, new GeneratedItem(TinkerRegistry.tabGeneral), "ardite_nugget");
      register(registry, new GeneratedItem(TinkerRegistry.tabGeneral), "ardite_ingot");
      register(registry, new GeneratedItem(TinkerRegistry.tabGeneral), "manyullyn_nugget");
      register(registry, new GeneratedItem(TinkerRegistry.tabGeneral), "manyullyn_ingot");
      register(registry, new GeneratedItem(TinkerRegistry.tabGeneral), "pigiron_nugget");
      register(registry, new GeneratedItem(TinkerRegistry.tabGeneral), "pigiron_ingot");
      register(registry, new AlubrassItem(TinkerRegistry.tabGeneral), "alubrass_nugget");
      register(registry, new AlubrassItem(TinkerRegistry.tabGeneral), "alubrass_ingot");
    }

    if (isToolsLoaded() || forced) {
      register(registry, new EdibleItem(TinkerFood.BACON, TinkerRegistry.tabGeneral), "bacon");

      register(registry, new GeneratedItem(TinkerRegistry.tabGeneral), "green_slime_crystal");
      register(registry, new GeneratedItem(TinkerRegistry.tabGeneral), "blue_slime_crystal");
      register(registry, new GeneratedItem(TinkerRegistry.tabGeneral), "magma_slime_crystal");
      register(registry, new GeneratedItem(TinkerRegistry.tabGeneral), "width_expander");
      register(registry, new GeneratedItem(TinkerRegistry.tabGeneral), "height_expander");
      register(registry, new GeneratedItem(TinkerRegistry.tabGeneral), "reinforcement");
      register(registry, new GeneratedItem(TinkerRegistry.tabGeneral), "silky_cloth");
      register(registry, new GeneratedItem(TinkerRegistry.tabGeneral), "silky_jewel");
      register(registry, new GeneratedItem(TinkerRegistry.tabGeneral), "necrotic_bone");
      register(registry, new GeneratedItem(TinkerRegistry.tabGeneral), "moss");
      register(registry, new GeneratedItem(TinkerRegistry.tabGeneral), "mending_moss");
      register(registry, new GeneratedItem(TinkerRegistry.tabGeneral), "creative_modifier");

      register(registry, new GeneratedItem(TinkerRegistry.tabGeneral), "knightslime_nugget");
      register(registry, new GeneratedItem(TinkerRegistry.tabGeneral), "knightslime_ingot");
    }

    if (isGadgetsLoaded() || forced) {
      register(registry, new GeneratedItem(TinkerRegistry.tabGeneral), "dried_brick");

      register(registry, new EdibleItem(TinkerFood.MONSTER_JERKY, TinkerRegistry.tabGeneral), "monster_jerky");
      register(registry, new EdibleItem(TinkerFood.BEEF_JERKY, TinkerRegistry.tabGeneral), "beef_jerky");
      register(registry, new EdibleItem(TinkerFood.CHICKEN_JERKY, TinkerRegistry.tabGeneral), "chicken_jerky");
      register(registry, new EdibleItem(TinkerFood.PORK_JERKY, TinkerRegistry.tabGeneral), "pork_jerky");
      register(registry, new EdibleItem(TinkerFood.MUTTON_JERKY, TinkerRegistry.tabGeneral), "mutton_jerky");
      register(registry, new EdibleItem(TinkerFood.RABBIT_JERKY, TinkerRegistry.tabGeneral), "rabbit_jerky");
      register(registry, new EdibleItem(TinkerFood.FISH_JERKY, TinkerRegistry.tabGeneral), "fish_jerky");
      register(registry, new EdibleItem(TinkerFood.SALMON_JERKY, TinkerRegistry.tabGeneral), "salmon_jerky");
      register(registry, new EdibleItem(TinkerFood.CLOWNFISH_JERKY, TinkerRegistry.tabGeneral), "clownfish_jerky");
      register(registry, new EdibleItem(TinkerFood.PUFFERFISH_JERKY, TinkerRegistry.tabGeneral), "pufferfish_jerky");

      register(registry, new EdibleItem(TinkerFood.GREEN_SLIME_DROP, TinkerRegistry.tabGeneral), "green_slime_drop");
      register(registry, new EdibleItem(TinkerFood.BLUE_SLIME_DROP, TinkerRegistry.tabGeneral), "blue_slime_drop");
      register(registry, new EdibleItem(TinkerFood.PURPLE_SLIME_DROP, TinkerRegistry.tabGeneral), "purple_slime_drop");
      register(registry, new EdibleItem(TinkerFood.BLOOD_SLIME_DROP, TinkerRegistry.tabGeneral), "blood_slime_drop");
      register(registry, new EdibleItem(TinkerFood.MAGMA_SLIME_DROP, TinkerRegistry.tabGeneral), "magma_slime_drop");
    }
  }

  @SubscribeEvent
  public void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event) {
    CraftingHelper.register(ConfigOptionEnabledCondition.Serializer.INSTANCE);
  }

  @SubscribeEvent
  public void preInit(final FMLCommonSetupEvent event) {
    proxy.preInit();
  }

  @SubscribeEvent
  public void init(final InterModEnqueueEvent event) {
    proxy.init();

    MinecraftForge.EVENT_BUS.register(new AchievementEvents());
    MinecraftForge.EVENT_BUS.register(new BlockEvents());
    MinecraftForge.EVENT_BUS.register(new PlayerDataEvents());
  }

  @SubscribeEvent
  public void postInit(final InterModProcessEvent event) {
    proxy.postInit();
    TinkerRegistry.tabGeneral.setDisplayIcon(new ItemStack(blue_slime_ball));
  }
}
