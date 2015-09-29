package slimeknights.tconstruct.tools.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

import slimeknights.mantle.item.ItemBlockMeta;
import slimeknights.tconstruct.shared.tileentity.TileTable;

public class ItemBlockTable extends ItemBlockMeta {

  public ItemBlockTable(Block block) {
    super(block);
  }

  @Override
  public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced) {
    if(!stack.hasTagCompound()) {
      return;
    }

    NBTTagCompound tag = stack.getTagCompound().getCompoundTag(TileTable.FEET_TAG);
    ItemStack legs = ItemStack.loadItemStackFromNBT(tag);
    if(legs != null && legs.getItem() != null) {
      tooltip.add(legs.getDisplayName());
    }
  }
}
