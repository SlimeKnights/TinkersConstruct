package slimeknights.tconstruct.gadgets.item;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.tools.common.item.ItemBlockTable;

public class ItemBlockRack extends ItemMultiTexture {

  public ItemBlockRack(Block block) {
    super(block, block, new String[] { "item", "drying" });
  }

  @Override
  public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, ITooltipFlag flagIn) {
    if(stack.hasTagCompound()) {
      ItemStack legs = ItemBlockTable.getLegStack(stack);
      if(!legs.isEmpty()) {
        tooltip.add(legs.getDisplayName());
      }

      if(stack.getTagCompound().hasKey("inventory")) {
        tooltip.add(Util.translate("tooltip.chest.has_items"));
      }
    }

    if(stack.getMetadata() == 0) {
      tooltip.add(Util.translate("tile.tconstruct.rack.item.tooltip"));
    }
    else if(stack.getMetadata() == 1) {
      tooltip.add(Util.translate("tile.tconstruct.rack.drying.tooltip"));
    }
  }
}
