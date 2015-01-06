package tconstruct.library.tools;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import tconstruct.library.tinkering.Material;
import tconstruct.library.tinkering.TinkersItem;

public class TinkerHarvestTool extends TinkersTool {
  @Override
  public float getDigSpeed(ItemStack itemstack, IBlockState state) {
    return ToolHelper.calcDigSpeed(itemstack, state);
  }

  @Override
  protected NBTTagCompound buildTag(Material[] materials) {
    return null;
  }

  @Override
  public String getItemType() {
    return "harvest";
  }

  @Override
  public String[] getInformation() {
    // todo
    return new String[0];
  }
}
