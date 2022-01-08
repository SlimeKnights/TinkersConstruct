package slimeknights.tconstruct.smeltery.recipe;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.smeltery.tileentity.CastingTileEntity;

import javax.annotation.Nullable;

/**
 * Provides read only access to the input of a casting table. Prevents extra data from leaking
 */
@RequiredArgsConstructor
public class TileCastingWrapper implements ICastingInventory {
  private final CastingTileEntity tile;
  @Setter
  private FluidStack fluid;
  private boolean switchSlots = false;

  @Override
  public ItemStack getStack() {
    return tile.getStackInSlot(switchSlots ? CastingTileEntity.OUTPUT : CastingTileEntity.INPUT);
  }

  @Override
  public Fluid getFluid() {
    return fluid.getFluid();
  }

  @Nullable
  @Override
  public CompoundNBT getFluidTag() {
    return fluid.getTag();
  }

  /** Uses the input for input (default) */
  public void useInput() {
    switchSlots = false;
  }

  /** Uses the output for input (for multistep casting) */
  public void useOutput() {
    switchSlots = true;
  }
}
