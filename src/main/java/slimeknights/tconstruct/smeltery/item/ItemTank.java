package slimeknights.tconstruct.smeltery.item;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import java.util.List;

import javax.annotation.Nullable;

import slimeknights.mantle.item.ItemBlockMeta;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.smeltery.tileentity.TileTank;

public class ItemTank extends ItemBlockMeta {

  public ItemTank(Block block) {
    super(block);
    this.addPropertyOverride(new ResourceLocation("amount"), TankCapacityGetter.INSTANCE);
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

  public enum TankCapacityGetter implements IItemPropertyGetter {
    INSTANCE;

    @Override
    public float apply(ItemStack stack, World worldIn, EntityLivingBase entityIn) {
      if(!stack.hasTagCompound()) {
        return 0;
      }
      FluidStack fluid = FluidStack.loadFluidStackFromNBT(stack.getTagCompound());
      if (fluid != null && fluid.amount > 0) {
        return (float)fluid.amount / TileTank.CAPACITY;
      }
      return 0;
    }
  }
}
