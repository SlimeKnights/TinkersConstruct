package slimeknights.tconstruct.tools.traits;

import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import slimeknights.tconstruct.library.events.TinkerEvent;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.TagUtil;

public class TraitCheapskate extends AbstractTrait {

  public TraitCheapskate() {
    super("cheapskate", EnumChatFormatting.GRAY);
    MinecraftForge.EVENT_BUS.register(this);
  }

  @SubscribeEvent
  public void onToolBuilding(TinkerEvent.OnItemBuilding event) {
    ToolNBT data = TagUtil.getToolStats(event.tag);
    // reduce durability by 20%
    data.durability = Math.max(1, (data.durability * 80) / 100);
    TagUtil.setToolTag(event.tag, data.get());
  }
}
