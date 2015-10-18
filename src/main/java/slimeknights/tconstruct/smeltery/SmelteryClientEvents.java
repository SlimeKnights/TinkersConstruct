package slimeknights.tconstruct.smeltery;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.CustomTextureCreator;
import slimeknights.tconstruct.tools.ToolClientEvents;

public class SmelteryClientEvents {

  // Blank Pattern
  private static final ResourceLocation MODEL_BlankCast = Util.getResource("item/cast");
  public static final ResourceLocation locBlankCast = Util.getResource("cast");

  @SubscribeEvent
  public void onModelBake(ModelBakeEvent event) {
    // add the extra cast models. See ToolClientEvents for more info with the pattern
    ToolClientEvents.replacePatternModel(locBlankCast, MODEL_BlankCast, event, CustomTextureCreator.castLocString);
  }
}
