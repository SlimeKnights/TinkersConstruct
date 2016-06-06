package slimeknights.tconstruct.smeltery.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidTank;

import java.util.List;

import javax.annotation.Nonnull;

import slimeknights.mantle.item.ItemBlockMeta;
import slimeknights.tconstruct.library.Util;

public class ItemTank extends ItemBlockMeta {

  public ItemTank(Block block) {
    super(block);
  }

  @Override
  public void addInformation(@Nonnull ItemStack stack, @Nonnull EntityPlayer playerIn, @Nonnull List<String> tooltip, boolean advanced) {
    super.addInformation(stack, playerIn, tooltip, advanced);

    if(stack.hasTagCompound()) {
      FluidTank tank = new FluidTank(0);
      tank.readFromNBT(stack.getTagCompound());
      if(tank.getFluidAmount() > 0) {
        tooltip.add(Util.translateFormatted("tooltip.tank.fluid", tank.getFluid().getLocalizedName()));
        tooltip.add(Util.translateFormatted("tooltip.tank.amount", tank.getFluid().amount));
      }
    }
  }
}
