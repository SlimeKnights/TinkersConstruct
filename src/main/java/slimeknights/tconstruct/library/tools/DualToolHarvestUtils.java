package slimeknights.tconstruct.library.tools;


import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import slimeknights.tconstruct.library.utils.ToolHelper;

public final class DualToolHarvestUtils {
  public static DualToolHarvestUtils INSTANCE = new DualToolHarvestUtils();

  private DualToolHarvestUtils() {}

  public static boolean shouldUseOffhand(EntityLivingBase player, BlockPos pos, ItemStack tool) {
    return shouldUseOffhand(player, player.getEntityWorld().getBlockState(pos), tool);
  }

  public static boolean shouldUseOffhand(EntityLivingBase player, IBlockState blockState, ItemStack tool) {
    ItemStack offhand = player.getHeldItemOffhand();

    return !tool.isEmpty()
           && !offhand.isEmpty()
           && blockState != null
           && tool.getItem() instanceof TinkerToolCore
           && !ToolHelper.isToolEffective2(tool, blockState)
           && ToolHelper.isToolEffective2(offhand, blockState);
  }

  public static ItemStack getItemstackToUse(EntityLivingBase player, IBlockState blockState) {
    ItemStack mainhand = player.getHeldItemMainhand();
    if(!mainhand.isEmpty() && shouldUseOffhand(player, blockState, mainhand)) {
      return player.getHeldItemOffhand();
    }
    return mainhand;
  }

  @SubscribeEvent
  public void offhandBreakSpeed(PlayerEvent.BreakSpeed event) {
    EntityPlayer player = event.getEntityPlayer();
    if(shouldUseOffhand(player, event.getState(), player.getHeldItemMainhand())) {
      ItemStack main = player.getHeldItemMainhand();
      ItemStack offhand = player.getHeldItemOffhand();

      // we use this instead of player.setItemStackToSlot because that one plays an equip sound ;_;
      player.inventory.mainInventory.set(player.inventory.currentItem, offhand);
      float speed = player.getDigSpeed(event.getState(), event.getPos());
      player.inventory.mainInventory.set(player.inventory.currentItem, main);

      event.setNewSpeed(speed);
    }
  }
}
