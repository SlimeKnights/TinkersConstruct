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
import net.minecraftforge.registries.ObjectHolder;

import org.apache.logging.log4j.Logger;

import slimeknights.mantle.block.StairsBaseBlock;
import slimeknights.mantle.client.CreativeTab;
import slimeknights.mantle.item.EdibleItem;
import slimeknights.mantle.item.GeneratedItem;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.ServerProxy;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.common.conditions.ConfigOptionEnabledCondition;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.common.item.TinkerBookItem;
import slimeknights.tconstruct.common.registry.BaseRegistryAdapter;
import slimeknights.tconstruct.common.registry.ItemRegistryAdapter;
import slimeknights.tconstruct.library.TinkerPulseIds;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.shared.block.ClearGlassBlock;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock;
import slimeknights.tconstruct.shared.block.CongealedSlimeBlock;
import slimeknights.tconstruct.shared.block.ConsecratedSoilBlock;
import slimeknights.tconstruct.shared.block.DecoGroundBlock;
import slimeknights.tconstruct.shared.block.DecoGroundSlabBlock;
import slimeknights.tconstruct.shared.block.FirewoodBlock;
import slimeknights.tconstruct.shared.block.FirewoodSlabBlock;
import slimeknights.tconstruct.shared.block.GlowBlock;
import slimeknights.tconstruct.shared.block.GraveyardSoilBlock;
import slimeknights.tconstruct.shared.block.GroutBlock;
import slimeknights.tconstruct.shared.block.MetalBlock;
import slimeknights.tconstruct.shared.block.OreBlock;
import slimeknights.tconstruct.shared.block.SlimeBlock;
import slimeknights.tconstruct.shared.block.SlimyMudBlock;
import slimeknights.tconstruct.shared.item.AlubrassItem;

/**
 * Contains items and blocks and stuff that is shared by multiple pulses, but might be required individually
 */
@Pulse(id = TinkerPulseIds.TINKER_COMMONS_PULSE_ID, forced = true)
@ObjectHolder(TConstruct.modID)
public class TinkerCommons extends TinkerPulse {

  static final Logger log = Util.getLogger(TinkerPulseIds.TINKER_COMMONS_PULSE_ID);

  public static ServerProxy proxy = DistExecutor.runForDist(() -> CommonsClientProxy::new, () -> ServerProxy::new);

  public static final GroutBlock grout = injected();

  public static final GraveyardSoilBlock graveyard_soil = injected();
  public static final ConsecratedSoilBlock consecrated_soil = injected();

  public static final SlimyMudBlock slimy_mud_green = injected();
  public static final SlimyMudBlock slimy_mud_blue = injected();
  public static final SlimyMudBlock slimy_mud_magma = injected();

  public static final SlimeBlock blue_slime = injected();
  public static final SlimeBlock purple_slime = injected();
  public static final SlimeBlock blood_slime = injected();
  public static final SlimeBlock magma_slime = injected();
  public static final SlimeBlock pink_slime = injected();

  public static final CongealedSlimeBlock congealed_green_slime = injected();
  public static final CongealedSlimeBlock congealed_blue_slime = injected();
  public static final CongealedSlimeBlock congealed_purple_slime = injected();
  public static final CongealedSlimeBlock congealed_blood_slime = injected();
  public static final CongealedSlimeBlock congealed_magma_slime = injected();
  public static final CongealedSlimeBlock congealed_pink_slime = injected();

  public static final OreBlock cobalt_ore = injected();
  public static final OreBlock ardite_ore = injected();

  public static FirewoodBlock lavawood;
  public static FirewoodBlock firewood;

  public static DecoGroundBlock mud_bricks;

  public static final ClearGlassBlock clear_glass = injected();

  public static final ClearStainedGlassBlock white_clear_stained_glass = injected();
  public static final ClearStainedGlassBlock orange_clear_stained_glass = injected();
  public static final ClearStainedGlassBlock magenta_clear_stained_glass = injected();
  public static final ClearStainedGlassBlock light_blue_clear_stained_glass = injected();
  public static final ClearStainedGlassBlock yellow_clear_stained_glass = injected();
  public static final ClearStainedGlassBlock lime_clear_stained_glass = injected();
  public static final ClearStainedGlassBlock pink_clear_stained_glass = injected();
  public static final ClearStainedGlassBlock gray_clear_stained_glass = injected();
  public static final ClearStainedGlassBlock light_gray_clear_stained_glass = injected();
  public static final ClearStainedGlassBlock cyan_clear_stained_glass = injected();
  public static final ClearStainedGlassBlock purple_clear_stained_glass = injected();
  public static final ClearStainedGlassBlock blue_clear_stained_glass = injected();
  public static final ClearStainedGlassBlock brown_clear_stained_glass = injected();
  public static final ClearStainedGlassBlock green_clear_stained_glass = injected();
  public static final ClearStainedGlassBlock red_clear_stained_glass = injected();
  public static final ClearStainedGlassBlock black_clear_stained_glass = injected();

  public static final DecoGroundSlabBlock mud_bricks_slab = injected();
  public static final FirewoodSlabBlock lavawood_slab = injected();
  public static final FirewoodSlabBlock firewood_slab = injected();

  public static final StairsBaseBlock mud_bricks_stairs = injected();
  public static final StairsBaseBlock firewood_stairs = injected();
  public static final StairsBaseBlock lavawood_stairs = injected();

  public static final MetalBlock cobalt_block = injected();
  public static final MetalBlock ardite_block = injected();
  public static final MetalBlock manyullyn_block = injected();
  public static final MetalBlock knightslime_block = injected();
  public static final MetalBlock pigiron_block = injected();
  public static final MetalBlock alubrass_block = injected();
  public static final MetalBlock silky_jewel_block = injected();

  public static final GlowBlock glow = injected();

  public static final TinkerBookItem book = injected();

  public static final GeneratedItem seared_brick = injected();
  public static final GeneratedItem mud_brick = injected();

  public static final EdibleItem blue_slime_ball = injected();
  public static final EdibleItem purple_slime_ball = injected();
  public static final EdibleItem blood_slime_ball = injected();
  public static final EdibleItem magma_slime_ball = injected();
  public static final EdibleItem pink_slime_ball = injected();

  public static final GeneratedItem cobalt_nugget = injected();
  public static final GeneratedItem cobalt_ingot = injected();
  public static final GeneratedItem ardite_nugget = injected();
  public static final GeneratedItem ardite_ingot = injected();
  public static final GeneratedItem manyullyn_nugget = injected();
  public static final GeneratedItem manyullyn_ingot = injected();
  public static final GeneratedItem pigiron_nugget = injected();
  public static final GeneratedItem pigiron_ingot = injected();
  public static final GeneratedItem alubrass_nugget = injected();
  public static final AlubrassItem alubrass_ingot = injected();

  public static final EdibleItem bacon = injected();

  public static final GeneratedItem green_slime_crystal = injected();
  public static final GeneratedItem blue_slime_crystal = injected();
  public static final GeneratedItem magma_slime_crystal = injected();
  public static final GeneratedItem width_expander = injected();
  public static final GeneratedItem height_expander = injected();
  public static final GeneratedItem reinforcement = injected();
  public static final GeneratedItem silky_cloth = injected();
  public static final GeneratedItem silky_jewel = injected();
  public static final GeneratedItem necrotic_bone = injected();
  public static final GeneratedItem moss = injected();
  public static final GeneratedItem mending_moss = injected();
  public static final GeneratedItem creative_modifier = injected();

  public static final GeneratedItem knightslime_nugget = injected();
  public static final GeneratedItem knightslime_ingot = injected();

  public static final GeneratedItem dried_brick = injected();

  public static final EdibleItem monster_jerky = injected();
  public static final EdibleItem beef_jerky = injected();
  public static final EdibleItem chicken_jerky = injected();
  public static final EdibleItem pork_jerky = injected();
  public static final EdibleItem mutton_jerky = injected();
  public static final EdibleItem rabbit_jerky = injected();
  public static final EdibleItem fish_jerky = injected();
  public static final EdibleItem salmon_jerky = injected();
  public static final EdibleItem clownfish_jerky = injected();
  public static final EdibleItem pufferfish_jerky = injected();
  public static final EdibleItem green_slime_drop = injected();
  public static final EdibleItem blue_slime_drop = injected();
  public static final EdibleItem purple_slime_drop = injected();
  public static final EdibleItem blood_slime_drop = injected();
  public static final EdibleItem magma_slime_drop = injected();

  @SubscribeEvent
  public void registerBlocks(final RegistryEvent.Register<Block> event) {
    BaseRegistryAdapter<Block> registry = new BaseRegistryAdapter<>(event.getRegistry());
    boolean forced = Config.forceRegisterAll; // causes to always register all items

    // Soils
    registry.register(new GroutBlock(), "grout");

    registry.register(new GraveyardSoilBlock(), "graveyard_soil");
    registry.register(new ConsecratedSoilBlock(), "consecrated_soil");

    registry.register(new SlimyMudBlock(SlimyMudBlock.MudType.SLIMY_MUD_GREEN), "slimy_mud_green");
    registry.register(new SlimyMudBlock(SlimyMudBlock.MudType.SLIMY_MUD_BLUE), "slimy_mud_blue");
    registry.register(new SlimyMudBlock(SlimyMudBlock.MudType.SLIMY_MUD_MAGMA), "slimy_mud_magma");

    // Slimes
    registry.register(new SlimeBlock(true), "blue_slime");
    registry.register(new SlimeBlock(true), "purple_slime");
    registry.register(new SlimeBlock(true), "blood_slime");
    registry.register(new SlimeBlock(true), "magma_slime");
    registry.register(new SlimeBlock(false), "pink_slime");

    registry.register(new CongealedSlimeBlock(true), "congealed_green_slime");
    registry.register(new CongealedSlimeBlock(true), "congealed_blue_slime");
    registry.register(new CongealedSlimeBlock(true), "congealed_purple_slime");
    registry.register(new CongealedSlimeBlock(true), "congealed_blood_slime");
    registry.register(new CongealedSlimeBlock(true), "congealed_magma_slime");
    registry.register(new CongealedSlimeBlock(false), "congealed_pink_slime");

    // Ores
    registry.register(new OreBlock(), "cobalt_ore");
    registry.register(new OreBlock(), "ardite_ore");

    // Firewood
    lavawood = registry.register(new FirewoodBlock(), "lavawood");
    firewood = registry.register(new FirewoodBlock(), "firewood");

    // Decorative Stuff
    mud_bricks = registry.register(new DecoGroundBlock(), "mud_bricks");

    registry.register(new ClearGlassBlock(), "clear_glass");

    registry.register(new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.WHITE), "white_clear_stained_glass");
    registry.register(new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.ORANGE), "orange_clear_stained_glass");
    registry.register(new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.MAGENTA), "magenta_clear_stained_glass");
    registry.register(new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.LIGHT_BLUE), "light_blue_clear_stained_glass");
    registry.register(new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.YELLOW), "yellow_clear_stained_glass");
    registry.register(new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.LIME), "lime_clear_stained_glass");
    registry.register(new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.PINK), "pink_clear_stained_glass");
    registry.register(new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.GRAY), "gray_clear_stained_glass");
    registry.register(new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.LIGHT_GRAY), "light_gray_clear_stained_glass");
    registry.register(new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.CYAN), "cyan_clear_stained_glass");
    registry.register(new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.PURPLE), "purple_clear_stained_glass");
    registry.register(new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.BLUE), "blue_clear_stained_glass");
    registry.register(new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.BROWN), "brown_clear_stained_glass");
    registry.register(new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.GREEN), "green_clear_stained_glass");
    registry.register(new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.RED), "red_clear_stained_glass");
    registry.register(new ClearStainedGlassBlock(ClearStainedGlassBlock.GlassColor.BLACK), "black_clear_stained_glass");

    // Slabs
    registry.register(new DecoGroundSlabBlock(), "mud_bricks_slab");
    registry.register(new FirewoodSlabBlock(), "lavawood_slab");
    registry.register(new FirewoodSlabBlock(), "firewood_slab");

    // Stairs
    registry.register(new StairsBaseBlock(mud_bricks), "mud_bricks_stairs");
    registry.register(new StairsBaseBlock(lavawood), "lavawood_stairs");
    registry.register(new StairsBaseBlock(firewood), "firewood_stairs");

    // Metals
    if (isToolsLoaded() || isSmelteryLoaded() || forced) {
      registry.register(new MetalBlock(MetalBlock.MetalType.COBALT), "cobalt_block");
      registry.register(new MetalBlock(MetalBlock.MetalType.ARDITE), "ardite_block");
      registry.register(new MetalBlock(MetalBlock.MetalType.MANYULLYN), "manyullyn_block");
      registry.register(new MetalBlock(MetalBlock.MetalType.KNIGHTSLIME), "knightslime_block");
      registry.register(new MetalBlock(MetalBlock.MetalType.PIGIRON), "pigiron_block");
      registry.register(new MetalBlock(MetalBlock.MetalType.ALUBRASS), "alubrass_block");
      registry.register(new MetalBlock(MetalBlock.MetalType.SILKY_JEWEL), "silky_jewel_block");
    }

    if (isToolsLoaded() || isGadgetsLoaded() || forced) {
      registry.register(new GlowBlock(), "glow");
    }
  }

  @SubscribeEvent
  public void registerItems(final RegistryEvent.Register<Item> event) {
    ItemRegistryAdapter registry = new ItemRegistryAdapter(event.getRegistry());
    CreativeTab tabGeneral = TinkerRegistry.tabGeneral;
    CreativeTab tabWorld = TinkerRegistry.tabWorld;
    boolean forced = Config.forceRegisterAll; // causes to always register all items

    registry.register(new TinkerBookItem(), "book");

    // Soils
    registry.registerBlockItem(grout, tabGeneral);

    registry.registerBlockItem(graveyard_soil, tabGeneral);
    registry.registerBlockItem(consecrated_soil, tabGeneral);

    registry.registerBlockItem(slimy_mud_green, tabGeneral);
    registry.registerBlockItem(slimy_mud_blue, tabGeneral);
    registry.registerBlockItem(slimy_mud_magma, tabGeneral);

    // Slimes
    registry.registerBlockItem(blue_slime, tabWorld);
    registry.registerBlockItem(purple_slime, tabWorld);
    registry.registerBlockItem(blood_slime, tabWorld);
    registry.registerBlockItem(magma_slime, tabWorld);
    registry.registerBlockItem(pink_slime, tabWorld);

    registry.registerBlockItem(congealed_green_slime, tabWorld);
    registry.registerBlockItem(congealed_blue_slime, tabWorld);
    registry.registerBlockItem(congealed_purple_slime, tabWorld);
    registry.registerBlockItem(congealed_blood_slime, tabWorld);
    registry.registerBlockItem(congealed_magma_slime, tabWorld);
    registry.registerBlockItem(congealed_pink_slime, tabWorld);

    // Ores
    registry.registerBlockItem(cobalt_ore, tabWorld);
    registry.registerBlockItem(ardite_ore, tabWorld);

    // Firewood
    registry.registerBlockItem(lavawood, tabGeneral);
    registry.registerBlockItem(firewood, tabGeneral);

    // Decorative Stuff
    registry.registerBlockItem(mud_bricks, tabGeneral);

    registry.registerBlockItem(clear_glass, tabGeneral);

    registry.registerBlockItem(white_clear_stained_glass, tabGeneral);
    registry.registerBlockItem(orange_clear_stained_glass, tabGeneral);
    registry.registerBlockItem(magenta_clear_stained_glass, tabGeneral);
    registry.registerBlockItem(light_blue_clear_stained_glass, tabGeneral);
    registry.registerBlockItem(yellow_clear_stained_glass, tabGeneral);
    registry.registerBlockItem(lime_clear_stained_glass, tabGeneral);
    registry.registerBlockItem(pink_clear_stained_glass, tabGeneral);
    registry.registerBlockItem(gray_clear_stained_glass, tabGeneral);
    registry.registerBlockItem(light_gray_clear_stained_glass, tabGeneral);
    registry.registerBlockItem(cyan_clear_stained_glass, tabGeneral);
    registry.registerBlockItem(purple_clear_stained_glass, tabGeneral);
    registry.registerBlockItem(blue_clear_stained_glass, tabGeneral);
    registry.registerBlockItem(brown_clear_stained_glass, tabGeneral);
    registry.registerBlockItem(green_clear_stained_glass, tabGeneral);
    registry.registerBlockItem(red_clear_stained_glass, tabGeneral);
    registry.registerBlockItem(black_clear_stained_glass, tabGeneral);

    // Slabs
    registry.registerBlockItem(mud_bricks_slab, tabGeneral);
    registry.registerBlockItem(lavawood_slab, tabGeneral);
    registry.registerBlockItem(firewood_slab, tabGeneral);

    // Stairs
    registry.registerBlockItem(mud_bricks_stairs, tabGeneral);
    registry.registerBlockItem(lavawood_stairs, tabGeneral);
    registry.registerBlockItem(firewood_stairs, tabGeneral);

    // Metals
    if (isToolsLoaded() || isSmelteryLoaded() || forced) {
      registry.registerBlockItem(cobalt_block, tabGeneral);
      registry.registerBlockItem(ardite_block, tabGeneral);
      registry.registerBlockItem(manyullyn_block, tabGeneral);
      registry.registerBlockItem(knightslime_block, tabGeneral);
      registry.registerBlockItem(pigiron_block, tabGeneral);
      registry.registerBlockItem(alubrass_block, tabGeneral);
      registry.registerBlockItem(silky_jewel_block, tabGeneral);
    }

    if (isToolsLoaded() || isGadgetsLoaded() || forced) {
      registry.registerBlockItem(glow, TinkerRegistry.tabGadgets);
    }

    registry.register(new EdibleItem(TinkerFood.BLUE_SLIME_BALL, tabGeneral), "blue_slime_ball");
    registry.register(new EdibleItem(TinkerFood.PURPLE_SLIME_BALL, tabGeneral), "purple_slime_ball");
    registry.register(new EdibleItem(TinkerFood.BLOOD_SLIME_BALL, tabGeneral), "blood_slime_ball");
    registry.register(new EdibleItem(TinkerFood.MAGMA_SLIME_BALL, tabGeneral), "magma_slime_ball");
    registry.register(new EdibleItem(TinkerFood.PINK_SLIME_BALL, tabGeneral), "pink_slime_ball");

    if (isSmelteryLoaded() || forced) {
      registry.register(new GeneratedItem(tabGeneral), "seared_brick");
      registry.register(new GeneratedItem(tabGeneral), "mud_brick");
    }

    // Ingots and nuggets
    if (isToolsLoaded() || isSmelteryLoaded() || forced) {
      registry.register(new GeneratedItem(tabGeneral), "cobalt_nugget");
      registry.register(new GeneratedItem(tabGeneral), "cobalt_ingot");
      registry.register(new GeneratedItem(tabGeneral), "ardite_nugget");
      registry.register(new GeneratedItem(tabGeneral), "ardite_ingot");
      registry.register(new GeneratedItem(tabGeneral), "manyullyn_nugget");
      registry.register(new GeneratedItem(tabGeneral), "manyullyn_ingot");
      registry.register(new GeneratedItem(tabGeneral), "pigiron_nugget");
      registry.register(new GeneratedItem(tabGeneral), "pigiron_ingot");
      registry.register(new AlubrassItem(tabGeneral), "alubrass_nugget");
      registry.register(new AlubrassItem(tabGeneral), "alubrass_ingot");
    }

    if (isToolsLoaded() || forced) {
      registry.register(new EdibleItem(TinkerFood.BACON, tabGeneral), "bacon");

      registry.register(new GeneratedItem(tabGeneral), "green_slime_crystal");
      registry.register(new GeneratedItem(tabGeneral), "blue_slime_crystal");
      registry.register(new GeneratedItem(tabGeneral), "magma_slime_crystal");
      registry.register(new GeneratedItem(tabGeneral), "width_expander");
      registry.register(new GeneratedItem(tabGeneral), "height_expander");
      registry.register(new GeneratedItem(tabGeneral), "reinforcement");
      registry.register(new GeneratedItem(tabGeneral), "silky_cloth");
      registry.register(new GeneratedItem(tabGeneral), "silky_jewel");
      registry.register(new GeneratedItem(tabGeneral), "necrotic_bone");
      registry.register(new GeneratedItem(tabGeneral), "moss");
      registry.register(new GeneratedItem(tabGeneral), "mending_moss");
      registry.register(new GeneratedItem(tabGeneral), "creative_modifier");

      registry.register(new GeneratedItem(tabGeneral), "knightslime_nugget");
      registry.register(new GeneratedItem(tabGeneral), "knightslime_ingot");
    }

    if (isGadgetsLoaded() || forced) {
      registry.register(new GeneratedItem(tabGeneral), "dried_brick");

      registry.register(new EdibleItem(TinkerFood.MONSTER_JERKY, tabGeneral), "monster_jerky");
      registry.register(new EdibleItem(TinkerFood.BEEF_JERKY, tabGeneral), "beef_jerky");
      registry.register(new EdibleItem(TinkerFood.CHICKEN_JERKY, tabGeneral), "chicken_jerky");
      registry.register(new EdibleItem(TinkerFood.PORK_JERKY, tabGeneral), "pork_jerky");
      registry.register(new EdibleItem(TinkerFood.MUTTON_JERKY, tabGeneral), "mutton_jerky");
      registry.register(new EdibleItem(TinkerFood.RABBIT_JERKY, tabGeneral), "rabbit_jerky");
      registry.register(new EdibleItem(TinkerFood.FISH_JERKY, tabGeneral), "fish_jerky");
      registry.register(new EdibleItem(TinkerFood.SALMON_JERKY, tabGeneral), "salmon_jerky");
      registry.register(new EdibleItem(TinkerFood.CLOWNFISH_JERKY, tabGeneral), "clownfish_jerky");
      registry.register(new EdibleItem(TinkerFood.PUFFERFISH_JERKY, tabGeneral), "pufferfish_jerky");

      registry.register(new EdibleItem(TinkerFood.GREEN_SLIME_DROP, tabGeneral), "green_slime_drop");
      registry.register(new EdibleItem(TinkerFood.BLUE_SLIME_DROP, tabGeneral), "blue_slime_drop");
      registry.register(new EdibleItem(TinkerFood.PURPLE_SLIME_DROP, tabGeneral), "purple_slime_drop");
      registry.register(new EdibleItem(TinkerFood.BLOOD_SLIME_DROP, tabGeneral), "blood_slime_drop");
      registry.register(new EdibleItem(TinkerFood.MAGMA_SLIME_DROP, tabGeneral), "magma_slime_drop");
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
