package slimeknights.tconstruct.gadgets.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.shared.tileentity.TileTable;

public class ItemBlockRack extends ItemMultiTexture {

  public ItemBlockRack(Block block) {
    super(block, block, new String[]{"item", "drying"});
  }

  @Override
  public void addInformation(@Nonnull ItemStack stack, @Nonnull EntityPlayer playerIn, @Nonnull List<String> tooltip, boolean advanced) {
    if(stack.hasTagCompound()) {
      NBTTagCompound tag = stack.getTagCompound().getCompoundTag(TileTable.FEET_TAG);
      ItemStack legs = ItemStack.loadItemStackFromNBT(tag);
      if(legs != null && legs.getItem() != null) {
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
