package tconstruct.test;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

import org.apache.logging.log4j.Logger;

import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import tconstruct.TinkerPulse;
import tconstruct.Util;
import tconstruct.tools.TinkerTools;

@Pulse(id = TinkerTools.PulseId)
public class TinkerTest extends TinkerPulse {

  public static final String PulseId = "TinkerTest";
  static final Logger log = Util.getLogger(PulseId);

  public static Item testItem;

  @Handler
  public void preInit(FMLPreInitializationEvent event) {

  }

  @Handler
  public void init(FMLInitializationEvent event) {
    testItem = new Item();
    registerItem(testItem, "TestTool");

    // This should be in a client-proxy
    if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
      Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
          .register(testItem, 0, new ModelResourceLocation("TConstruct:TestTool", "inventory"));

      ModelBakery.addVariantName(testItem, "tconstruct:TestTool", "tconstruct:pick_head", "tconstruct:pick_handle", "tconstruct:pick_binding");
    }
  }

  @Handler
  public void postInit(FMLPostInitializationEvent event) {
    MinecraftForge.EVENT_BUS.register(new TinkerModelManager());
    MinecraftForge.EVENT_BUS.register(new CustomTextureCreator());
  }

}
