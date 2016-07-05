package slimeknights.tconstruct.tools;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.traits.ITrait;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.library.events.TinkerToolEvent;

public class TraitEvents {

  @SubscribeEvent
  public void mineSpeed(PlayerEvent.BreakSpeed event) {
    ItemStack tool = event.getEntityPlayer().inventory.getCurrentItem();

    if(isTool(tool) && !ToolHelper.isBroken(tool)) {
      NBTTagList list = TagUtil.getTraitsTagList(tool);
      for(int i = 0; i < list.tagCount(); i++) {
        ITrait trait = TinkerRegistry.getTrait(list.getStringTagAt(i));
        if(trait != null) {
          trait.miningSpeed(tool, event);
        }
      }
    }
  }

  @SubscribeEvent
  public void blockBreak(BlockEvent.BreakEvent event) {
    ItemStack tool = event.getPlayer().inventory.getCurrentItem();

    if(isTool(tool) && !ToolHelper.isBroken(tool)) {
      NBTTagList list = TagUtil.getTraitsTagList(tool);
      for(int i = 0; i < list.tagCount(); i++) {
        ITrait trait = TinkerRegistry.getTrait(list.getStringTagAt(i));
        if(trait != null) {
          trait.beforeBlockBreak(tool, event);
        }
      }
    }
  }

  @SubscribeEvent
  public void blockDropEvent(BlockEvent.HarvestDropsEvent event) {
    if(event.getHarvester() == null) {
      return;
    }
    ItemStack tool = event.getHarvester().inventory.getCurrentItem();

    if(isTool(tool) && !ToolHelper.isBroken(tool)) {
      NBTTagList list = TagUtil.getTraitsTagList(tool);
      for(int i = 0; i < list.tagCount(); i++) {
        ITrait trait = TinkerRegistry.getTrait(list.getStringTagAt(i));
        if(trait != null) {
          trait.blockHarvestDrops(tool, event);
        }
      }
    }
  }

  @SubscribeEvent
  public void playerBlockEvent(LivingHurtEvent event) {
    if(event.getEntity() == null || !(event.getEntity() instanceof EntityPlayer) || !((EntityPlayer) event.getEntity()).isActiveItemStackBlocking()) {
      return;
    }
    // item in use has to be current item, otherwise MC stops using it
    ItemStack tool = ((EntityPlayer) event.getEntity()).inventory.getCurrentItem();

    if(isTool(tool) && !ToolHelper.isBroken(tool)) {
      NBTTagList list = TagUtil.getTraitsTagList(tool);
      for(int i = 0; i < list.tagCount(); i++) {
        ITrait trait = TinkerRegistry.getTrait(list.getStringTagAt(i));
        if(trait != null) {
          trait.onBlock(tool, (EntityPlayer) event.getEntity(), event);
        }
      }
    }
  }

  @SubscribeEvent
  public void onRepair(TinkerToolEvent.OnRepair event) {
    ItemStack tool = event.itemStack;

    NBTTagList list = TagUtil.getTraitsTagList(tool);
    for(int i = 0; i < list.tagCount(); i++) {
      ITrait trait = TinkerRegistry.getTrait(list.getStringTagAt(i));
      if(trait != null) {
        trait.onRepair(tool, event.amount);
      }
    }
  }

  private boolean isTool(ItemStack stack) {
    return stack != null && stack.getItem() instanceof ToolCore;
  }
}
