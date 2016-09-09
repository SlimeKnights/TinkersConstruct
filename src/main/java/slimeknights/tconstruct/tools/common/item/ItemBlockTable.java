package slimeknights.tconstruct.tools.common.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

import javax.annotation.Nonnull;

import slimeknights.mantle.item.ItemBlockMeta;
import slimeknights.mantle.util.LocUtils;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.shared.tileentity.TileTable;

public class ItemBlockTable extends ItemBlockMeta {

  public ItemBlockTable(Block block) {
    super(block);
  }

  @Override
  public void addInformation(@Nonnull ItemStack stack, @Nonnull EntityPlayer playerIn, @Nonnull List<String> tooltip, boolean advanced) {
    super.addInformation(stack, playerIn, tooltip, advanced);
    if(!stack.hasTagCompound()) {
      return;
    }

    NBTTagCompound tag = stack.getTagCompound().getCompoundTag(TileTable.FEET_TAG);
    ItemStack legs = ItemStack.loadItemStackFromNBT(tag);
    if(legs != null && legs.getItem() != null) {
      tooltip.add(legs.getDisplayName());
    }

    if(stack.getTagCompound().hasKey("inventory")) {
      tooltip.addAll(LocUtils.getTooltips(Util.translate("tooltip.chest.has_items")));
    }
  }
}
