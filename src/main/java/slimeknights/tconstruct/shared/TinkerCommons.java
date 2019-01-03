package slimeknights.tconstruct.shared;

import com.google.common.eventbus.Subscribe;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import org.apache.logging.log4j.Logger;

import slimeknights.mantle.item.ItemBlockMeta;
import slimeknights.mantle.item.ItemEdible;
import slimeknights.mantle.item.ItemMetaDynamic;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.common.CommonProxy;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.common.item.ItemTinkerBook;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.shared.block.BlockClearGlass;
import slimeknights.tconstruct.shared.block.BlockClearStainedGlass;
import slimeknights.tconstruct.shared.block.BlockDecoGround;
import slimeknights.tconstruct.shared.block.BlockDecoGroundSlab;
import slimeknights.tconstruct.shared.block.BlockFirewood;
import slimeknights.tconstruct.shared.block.BlockFirewoodSlab;
import slimeknights.tconstruct.shared.block.BlockGlow;
import slimeknights.tconstruct.shared.block.BlockMetal;
import slimeknights.tconstruct.shared.block.BlockOre;
import slimeknights.tconstruct.shared.block.BlockSlime;
import slimeknights.tconstruct.shared.block.BlockSlimeCongealed;
import slimeknights.tconstruct.shared.block.BlockSoil;
import slimeknights.tconstruct.shared.item.ItemMetaDynamicTinkers;
import slimeknights.tconstruct.shared.worldgen.NetherOreGenerator;

/**
 * Contains items and blocks and stuff that is shared by multiple pulses, but might be required individually
 */
@Pulse(id = TinkerCommons.PulseId, forced = true)
public class TinkerCommons extends TinkerPulse {

  public static final String PulseId = "TinkerCommons";
  static final Logger log = Util.getLogger(PulseId);

  @SidedProxy(clientSide = "slimeknights.tconstruct.shared.CommonsClientProxy", serverSide = "slimeknights.tconstruct.common.CommonProxy")
  public static CommonProxy proxy;

  public static BlockSoil blockSoil;
  public static BlockOre blockOre;
  public static BlockMetal blockMetal;
  public static BlockFirewood blockFirewood;
  public static BlockGlow blockGlow;

  public static BlockDecoGround blockDecoGround;

  public static BlockSlime blockSlime;
  public static BlockSlimeCongealed blockSlimeCongealed;

  public static BlockDecoGroundSlab slabDecoGround;
  public static BlockFirewoodSlab slabFirewood;

  // stairs
  public static Block stairsMudBrick;
  public static Block stairsFirewood;
  public static Block stairsLavawood;

  // glass
  public static Block blockClearGlass;
  public static BlockClearStainedGlass blockClearStainedGlass;

  // block itemstacks
  public static ItemStack grout;
  public static ItemStack slimyMudGreen;
  public static ItemStack slimyMudBlue;
  public static ItemStack slimyMudMagma;
  public static ItemStack graveyardSoil;
  public static ItemStack consecratedSoil;

  public static ItemStack mudBrickBlock;

  public static ItemStack oreCobalt;
  public static ItemStack oreArdite;

  public static ItemStack blockCobalt;
  public static ItemStack blockArdite;
  public static ItemStack blockManyullyn;
  public static ItemStack blockPigIron;
  public static ItemStack blockKnightSlime;
  public static ItemStack blockSilkyJewel;
  public static ItemStack blockAlubrass;

  public static ItemStack lavawood;
  public static ItemStack firewood;

  // Items
  public static ItemTinkerBook book;
  public static ItemMetaDynamic nuggets;
  public static ItemMetaDynamic ingots;
  public static ItemMetaDynamic materials;
  public static ItemEdible edibles;

  // Nugget Itemstacks
  public static ItemStack nuggetCobalt;
  public static ItemStack nuggetArdite;
  public static ItemStack nuggetManyullyn;
  public static ItemStack nuggetPigIron;
  public static ItemStack nuggetKnightSlime;
  public static ItemStack nuggetAlubrass;

  // Ingot Itemstacks
  public static ItemStack ingotCobalt;
  public static ItemStack ingotArdite;
  public static ItemStack ingotManyullyn;
  public static ItemStack ingotPigIron;
  public static ItemStack ingotKnightSlime;
  public static ItemStack ingotAlubrass;

  // Material Itemstacks
  public static ItemStack searedBrick;
  public static ItemStack mudBrick;
  public static ItemStack driedBrick;

  public static ItemStack matSlimeBallBlue;
  public static ItemStack matSlimeBallPurple;
  public static ItemStack matSlimeBallBlood;
  public static ItemStack matSlimeBallMagma;

  public static ItemStack matSlimeCrystalGreen;
  public static ItemStack matSlimeCrystalBlue;
  public static ItemStack matSlimeCrystalMagma;

  public static ItemStack matExpanderW;
  public static ItemStack matExpanderH;
  public static ItemStack matReinforcement;
  public static ItemStack matCreativeModifier;
  public static ItemStack matSilkyCloth;
  public static ItemStack matSilkyJewel;
  public static ItemStack matNecroticBone;
  public static ItemStack matMoss;
  public static ItemStack matMendingMoss;

  // jerky
  public static ItemStack jerkyBeef;
  public static ItemStack jerkyChicken;
  public static ItemStack jerkyPork;
  public static ItemStack jerkyMutton;
  public static ItemStack jerkyRabbit;

  public static ItemStack jerkyFish;
  public static ItemStack jerkySalmon;
  public static ItemStack jerkyClownfish;
  public static ItemStack jerkyPufferfish;

  public static ItemStack slimedropGreen;
  public static ItemStack slimedropBlue;
  public static ItemStack slimedropPurple;
  public static ItemStack slimedropBlood;
  public static ItemStack slimedropMagma;

  public static ItemStack jerkyMonster;

  // Misc.
  public static ItemStack bacon;

  @SubscribeEvent
  public void registerBlocks(Register<Block> event) {
    IForgeRegistry<Block> registry = event.getRegistry();
    boolean forced = Config.forceRegisterAll; // causes to always register all items

    // Soils
    blockSoil = registerBlock(registry, new BlockSoil(), "soil");

    // Slime Blocks
    blockSlime = registerBlock(registry, new BlockSlime(), "slime");
    blockSlimeCongealed = registerBlock(registry, new BlockSlimeCongealed(), "slime_congealed");

    // Ores
    blockOre = registerBlock(registry, new BlockOre(), "ore");

    // Firewood
    blockFirewood = registerBlock(registry, new BlockFirewood(), "firewood");
    blockFirewood.setLightLevel(0.5f);
    blockFirewood.setCreativeTab(TinkerRegistry.tabGeneral);

    // Decorative Stuff
    blockDecoGround = registerBlock(registry, new BlockDecoGround(), "deco_ground");

    blockClearGlass = registerBlock(registry, new BlockClearGlass(), "clear_glass");
    blockClearStainedGlass = registerBlock(registry, new BlockClearStainedGlass(), "clear_stained_glass");

    // slabs
    slabDecoGround = registerBlock(registry, new BlockDecoGroundSlab(), "deco_ground_slab");
    slabFirewood = registerBlock(registry, new BlockFirewoodSlab(), "firewood_slab");

    // stairs
    stairsMudBrick = registerBlockStairsFrom(registry, blockDecoGround, BlockDecoGround.DecoGroundType.MUDBRICK, "mudbrick_stairs");
    stairsFirewood = registerBlockStairsFrom(registry, blockFirewood, BlockFirewood.FirewoodType.FIREWOOD, "firewood_stairs");
    stairsLavawood = registerBlockStairsFrom(registry, blockFirewood, BlockFirewood.FirewoodType.LAVAWOOD, "lavawood_stairs");

    // Ingots and nuggets
    if(isToolsLoaded() || isSmelteryLoaded() || forced) {
      blockMetal = registerBlock(registry, new BlockMetal(), "metal");
    }

    if(isToolsLoaded() || isGadgetsLoaded()) {
      blockGlow = registerBlock(registry, new BlockGlow(), "glow");
    }
  }

  @SubscribeEvent
  public void registerItems(Register<Item> event) {
    IForgeRegistry<Item> registry = event.getRegistry();
    boolean forced = Config.forceRegisterAll; // causes to always register all items

    book = registerItem(registry, new ItemTinkerBook(), "book");

    // Soils
    blockSoil = registerEnumItemBlock(registry, blockSoil);

    grout = new ItemStack(blockSoil, 1, BlockSoil.SoilTypes.GROUT.getMeta());
    slimyMudGreen = new ItemStack(blockSoil, 1, BlockSoil.SoilTypes.SLIMY_MUD_GREEN.getMeta());
    slimyMudBlue = new ItemStack(blockSoil, 1, BlockSoil.SoilTypes.SLIMY_MUD_BLUE.getMeta());
    slimyMudMagma = new ItemStack(blockSoil, 1, BlockSoil.SoilTypes.SLIMY_MUD_MAGMA.getMeta());
    graveyardSoil = new ItemStack(blockSoil, 1, BlockSoil.SoilTypes.GRAVEYARD.getMeta());
    consecratedSoil = new ItemStack(blockSoil, 1, BlockSoil.SoilTypes.CONSECRATED.getMeta());

    // Slime Blocks
    blockSlime = registerItemBlockProp(registry, new ItemBlockMeta(blockSlime), BlockSlime.TYPE);
    blockSlimeCongealed = registerItemBlockProp(registry, new ItemBlockMeta(blockSlimeCongealed), BlockSlime.TYPE);

    // Ores
    blockOre = registerEnumItemBlock(registry, blockOre);

    oreCobalt = new ItemStack(blockOre, 1, BlockOre.OreTypes.COBALT.getMeta());
    oreArdite = new ItemStack(blockOre, 1, BlockOre.OreTypes.ARDITE.getMeta());

    // Firewood
    blockFirewood = registerEnumItemBlock(registry, blockFirewood);

    lavawood = new ItemStack(blockFirewood, 1, BlockFirewood.FirewoodType.LAVAWOOD.getMeta());
    firewood = new ItemStack(blockFirewood, 1, BlockFirewood.FirewoodType.FIREWOOD.getMeta());

    // Decorative Stuff
    blockDecoGround = registerEnumItemBlock(registry, blockDecoGround);

    mudBrickBlock = new ItemStack(blockDecoGround, 1, BlockDecoGround.DecoGroundType.MUDBRICK.getMeta());

    blockClearGlass = registerItemBlock(registry, blockClearGlass);
    blockClearStainedGlass = registerEnumItemBlock(registry, blockClearStainedGlass);

    // Slabs
    slabDecoGround = registerEnumItemBlockSlab(registry, slabDecoGround);
    slabFirewood = registerEnumItemBlockSlab(registry, slabFirewood);

    // Stairs
    stairsMudBrick = registerItemBlock(registry, stairsMudBrick);
    stairsFirewood = registerItemBlock(registry, stairsFirewood);
    stairsLavawood = registerItemBlock(registry, stairsLavawood);

    // create the items. We can probably always create them since they handle themselves dynamically
    nuggets = registerItem(registry, new ItemMetaDynamicTinkers(), "nuggets");
    ingots = registerItem(registry, new ItemMetaDynamicTinkers(), "ingots");
    materials = registerItem(registry, new ItemMetaDynamic(), "materials");
    edibles = registerItem(registry, new ItemEdible(), "edible");

    nuggets.setCreativeTab(TinkerRegistry.tabGeneral);
    ingots.setCreativeTab(TinkerRegistry.tabGeneral);
    materials.setCreativeTab(TinkerRegistry.tabGeneral);
    edibles.setCreativeTab(TinkerRegistry.tabGeneral);

    // Items that can always be present.. slimeballs
    matSlimeBallBlue = edibles.addFood(1, 1, 1f, "slimeball_blue", new PotionEffect(MobEffects.SLOWNESS, 20 * 45, 2), new PotionEffect(MobEffects.JUMP_BOOST, 20 * 60, 2));
    matSlimeBallPurple = edibles.addFood(2, 1, 2f, "slimeball_purple", new PotionEffect(MobEffects.UNLUCK, 20 * 45), new PotionEffect(MobEffects.LUCK, 20 * 60));
    matSlimeBallBlood = edibles.addFood(3, 1, 1.5f, "slimeball_blood", new PotionEffect(MobEffects.POISON, 20 * 45, 2), new PotionEffect(MobEffects.HEALTH_BOOST, 20 * 60));
    matSlimeBallMagma = edibles.addFood(4, 2, 1f, "slimeball_magma", new PotionEffect(MobEffects.WEAKNESS, 20 * 45), new PotionEffect(MobEffects.WITHER, 20 * 15), new PotionEffect(MobEffects.FIRE_RESISTANCE, 20 * 60));

    // All other items are either ingots or items for modifiers

    if(isSmelteryLoaded() || forced) {
      searedBrick = materials.addMeta(0, "seared_brick");
      mudBrick = materials.addMeta(1, "mud_brick");
    }

    // Ingots and nuggets
    if(isToolsLoaded() || isSmelteryLoaded() || forced) {
      nuggetCobalt = nuggets.addMeta(0, "cobalt");
      ingotCobalt = ingots.addMeta(0, "cobalt");

      nuggetArdite = nuggets.addMeta(1, "ardite");
      ingotArdite = ingots.addMeta(1, "ardite");

      nuggetManyullyn = nuggets.addMeta(2, "manyullyn");
      ingotManyullyn = ingots.addMeta(2, "manyullyn");

      nuggetPigIron = nuggets.addMeta(4, "pigiron");
      ingotPigIron = ingots.addMeta(4, "pigiron");

      nuggetAlubrass = nuggets.addMeta(5, "alubrass");
      ingotAlubrass = ingots.addMeta(5, "alubrass");

      blockMetal = registerEnumItemBlock(registry, blockMetal);

      blockCobalt = new ItemStack(blockMetal, 1, BlockMetal.MetalTypes.COBALT.getMeta());
      blockArdite = new ItemStack(blockMetal, 1, BlockMetal.MetalTypes.ARDITE.getMeta());
      blockManyullyn = new ItemStack(blockMetal, 1, BlockMetal.MetalTypes.MANYULLYN.getMeta());
      blockKnightSlime = new ItemStack(blockMetal, 1, BlockMetal.MetalTypes.KNIGHTSLIME.getMeta());
      blockPigIron = new ItemStack(blockMetal, 1, BlockMetal.MetalTypes.PIGIRON.getMeta());
      blockAlubrass = new ItemStack(blockMetal, 1, BlockMetal.MetalTypes.ALUBRASS.getMeta());
      blockSilkyJewel = new ItemStack(blockMetal, 1, BlockMetal.MetalTypes.SILKY_JEWEL.getMeta());
    }

    // Materials
    if(isToolsLoaded() || forced) {
      bacon = edibles.addFood(0, 4, 0.6f, "bacon");

      matSlimeCrystalGreen = materials.addMeta(9, "slimecrystal_green");
      matSlimeCrystalBlue = materials.addMeta(10, "slimecrystal_blue");
      matSlimeCrystalMagma = materials.addMeta(11, "slimecrystal_magma");
      matExpanderW = materials.addMeta(12, "expander_w");
      matExpanderH = materials.addMeta(13, "expander_h");
      matReinforcement = materials.addMeta(14, "reinforcement");

      matSilkyCloth = materials.addMeta(15, "silky_cloth");
      matSilkyJewel = materials.addMeta(16, "silky_jewel");

      matNecroticBone = materials.addMeta(17, "necrotic_bone");
      matMoss = materials.addMeta(18, "moss");
      matMendingMoss = materials.addMeta(19, "mending_moss");

      matCreativeModifier = materials.addMeta(50, "creative_modifier");

      ingotKnightSlime = ingots.addMeta(3, "knightslime");
      nuggetKnightSlime = nuggets.addMeta(3, "knightslime");
    }

    if(isGadgetsLoaded() || forced) {
      driedBrick = materials.addMeta(2, "dried_brick");

      // Jerky
      jerkyMonster = edibles.addFood(10, 4, 0.4f, "jerky_monster", false);
      jerkyBeef = edibles.addFood(11, 8, 1f, "jerky_beef", false);
      jerkyChicken = edibles.addFood(12, 6, 0.8f, "jerky_chicken", false);
      jerkyPork = edibles.addFood(13, 8, 1f, "jerky_pork", false);
      jerkyMutton = edibles.addFood(14, 6, 1f, "jerky_mutton", false);
      jerkyRabbit = edibles.addFood(15, 5, 0.8f, "jerky_rabbit", false);

      jerkyFish = edibles.addFood(20, 5, 0.8f, "jerky_fish", false);
      jerkySalmon = edibles.addFood(21, 6, 1f, "jerky_salmon", false);
      jerkyClownfish = edibles.addFood(22, 3, 0.8f, "jerky_clownfish", false);
      jerkyPufferfish = edibles.addFood(23, 3, 0.8f, "jerky_pufferfish", false);

      slimedropGreen = edibles.addFood(30, 1, 1f, "slimedrop_green", new PotionEffect(MobEffects.SPEED, 20 * 90, 2));
      slimedropBlue = edibles.addFood(31, 3, 1f, "slimedrop_blue", new PotionEffect(MobEffects.JUMP_BOOST, 20 * 90, 2));
      slimedropPurple = edibles.addFood(32, 3, 2f, "slimedrop_purple", new PotionEffect(MobEffects.LUCK, 20 * 90));
      slimedropBlood = edibles.addFood(33, 3, 1.5f, "slimedrop_blood", new PotionEffect(MobEffects.HEALTH_BOOST, 20 * 90));
      slimedropMagma = edibles.addFood(34, 6, 1f, "slimedrop_magma", new PotionEffect(MobEffects.FIRE_RESISTANCE, 20 * 90));
    }
  }

  @SubscribeEvent
  public void registerModels(ModelRegistryEvent event) {
    proxy.registerModels();
  }

  @Subscribe
  public void preInit(FMLPreInitializationEvent event) {
    proxy.preInit();
  }

  @Subscribe
  public void init(FMLInitializationEvent event) {
    registerSmeltingRecipes();
    proxy.init();

    GameRegistry.registerWorldGenerator(NetherOreGenerator.INSTANCE, 0);

    MinecraftForge.EVENT_BUS.register(new AchievementEvents());
    MinecraftForge.EVENT_BUS.register(new BlockEvents());
    MinecraftForge.EVENT_BUS.register(new PlayerDataEvents());
  }

  // POST-INITIALIZATION
  @Subscribe
  public void postInit(FMLPostInitializationEvent event) {
    TinkerRegistry.tabGeneral.setDisplayIcon(matSlimeBallBlue);
  }

  private void registerSmeltingRecipes() {
    GameRegistry.addSmelting(graveyardSoil, consecratedSoil, 0.1f);

    if(!isSmelteryLoaded()) {
      // compat recipe if the smeltery is not available for melting
      GameRegistry.addSmelting(Blocks.GLASS, new ItemStack(blockClearGlass), 0.1f);
    }
  }
}
