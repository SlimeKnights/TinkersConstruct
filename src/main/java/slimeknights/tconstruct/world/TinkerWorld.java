package slimeknights.tconstruct.world;

import com.google.common.eventbus.Subscribe;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

import org.apache.logging.log4j.Logger;

import slimeknights.mantle.item.ItemBlockMeta;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.CommonProxy;
import slimeknights.tconstruct.common.EntityIDs;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.world.block.BlockSlimeDirt;
import slimeknights.tconstruct.world.block.BlockSlimeGrass;
import slimeknights.tconstruct.world.block.BlockSlimeLeaves;
import slimeknights.tconstruct.world.block.BlockSlimeSapling;
import slimeknights.tconstruct.world.block.BlockSlimeVine;
import slimeknights.tconstruct.world.block.BlockTallSlimeGrass;
import slimeknights.tconstruct.world.entity.EntityBlueSlime;
import slimeknights.tconstruct.world.item.ItemBlockLeaves;
import slimeknights.tconstruct.world.worldgen.MagmaSlimeIslandGenerator;
import slimeknights.tconstruct.world.worldgen.SlimeIslandGenerator;

@Pulse(id = TinkerWorld.PulseId, description = "Everything that's found in the world and worldgen")
public class TinkerWorld extends TinkerPulse {

  public static final String PulseId = "TinkerWorld";
  static final Logger log = Util.getLogger(PulseId);

  @SidedProxy(clientSide = "slimeknights.tconstruct.world.WorldClientProxy", serverSide = "slimeknights.tconstruct.common.CommonProxy")
  public static CommonProxy proxy;

  public static BlockSlimeDirt slimeDirt;
  public static BlockSlimeGrass slimeGrass;
  public static BlockSlimeLeaves slimeLeaves;
  public static BlockTallSlimeGrass slimeGrassTall;
  public static BlockSlimeSapling slimeSapling;
  public static BlockSlimeVine slimeVineBlue1;
  public static BlockSlimeVine slimeVinePurple1;
  public static BlockSlimeVine slimeVineBlue2;
  public static BlockSlimeVine slimeVinePurple2;
  public static BlockSlimeVine slimeVineBlue3;
  public static BlockSlimeVine slimeVinePurple3;

  public static final EnumPlantType slimePlantType = EnumPlantType.getPlantType("slime");

  // PRE-INITIALIZATION
  @Subscribe
  public void preInit(FMLPreInitializationEvent event) {
    slimeDirt = registerEnumBlock(new BlockSlimeDirt(), "slime_dirt");
    slimeGrass = registerBlock(new BlockSlimeGrass(), "slime_grass", BlockSlimeGrass.TYPE);
    slimeLeaves = registerBlock(new ItemBlockLeaves(new BlockSlimeLeaves()), "slime_leaves");
    ItemBlockMeta.setMappingProperty(slimeLeaves, BlockSlimeGrass.FOLIAGE);
    slimeGrassTall = registerBlock(new BlockTallSlimeGrass(), "slime_grass_tall", BlockTallSlimeGrass.TYPE);
    slimeSapling = registerBlock(new BlockSlimeSapling(), "slime_sapling", BlockSlimeGrass.FOLIAGE);

    slimeVineBlue3 = registerBlock(new BlockSlimeVine(BlockSlimeGrass.FoliageType.BLUE, null), "slime_vine_blue_end");
    slimeVineBlue2 = registerBlock(new BlockSlimeVine(BlockSlimeGrass.FoliageType.BLUE, slimeVineBlue3), "slime_vine_blue_mid");
    slimeVineBlue1 = registerBlock(new BlockSlimeVine(BlockSlimeGrass.FoliageType.BLUE, slimeVineBlue2), "slime_vine_blue");

    slimeVinePurple3 = registerBlock(new BlockSlimeVine(BlockSlimeGrass.FoliageType.PURPLE, null), "slime_vine_purple_end");
    slimeVinePurple2 = registerBlock(new BlockSlimeVine(BlockSlimeGrass.FoliageType.PURPLE, slimeVinePurple3), "slime_vine_purple_mid");
    slimeVinePurple1 = registerBlock(new BlockSlimeVine(BlockSlimeGrass.FoliageType.PURPLE, slimeVinePurple2), "slime_vine_purple");

    EntityRegistry.registerModEntity(EntityBlueSlime.class, "blueslime", EntityIDs.BLUESLIME, TConstruct.instance, 64, 5, true, 0x47eff5, 0xacfff4);
    LootTableList.register(EntityBlueSlime.LOOT_TABLE);
    //EntitySpawnPlacementRegistry.setPlacementType(EntityBlueSlime.class, EntityLiving.SpawnPlacementType.IN_WATER);


    proxy.preInit();

    TinkerRegistry.tabWorld.setDisplayIcon(new ItemStack(slimeSapling));
  }

  // INITIALIZATION
  @Subscribe
  public void init(FMLInitializationEvent event) {
    addRecipies();
    proxy.init();
  }

  private void addRecipies() {

    // Recipes to get slimy grass. Because why not
    IBlockState vanillaDirtState = Blocks.DIRT.getDefaultState();
    IBlockState grassState = slimeGrass.getDefaultState().withProperty(BlockSlimeGrass.TYPE, BlockSlimeGrass.DirtType.VANILLA);
    int meta = slimeGrass.getMetaFromState(grassState.withProperty(BlockSlimeGrass.FOLIAGE, BlockSlimeGrass.FoliageType.BLUE));
    ItemStack slime = TinkerCommons.matSlimeBallBlue.copy();
    GameRegistry.addShapedRecipe(new ItemStack(slimeGrass, 1, meta), " s ", "sBs", " s ", 's', slime, 'B', Blocks.GRASS);

    meta = slimeGrass.getMetaFromState(grassState.withProperty(BlockSlimeGrass.FOLIAGE, BlockSlimeGrass.FoliageType.PURPLE));
    slime = TinkerCommons.matSlimeBallPurple.copy();
    GameRegistry.addShapedRecipe(new ItemStack(slimeGrass, 1, meta), " s ", "sBs", " s ", 's', slime, 'B', Blocks.GRASS);

    meta = slimeGrass.getMetaFromState(grassState.withProperty(BlockSlimeGrass.FOLIAGE, BlockSlimeGrass.FoliageType.ORANGE));
    slime = TinkerCommons.matSlimeBallMagma.copy();
    GameRegistry.addShapedRecipe(new ItemStack(slimeGrass, 1, meta), " s ", "sBs", " s ", 's', slime, 'B', Blocks.GRASS);
  }

  // POST-INITIALIZATION
  @Subscribe
  public void postInit(FMLPostInitializationEvent event) {
    GameRegistry.registerWorldGenerator(SlimeIslandGenerator.INSTANCE, 25);
    GameRegistry.registerWorldGenerator(MagmaSlimeIslandGenerator.INSTANCE, 25);

    MinecraftForge.EVENT_BUS.register(new WorldEvents());

    proxy.postInit();
  }
}
