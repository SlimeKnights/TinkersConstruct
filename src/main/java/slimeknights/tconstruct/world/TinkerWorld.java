package slimeknights.tconstruct.world;

import com.google.common.eventbus.Subscribe;

import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import org.apache.logging.log4j.Logger;

import slimeknights.mantle.item.ItemBlockMeta;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.common.CommonProxy;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.world.block.BlockSlimeCongealed;
import slimeknights.tconstruct.world.block.BlockSlime;
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
  public static BlockSlimeVine slimeVine;

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
    slimeVine = registerBlock(new BlockSlimeVine(), ItemBlockMeta.class, "slime_vine");

    ItemBlockMeta.setMappingProperty(slimeBlock, BlockSlime.TYPE);
    ItemBlockMeta.setMappingProperty(slimeBlockCongealed, BlockSlime.TYPE);
    ItemBlockMeta.setMappingProperty(slimeGrass, BlockSlimeGrass.TYPE);
    ItemBlockMeta.setMappingProperty(slimeLeaves, BlockSlimeGrass.FOLIAGE);
    ItemBlockMeta.setMappingProperty(slimeGrassTall, BlockTallSlimeGrass.TYPE);
    ItemBlockMeta.setMappingProperty(slimeSapling, BlockSlimeGrass.FOLIAGE);
    ItemBlockMeta.setMappingProperty(slimeVine, BlockSlimeGrass.FOLIAGE);

    proxy.preInit();
  }

  // INITIALIZATION
  @Subscribe
  public void init(FMLInitializationEvent event) {
    proxy.init();
  }

  // POST-INITIALIZATION
  @Subscribe
  public void postInit(FMLPostInitializationEvent event) {
    GameRegistry.registerWorldGenerator(new SlimeIslandGenerator(), 5);

    proxy.postInit();
  }
}
