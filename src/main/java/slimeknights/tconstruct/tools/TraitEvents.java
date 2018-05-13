package slimeknights.tconstruct.tools;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

import slimeknights.tconstruct.library.events.TinkerToolEvent;
import slimeknights.tconstruct.library.tools.DualToolHarvestUtils;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class TraitEvents {

  @SubscribeEvent
  public void mineSpeed(PlayerEvent.BreakSpeed event) {
    ItemStack tool = event.getEntityPlayer().inventory.getCurrentItem();

    if(isTool(tool) && !ToolHelper.isBroken(tool)) {
      TinkerUtil.getTraitsOrdered(tool).forEach(trait -> trait.miningSpeed(tool, event));
    }
  }

  @SubscribeEvent
  public void blockBreak(BlockEvent.BreakEvent event) {
    ItemStack tool = event.getPlayer().inventory.getCurrentItem();

    if(isTool(tool) && !ToolHelper.isBroken(tool)) {
      TinkerUtil.getTraitsOrdered(tool).forEach(trait -> trait.beforeBlockBreak(tool, event));
    }
  }

  @SubscribeEvent
  public void blockDropEvent(BlockEvent.HarvestDropsEvent event) {
    if(event.getHarvester() == null) {
      return;
    }
    ItemStack tool = DualToolHarvestUtils.getItemstackToUse(event.getHarvester(), event.getState());

    if(isTool(tool) && !ToolHelper.isBroken(tool)) {
      TinkerUtil.getTraitsOrdered(tool).forEach(trait -> trait.blockHarvestDrops(tool, event));
    }
  }

  /**
   * Handles the onBlock or the onPlayerHurt trait callback. Note that only one of the two is called!
   */
  @SubscribeEvent
  public void playerBlockOrHurtEvent(LivingHurtEvent event) {
    boolean isPlayerGettingDamaged = event.getEntityLiving() instanceof EntityPlayer;
    boolean isClient = event.getEntityLiving().getEntityWorld().isRemote;
    boolean isReflectedDamage = event.getSource() instanceof EntityDamageSource && ((EntityDamageSource) event.getSource()).getIsThornsDamage();

    if(!isPlayerGettingDamaged || isClient || isReflectedDamage) {
      return;
    }
    final EntityPlayer player = (EntityPlayer) event.getEntityLiving();
    Entity attacker = event.getSource().getTrueSource();

    List<ItemStack> heldTools = new ArrayList<>();
    for(ItemStack tool : event.getEntity().getHeldEquipment()) {
      if(isTool(tool) && !ToolHelper.isBroken(tool)) {
        heldTools.add(tool);
      }
    }

    // first handle block
    if(player.isActiveItemStackBlocking()) {
      // we allow block traits to affect both main and offhand
      for(ItemStack tool : heldTools) {
        if(!event.isCanceled()) {
          TinkerUtil.getTraitsOrdered(tool).forEach(trait -> trait.onBlock(tool, player, event));
        }
      }
    }
    // else handle living hurt
    else if(attacker instanceof EntityLivingBase && !attacker.isDead) {
      // we allow block traits to affect both main and offhand
      for(ItemStack tool : heldTools) {
        if(!event.isCanceled()) {
          TinkerUtil.getTraitsOrdered(tool).forEach(trait -> trait.onPlayerHurt(tool, player, (EntityLivingBase) attacker, event));
        }
      }
    }
  }

  @SubscribeEvent
  public void onRepair(TinkerToolEvent.OnRepair event) {
    ItemStack tool = event.itemStack;

    TinkerUtil.getTraitsOrdered(tool).forEach(trait -> trait.onRepair(tool, event.amount));
  }

  private boolean isTool(ItemStack stack) {
    return stack != null && stack.getItem() instanceof ToolCore;
  }
}
