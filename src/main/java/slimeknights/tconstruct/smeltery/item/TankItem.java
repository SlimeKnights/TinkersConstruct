package slimeknights.tconstruct.smeltery.item;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import slimeknights.mantle.item.BlockTooltipItem;
import slimeknights.tconstruct.library.utils.Tags;
import slimeknights.tconstruct.smeltery.tileentity.TankTileEntity;

import javax.annotation.Nullable;
import java.util.List;

public class TankItem extends BlockTooltipItem {

  public TankItem(Block blockIn, Properties builder) {
    super(blockIn, builder);
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    if (stack.hasTag()) {
      FluidTank tank = getFluidTank(stack);
      if (tank.getFluidAmount() > 0) {
        tooltip.add(new TranslationTextComponent("block.tconstruct.tank.fluid", tank.getFluid().getDisplayName()).applyTextStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("block.tconstruct.tank.amount", tank.getFluidAmount()).applyTextStyle(TextFormatting.GRAY));
      }
    }
    else {
      super.addInformation(stack, worldIn, tooltip, flagIn);
    }
  }

  /**
   * Gets the tank for the given stack
   * @param stack  Tank stack
   * @return  Tank stored in the stack
   */
  public static FluidTank getFluidTank(ItemStack stack) {
    FluidTank tank = new FluidTank(TankTileEntity.CAPACITY);
    if (stack.hasTag()) {
      tank.readFromNBT(stack.getTag().getCompound(Tags.TANK));
    }
    return tank;
  }
}
