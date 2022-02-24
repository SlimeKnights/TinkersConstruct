package slimeknights.tconstruct.smeltery.item;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTank;

import java.util.List;

import javax.annotation.Nullable;

import slimeknights.mantle.item.ItemBlockMeta;
import slimeknights.tconstruct.library.Util;

public class ItemTank extends ItemBlockMeta {

  public ItemTank(Block block) {
    super(block);
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    super.addInformation(stack, worldIn, tooltip, flagIn);

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
