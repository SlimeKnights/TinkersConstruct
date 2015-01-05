package tconstruct.tools;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import tconstruct.Util;
import tconstruct.library.tools.PartMaterialWrapper;
import tconstruct.library.tools.ToolCore;
import tconstruct.library.tools.ToolPart;
import tconstruct.library.tools.materials.ToolMaterialStats;
import tconstruct.library.utils.ToolBuilder;

@Pulse(id = TinkerTools.PulseId, description = "This module contains all the tools and everything related to it.")
public class TinkerTools {

  public static final String PulseId = "TinkerTools";

  @Handler
  public void init(FMLPreInitializationEvent event) {
    ToolPart a, b;
    a = new ToolPart();
    b = new ToolPart();

    PartMaterialWrapper c, d;
    c = new PartMaterialWrapper(a, ToolMaterialStats.TYPE);
    d = new PartMaterialWrapper(b, ToolMaterialStats.TYPE);

    ToolCore testTool = new TestTool(c, d);

    ItemStack e, f;
    e = new ItemStack(a, 1, TinkerMaterials.stone.metadata);
    f = new ItemStack(b, 1, TinkerMaterials.wood.metadata);

    if (testTool.validComponent(0, e) && testTool.validComponent(1, f)) {

    }
  }
}
