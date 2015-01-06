package tconstruct.tools;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import org.apache.logging.log4j.Logger;

import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import tconstruct.Util;
import tconstruct.library.tools.PartMaterialWrapper;
import tconstruct.library.tools.ToolCore;
import tconstruct.library.tools.ToolPart;
import tconstruct.library.tools.materials.ToolMaterialStats;

@Pulse(id = TinkerTools.PulseId, description = "This module contains all the tools and everything related to it.")
public class TinkerTools {

  public static final String PulseId = "TinkerTools";
  static final Logger log = Util.getLogger(PulseId);

  @Handler
  public void preInit(FMLPreInitializationEvent event) {
    TinkerMaterials.registerToolMaterials();
  }

  @Handler
  public void init(FMLInitializationEvent event) {
    ToolPart a, b;
    a = new ToolPart();
    b = new ToolPart();

    GameRegistry.registerItem(a, "ItemA");
    GameRegistry.registerItem(b, "ItemB");

    PartMaterialWrapper c, d;
    c = new PartMaterialWrapper(a, ToolMaterialStats.TYPE);
    d = new PartMaterialWrapper(b, ToolMaterialStats.TYPE);

    ToolCore testTool = new TestTool(c, d);

    GameRegistry.registerItem(testTool, "TestTool");

    ItemStack e, f;
    e = new ItemStack(a, 1, TinkerMaterials.stone.metadata);
    f = new ItemStack(b, 1, TinkerMaterials.wood.metadata);

    ItemStack result = testTool.buildTool(new ItemStack[]{e, f});
    log.info(result.hasTagCompound());
  }
}
