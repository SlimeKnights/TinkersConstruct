package slimeknights.tconstruct.tools;

import com.google.common.collect.Sets;

import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Set;

import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.tools.events.TinkerToolEvent;

public class ToolEvents {

  public final static Set<ToolCore> smallTools = Sets.newHashSet();

  // Extra width/height modifier management
  @SubscribeEvent
  public void onExtraBlockBreak(TinkerToolEvent.ExtraBlockBreak event) {
    if(TinkerTools.harvestWidth == null || TinkerTools.harvestHeight == null) return;

    NBTTagList modifiers = TagUtil.getBaseModifiersTagList(event.itemStack);
    boolean width = false;
    boolean height = false;
    for(int i = 0; i < modifiers.tagCount(); i++) {
      String modId = modifiers.getStringTagAt(i);
      if(modId.equals(TinkerTools.harvestWidth.getIdentifier())) {
        width = true;
      }
      else if(modId.equals(TinkerTools.harvestHeight.getIdentifier())) {
        height = true;
      }
    }

    if(!width && !height) {
      return;
    }

    if(event.tool == TinkerTools.pickaxe ||
       event.tool == TinkerTools.hatchet ||
       event.tool == TinkerTools.shovel) {
      event.width += width ? 1 : 0;
      event.height += height ? 1 : 0;
    }
    else if(event.tool == TinkerTools.mattock) {
      int c = 0;
      if(width) c++;
      if(height) c++;
      event.width += c;
      event.height += c;
    }
    else if(event.tool == TinkerTools.hammer ||
            event.tool == TinkerTools.excavator ||
            event.tool == TinkerTools.lumberAxe) {
      event.width += width ? 2 : 0;
      event.height += height ? 2 : 0;
      event.distance = 2;
    }
  }
}
