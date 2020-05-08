package slimeknights.tconstruct.smeltery.item;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import slimeknights.tconstruct.smeltery.tileentity.TankTileEntity;

import javax.annotation.Nullable;
import java.util.List;

public class TankItem extends BlockItem {

  public TankItem(Block blockIn, Properties builder) {
    super(blockIn, builder);
    this.addPropertyOverride(new ResourceLocation("tconstruct","amount"), TankCapacityGetter.INSTANCE);
  }

  // TODO: Doesn't seem to work
  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    super.addInformation(stack, worldIn, tooltip, flagIn);
    if (stack.hasTag()) {
      FluidTank tank = new FluidTank(TankTileEntity.CAPACITY);
      tank.readFromNBT(stack.getTag());
      if (tank.getFluidAmount() > 0) {
        tooltip.add(tank.getFluid().getDisplayName());
      }
    }
  }

  // TODO: Doesn't seem to work
  public enum TankCapacityGetter implements IItemPropertyGetter {
    INSTANCE;

    @OnlyIn(Dist.CLIENT)
    @Override
    public float call(ItemStack stack, @Nullable World worldIn, @Nullable LivingEntity entity) {
      if (!stack.hasTag()) {
        return 0;
      }
      FluidStack fluid = FluidStack.loadFluidStackFromNBT(stack.getTag());
      if (fluid != null && fluid.getAmount() > 0) {
        return (float)fluid.getAmount() / TankTileEntity.CAPACITY;
      }
      return 0;
    }
  }
}
