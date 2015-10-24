package slimeknights.tconstruct.world;

import com.google.common.eventbus.Subscribe;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import org.apache.logging.log4j.Logger;

import slimeknights.mantle.item.ItemBlockMeta;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.common.CommonProxy;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.world.block.BlockCongealedSlime;
import slimeknights.tconstruct.world.block.BlockSlime;

@Pulse(id = TinkerWorld.PulseId, description = "Everything that's found in the world and worldgen")
public class TinkerWorld extends TinkerPulse {

  public static final String PulseId = "TinkerWorld";
  static final Logger log = Util.getLogger(PulseId);

  @SidedProxy(clientSide = "slimeknights.tconstruct.common.CommonProxy", serverSide = "slimeknights.tconstruct.common.CommonProxy")
  public static CommonProxy proxy;

  public static Block slimeBlock;
  public static Block slimeBlockCongealed;

  // PRE-INITIALIZATION
  @Subscribe
  public void preInit(FMLPreInitializationEvent event) {
    slimeBlock = registerBlock(new BlockSlime(), ItemBlockMeta.class, "slime");
    slimeBlockCongealed = registerBlock(new BlockCongealedSlime(), ItemBlockMeta.class, "slime_congealed");

    ItemBlockMeta.setMappingProperty(slimeBlock, BlockSlime.TYPE);
    ItemBlockMeta.setMappingProperty(slimeBlockCongealed, BlockSlime.TYPE);

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
    proxy.postInit();
  }
}
