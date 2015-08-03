package tconstruct.smeltery;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import tconstruct.library.Util;
import tconstruct.tools.ToolClientEvents;

public class SmelteryClientEvents {

  // Blank Pattern
  private static final ResourceLocation MODEL_BlankCast = Util.getResource("item/Cast");
  public static final ResourceLocation locBlankCast = Util.getResource("Cast");

  @SubscribeEvent
  public void onModelBake(ModelBakeEvent event) {
    // add the extra cast models. See ToolClientEvents for more info with the pattern
    ToolClientEvents.replacePatternModel(locBlankCast, MODEL_BlankCast, event);
  }
}
