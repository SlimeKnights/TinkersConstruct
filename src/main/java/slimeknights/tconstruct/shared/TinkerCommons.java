package slimeknights.tconstruct.shared;

import org.apache.logging.log4j.Logger;

import com.google.common.eventbus.Subscribe;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import slimeknights.mantle.item.ItemEdible;
import slimeknights.mantle.item.ItemMetaDynamic;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.common.CommonProxy;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.common.item.ItemTinkerBook;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.shared.block.BlockDecoGround;
import slimeknights.tconstruct.shared.block.BlockFirewood;
import slimeknights.tconstruct.shared.block.BlockFirewoodSlab;
import slimeknights.tconstruct.shared.block.BlockGlow;
import slimeknights.tconstruct.shared.block.BlockMetal;
import slimeknights.tconstruct.shared.block.BlockOre;
import slimeknights.tconstruct.shared.block.BlockSoil;
import slimeknights.tconstruct.shared.block.BlockDecoGroundSlab;
import slimeknights.tconstruct.shared.item.ItemMetaDynamicTinkers;
import slimeknights.tconstruct.shared.worldgen.NetherOreGenerator;
import slimeknights.tconstruct.tools.TinkerTools;

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

  public static Block slabDecoGround;
  public static Block slabFirewood;
  
  // stairs
  public static Block stairsMudBrick;
  public static Block stairsFirewood;
  public static Block stairsLavawood;

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

  @Subscribe
  public void preInit(FMLPreInitializationEvent event) {
    boolean forced = Config.forceRegisterAll; // causes to always register all items

    book = registerItem(new ItemTinkerBook(), "book");

    // Soils
    blockSoil = registerEnumBlock(new BlockSoil(), "soil");

    grout = new ItemStack(blockSoil, 1, BlockSoil.SoilTypes.GROUT.getMeta());
    slimyMudGreen = new ItemStack(blockSoil, 1, BlockSoil.SoilTypes.SLIMY_MUD_GREEN.getMeta());
    slimyMudBlue = new ItemStack(blockSoil, 1, BlockSoil.SoilTypes.SLIMY_MUD_BLUE.getMeta());
    slimyMudMagma = new ItemStack(blockSoil, 1, BlockSoil.SoilTypes.SLIMY_MUD_MAGMA.getMeta());
    graveyardSoil = new ItemStack(blockSoil, 1, BlockSoil.SoilTypes.GRAVEYARD.getMeta());
    consecratedSoil = new ItemStack(blockSoil, 1, BlockSoil.SoilTypes.CONSECRATED.getMeta());

    // Ores
    blockOre = registerEnumBlock(new BlockOre(), "ore");

    oreCobalt = new ItemStack(blockOre, 1, BlockOre.OreTypes.COBALT.getMeta());
    oreArdite = new ItemStack(blockOre, 1, BlockOre.OreTypes.ARDITE.getMeta());

    blockFirewood = registerEnumBlock(new BlockFirewood(), "firewood");
    blockFirewood.setLightLevel(0.5f);
    blockFirewood.setCreativeTab(TinkerRegistry.tabGeneral);
    lavawood = new ItemStack(blockFirewood, 1, BlockFirewood.FirewoodType.LAVAWOOD.getMeta());
    firewood = new ItemStack(blockFirewood, 1, BlockFirewood.FirewoodType.FIREWOOD.getMeta());

    // deco stuff
    blockDecoGround = registerEnumBlock(new BlockDecoGround(), "deco_ground");
    mudBrickBlock = new ItemStack(blockDecoGround, 1, BlockDecoGround.DecoGroundType.MUDBRICK.getMeta());

    // slabs
    slabDecoGround = registerEnumBlockSlab(new BlockDecoGroundSlab(), "deco_ground_slab");
    slabFirewood = registerEnumBlockSlab(new BlockFirewoodSlab(), "firewood_slab");
    
    // stairs
    stairsMudBrick = registerBlockStairsFrom(blockDecoGround, BlockDecoGround.DecoGroundType.MUDBRICK, "mudbrick_stairs");
    stairsFirewood = registerBlockStairsFrom(blockFirewood, BlockFirewood.FirewoodType.FIREWOOD, "firewood_stairs");
    stairsLavawood = registerBlockStairsFrom(blockFirewood, BlockFirewood.FirewoodType.LAVAWOOD, "lavawood_stairs");

    // create the items. We can probably always create them since they handle themselves dynamically
    nuggets = registerItem(new ItemMetaDynamicTinkers(), "nuggets");
    ingots = registerItem(new ItemMetaDynamicTinkers(), "ingots");
    materials = registerItem(new ItemMetaDynamic(), "materials");
    edibles = registerItem(new ItemEdible(), "edible");

    nuggets.setCreativeTab(TinkerRegistry.tabGeneral);
    ingots.setCreativeTab(TinkerRegistry.tabGeneral);
    materials.setCreativeTab(TinkerRegistry.tabGeneral);
    edibles.setCreativeTab(TinkerRegistry.tabGeneral);
    edibles.setAlwaysEdible();

    // Items that can always be present.. slimeballs
    matSlimeBallBlue = edibles.addFood(1, 1, 1f, "slimeball_blue", new PotionEffect(MobEffects.SLOWNESS, 20*45, 2), new PotionEffect(MobEffects.JUMP_BOOST, 20*60, 2) );
    matSlimeBallPurple = edibles.addFood(2, 1, 2f, "slimeball_purple", new PotionEffect(MobEffects.UNLUCK, 20*45), new PotionEffect(MobEffects.LUCK, 20*60));
    matSlimeBallBlood = edibles.addFood(3, 1, 1.5f, "slimeball_blood", new PotionEffect(MobEffects.POISON, 20*45, 2), new PotionEffect(MobEffects.HEALTH_BOOST, 20*60));
    matSlimeBallMagma = edibles.addFood(4, 2, 1f, "slimeball_magma", new PotionEffect(MobEffects.WEAKNESS, 20*45), new PotionEffect(MobEffects.WITHER, 20*15), new PotionEffect(MobEffects.FIRE_RESISTANCE, 20*60));

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

      blockMetal = registerEnumBlock(new BlockMetal(), "metal");

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

      matCreativeModifier = materials.addMeta(50, "creative_modifier");

      ingotKnightSlime = ingots.addMeta(3, "knightslime");
      nuggetKnightSlime = nuggets.addMeta(3, "knightslime");
    }

    if(isGadgetsLoaded() || forced) {
      driedBrick = materials.addMeta(2, "dried_brick");

      // Jerky
      jerkyMonster = edibles.addFood(10, 4, 0.4f, "jerky_monster");
      jerkyBeef = edibles.addFood(11, 8, 1f, "jerky_beef");
      jerkyChicken = edibles.addFood(12, 6, 0.8f, "jerky_chicken");
      jerkyPork = edibles.addFood(13, 8, 1f, "jerky_pork");
      jerkyMutton = edibles.addFood(14, 6, 1f, "jerky_mutton");
      jerkyRabbit = edibles.addFood(15, 5, 0.8f, "jerky_rabbit");

      jerkyFish = edibles.addFood(20, 5, 0.8f, "jerky_fish");
      jerkySalmon = edibles.addFood(21, 6, 1f, "jerky_salmon");
      jerkyClownfish = edibles.addFood(22, 3, 0.8f, "jerky_clownfish");
      jerkyPufferfish = edibles.addFood(23, 3, 0.8f, "jerky_pufferfish");

      slimedropGreen = edibles.addFood(30, 1, 1f, "slimedrop_green", new PotionEffect(MobEffects.SPEED, 20*90, 2));
      slimedropBlue = edibles.addFood(31, 3, 1f, "slimedrop_blue", new PotionEffect(MobEffects.JUMP_BOOST, 20*90, 2));
      slimedropPurple = edibles.addFood(32, 3, 2f, "slimedrop_purple", new PotionEffect(MobEffects.LUCK, 20*90));
      slimedropBlood = edibles.addFood(33, 3, 1.5f, "slimedrop_blood", new PotionEffect(MobEffects.HEALTH_BOOST, 20*90));
      slimedropMagma = edibles.addFood(34, 6, 1f, "slimedrop_magma", new PotionEffect(MobEffects.FIRE_RESISTANCE, 20*90));
    }

    if(isToolsLoaded() || isGadgetsLoaded()) {
      blockGlow = registerBlock(new BlockGlow(), "glow");
    }

    proxy.preInit();

    TinkerRegistry.tabGeneral.setDisplayIcon(matSlimeBallBlue);
  }

  @Subscribe
  public void init(FMLInitializationEvent event) {
    registerRecipies();

    GameRegistry.registerWorldGenerator(NetherOreGenerator.INSTANCE, 0);

    MinecraftForge.EVENT_BUS.register(new AchievementEvents());
    MinecraftForge.EVENT_BUS.register(new PlayerDataEvents());
  }

  private void registerRecipies() {
    // book
    if(isToolsLoaded()) {
      GameRegistry.addShapelessRecipe(new ItemStack(book), new ItemStack(Items.BOOK), new ItemStack(TinkerTools.pattern));
    }

    // soils
    GameRegistry.addSmelting(graveyardSoil, consecratedSoil, 0);
    GameRegistry.addShapelessRecipe(graveyardSoil, Blocks.DIRT, Items.ROTTEN_FLESH, new ItemStack(Items.DYE, 1, 15));
    if(mudBrick != null) {
      GameRegistry.addShapedRecipe(mudBrickBlock, "BB", "BB", 'B', mudBrick);
      GameRegistry.addShapedRecipe(new ItemStack(slabDecoGround, 1, BlockDecoGround.DecoGroundType.MUDBRICK.getMeta()), "bb", 'b', mudBrick);
    }
    addSlabRecipe(new ItemStack(slabDecoGround, 1, BlockDecoGround.DecoGroundType.MUDBRICK.getMeta()), mudBrickBlock);
    addStairRecipe(stairsMudBrick, mudBrickBlock);

    // firewood
    GameRegistry.addShapelessRecipe(firewood, Items.BLAZE_POWDER, lavawood, Items.BLAZE_POWDER);
    addSlabRecipe(new ItemStack(slabFirewood, 1, BlockFirewood.FirewoodType.FIREWOOD.getMeta()), firewood);
    addSlabRecipe(new ItemStack(slabFirewood, 1, BlockFirewood.FirewoodType.LAVAWOOD.getMeta()), lavawood);
    addStairRecipe(stairsFirewood, firewood);
    addStairRecipe(stairsLavawood, lavawood);

    // metals
    registerMetalRecipes("Cobalt", ingotCobalt, nuggetCobalt, blockCobalt);
    registerMetalRecipes("Ardite", ingotArdite, nuggetArdite, blockArdite);
    registerMetalRecipes("Manyullyn", ingotManyullyn, nuggetManyullyn, blockManyullyn);
    registerMetalRecipes("Knightslime", ingotKnightSlime, nuggetKnightSlime, blockKnightSlime);
    registerMetalRecipes("Pigiron", ingotPigIron, nuggetPigIron, blockPigIron);
    registerMetalRecipes("Alubrass", ingotAlubrass, nuggetAlubrass, blockAlubrass);

    if(blockSilkyJewel != null && matSilkyJewel != null) {
      GameRegistry.addShapedRecipe(blockSilkyJewel, "###", "###", "###", '#', matSilkyJewel);
    }
  }

  private static void registerMetalRecipes(String oreString, ItemStack ingot, ItemStack nugget, ItemStack block) {
    if(ingot == null) {
      return;
    }

    // nugget recipies
    if(nugget != null) {
      registerFullrecipe(nugget, ingot, "nugget" + oreString, "ingot" + oreString);
    }
    // block recipies
    if(block != null) {
      registerFullrecipe(ingot, block, "ingot" + oreString, "block" + oreString);
    }
  }

  private static void registerFullrecipe(ItemStack small, ItemStack big, String oreSmall, String oreBig) {
    // ingot -> block
    //GameRegistry.addShapedRecipe(big, "###", "###", "###", '#', small);
    GameRegistry.addRecipe(new ShapedOreRecipe(big, "###", "###", "###", '#', oreSmall));
    // block -> 9 ingot
    small = small.copy();
    small.stackSize = 9;
    //GameRegistry.addShapelessRecipe(small, big);
    GameRegistry.addRecipe(new ShapelessOreRecipe(small, oreBig));
  }
}
