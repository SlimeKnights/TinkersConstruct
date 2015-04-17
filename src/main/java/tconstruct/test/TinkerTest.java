package tconstruct.test;

import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import org.apache.logging.log4j.Logger;

import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import tconstruct.CommonProxy;
import tconstruct.TinkerPulse;
import tconstruct.Util;
import tconstruct.tools.TinkerTools;
import tconstruct.library.client.CustomTextureCreator;

@Pulse(id = TinkerTools.PulseId)
public class TinkerTest extends TinkerPulse {

  public static final String PulseId = "TinkerTest";
  static final Logger log = Util.getLogger(PulseId);

  @SidedProxy(clientSide = "tconstruct.test.TestClientProxy", serverSide = "tconstruct.CommonProxy")
  public static CommonProxy proxy;

  public static Item testItem;

  @Handler
  public void preInit(FMLPreInitializationEvent event) {
    testItem = new Item();
    registerItem(testItem, "TestTool");
  }

  @Handler
  public void init(FMLInitializationEvent event) {
    proxy.registerModels();
  }

  @Handler
  public void postInit(FMLPostInitializationEvent event) {
    MinecraftForge.EVENT_BUS.register(new CustomTextureCreator());
  }

}
