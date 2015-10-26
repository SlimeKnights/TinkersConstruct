package slimeknights.tconstruct.world;

import com.google.common.eventbus.Subscribe;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
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
import slimeknights.tconstruct.world.block.BlockCongealedSlime;
import slimeknights.tconstruct.world.block.BlockSlime;
import slimeknights.tconstruct.world.block.BlockSlimeDirt;
import slimeknights.tconstruct.world.block.BlockSlimeGrass;
import slimeknights.tconstruct.world.client.SlimeColorizer;
import slimeknights.tconstruct.world.worldgen.SlimeIslandGenerator;

@Pulse(id = TinkerWorld.PulseId, description = "Everything that's found in the world and worldgen")
public class TinkerWorld extends TinkerPulse {

  public static final String PulseId = "TinkerWorld";
  static final Logger log = Util.getLogger(PulseId);

  @SidedProxy(clientSide = "slimeknights.tconstruct.world.WorldClientProxy", serverSide = "slimeknights.tconstruct.common.CommonProxy")
  public static CommonProxy proxy;

  public static BlockSlime slimeBlock;
  public static BlockCongealedSlime slimeBlockCongealed;
  public static BlockSlimeDirt slimeDirt;
  public static BlockSlimeGrass slimeGrass;

  // PRE-INITIALIZATION
  @Subscribe
  public void preInit(FMLPreInitializationEvent event) {
    slimeBlock = registerBlock(new BlockSlime(), ItemBlockMeta.class, "slime");
    slimeBlockCongealed = registerBlock(new BlockCongealedSlime(), ItemBlockMeta.class, "slime_congealed");

    slimeDirt = registerEnumBlock(new BlockSlimeDirt(), "slime_dirt");
    slimeGrass = registerBlock(new BlockSlimeGrass(), ItemBlockMeta.class, "slime_grass");

    ItemBlockMeta.setMappingProperty(slimeBlock, BlockSlime.TYPE);
    ItemBlockMeta.setMappingProperty(slimeBlockCongealed, BlockSlime.TYPE);
    ItemBlockMeta.setMappingProperty(slimeGrass, BlockSlimeGrass.TYPE);

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
