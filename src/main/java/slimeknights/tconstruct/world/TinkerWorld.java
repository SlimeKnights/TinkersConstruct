package slimeknights.tconstruct.world;

import com.google.common.eventbus.Subscribe;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.logging.log4j.Logger;

import slimeknights.mantle.item.ItemBlockMeta;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.common.CommonProxy;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.world.block.BlockSlime;
import slimeknights.tconstruct.world.block.BlockSlimeCongealed;
import slimeknights.tconstruct.world.block.BlockSlimeDirt;
import slimeknights.tconstruct.world.block.BlockSlimeGrass;
import slimeknights.tconstruct.world.block.BlockSlimeLeaves;
import slimeknights.tconstruct.world.block.BlockSlimeSapling;
import slimeknights.tconstruct.world.block.BlockSlimeVine;
import slimeknights.tconstruct.world.block.BlockTallSlimeGrass;
import slimeknights.tconstruct.world.item.ItemBlockLeaves;
import slimeknights.tconstruct.world.worldgen.SlimeIslandGenerator;

@Pulse(id = TinkerWorld.PulseId, description = "Everything that's found in the world and worldgen")
public class TinkerWorld extends TinkerPulse {

  public static final String PulseId = "TinkerWorld";
  static final Logger log = Util.getLogger(PulseId);

  @SidedProxy(clientSide = "slimeknights.tconstruct.world.WorldClientProxy", serverSide = "slimeknights.tconstruct.common.CommonProxy")
  public static CommonProxy proxy;

  public static BlockSlime slimeBlock;
  public static BlockSlimeCongealed slimeBlockCongealed;
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
    slimeBlock = registerBlock(new BlockSlime(), ItemBlockMeta.class, "slime");
    slimeBlockCongealed = registerBlock(new BlockSlimeCongealed(), ItemBlockMeta.class, "slime_congealed");

    slimeDirt = registerEnumBlock(new BlockSlimeDirt(), "slime_dirt");
    slimeGrass = registerBlock(new BlockSlimeGrass(), ItemBlockMeta.class, "slime_grass");
    slimeLeaves = registerBlock(new BlockSlimeLeaves(), ItemBlockLeaves.class, "slime_leaves");
    slimeGrassTall = registerBlock(new BlockTallSlimeGrass(), ItemBlockMeta.class, "slime_grass_tall");
    slimeSapling = registerBlock(new BlockSlimeSapling(), ItemBlockMeta.class, "slime_sapling");

    slimeVineBlue3 = registerBlock(new BlockSlimeVine(BlockSlimeGrass.FoliageType.BLUE, null), ItemBlockMeta.class, "slime_vine_blue_end");
    slimeVineBlue2 = registerBlock(new BlockSlimeVine(BlockSlimeGrass.FoliageType.BLUE, slimeVineBlue3), ItemBlockMeta.class, "slime_vine_blue_mid");
    slimeVineBlue1 = registerBlock(new BlockSlimeVine(BlockSlimeGrass.FoliageType.BLUE, slimeVineBlue2), ItemBlockMeta.class, "slime_vine_blue");

    slimeVinePurple3 = registerBlock(new BlockSlimeVine(BlockSlimeGrass.FoliageType.PURPLE, null), ItemBlockMeta.class, "slime_vine_purple_end");
    slimeVinePurple2 = registerBlock(new BlockSlimeVine(BlockSlimeGrass.FoliageType.PURPLE, slimeVinePurple3), ItemBlockMeta.class, "slime_vine_purple_mid");
    slimeVinePurple1 = registerBlock(new BlockSlimeVine(BlockSlimeGrass.FoliageType.PURPLE, slimeVinePurple2), ItemBlockMeta.class, "slime_vine_purple");

    ItemBlockMeta.setMappingProperty(slimeBlock, BlockSlime.TYPE);
    ItemBlockMeta.setMappingProperty(slimeBlockCongealed, BlockSlime.TYPE);
    ItemBlockMeta.setMappingProperty(slimeGrass, BlockSlimeGrass.TYPE);
    ItemBlockMeta.setMappingProperty(slimeLeaves, BlockSlimeGrass.FOLIAGE);
    ItemBlockMeta.setMappingProperty(slimeGrassTall, BlockTallSlimeGrass.TYPE);
    ItemBlockMeta.setMappingProperty(slimeSapling, BlockSlimeGrass.FOLIAGE);

    oredict();
    proxy.preInit();
  }

  private void oredict() {
    OreDictionary.registerOre("blockSlime", slimeBlock);
  }

  // INITIALIZATION
  @Subscribe
  public void init(FMLInitializationEvent event) {
    addRecipies();
    proxy.init();
  }

  private void addRecipies() {
    // Slimeblocks

    // green slime
    addSlimeRecipes(new ItemStack(Items.slime_ball), BlockSlime.SlimeType.GREEN);

    // blue slime
    addSlimeRecipes(TinkerCommons.matSlimeBallBlue, BlockSlime.SlimeType.BLUE);

    // purple slime
    addSlimeRecipes(TinkerCommons.matSlimeBallPurple, BlockSlime.SlimeType.PURPLE);

    // blood slime
    addSlimeRecipes(TinkerCommons.matSlimeBallBlood, BlockSlime.SlimeType.BLOOD);

    // magma slime
    //stack.setItemDamage(slimeBlockCongealed.getMetaFromState(state.withProperty(BlockSlime.TYPE, BlockSlime.SlimeType.MAGMA)));
    //GameRegistry.addRecipe(stack, "##", "##", '#', ???);

  }

  private void addSlimeRecipes(ItemStack slimeball, BlockSlime.SlimeType type) {
    ItemStack congealed = new ItemStack(slimeBlockCongealed);
    congealed.setItemDamage(slimeBlockCongealed.getMetaFromState(slimeBlockCongealed.getDefaultState().withProperty(BlockSlime.TYPE, type)));

    ItemStack block = new ItemStack(slimeBlock);
    block.setItemDamage(slimeBlock.getMetaFromState(slimeBlock.getDefaultState().withProperty(BlockSlime.TYPE, type)));

    GameRegistry.addRecipe(congealed.copy(), "##", "##", '#', slimeball);
    // todo: if we ever get the slimeblock substitution, track if it was successful and alter the recipe used to match vanilla
    GameRegistry.addRecipe(block, "#", '#', congealed);
  }

  // POST-INITIALIZATION
  @Subscribe
  public void postInit(FMLPostInitializationEvent event) {
    GameRegistry.registerWorldGenerator(new SlimeIslandGenerator(), 5);

    proxy.postInit();
  }
}
