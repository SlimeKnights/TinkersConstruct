package slimeknights.tconstruct.tools;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.traits.ITrait;
import slimeknights.tconstruct.library.utils.TagUtil;

public class TraitEvents {

  @SubscribeEvent
  public void mineSpeed(PlayerEvent.BreakSpeed event) {
    ItemStack tool = event.entityPlayer.inventory.getCurrentItem();

    if(isTool(tool)) {
      NBTTagList list = TagUtil.getTraitsTagList(tool);
      for(int i = 0; i < list.tagCount(); i++) {
        ITrait trait = TinkerRegistry.getTrait(list.getStringTagAt(i));
        if(trait != null) {
          trait.miningSpeed(tool, event);
        }
      }
    }
  }

  public void blockBreak(BlockEvent.BreakEvent event) {
    ItemStack tool = event.getPlayer().inventory.getCurrentItem();

    if(isTool(tool)) {
      NBTTagList list = TagUtil.getTraitsTagList(tool);
      for(int i = 0; i < list.tagCount(); i++) {
        ITrait trait = TinkerRegistry.getTrait(list.getStringTagAt(i));
        if(trait != null) {
          trait.beforeBlockBreak(tool, event);
        }
      }
    }
  }

  public void blockDropEvent(BlockEvent.HarvestDropsEvent event) {
    if(event.harvester == null) {
      return;
    }
    ItemStack tool = event.harvester.inventory.getCurrentItem();

    if(isTool(tool)) {
      NBTTagList list = TagUtil.getTraitsTagList(tool);
      for(int i = 0; i < list.tagCount(); i++) {
        ITrait trait = TinkerRegistry.getTrait(list.getStringTagAt(i));
        if(trait != null) {
          trait.blockHarvestDrops(tool, event);
        }
      }
    }
  }

  private boolean isTool(ItemStack stack) {
    return stack != null && stack.getItem() instanceof ToolCore;
  }
}
