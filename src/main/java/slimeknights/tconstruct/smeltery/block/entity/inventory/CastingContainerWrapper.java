package slimeknights.tconstruct.smeltery.block.entity.inventory;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.recipe.casting.ICastingContainer;
import slimeknights.tconstruct.smeltery.block.entity.CastingBlockEntity;

import javax.annotation.Nullable;

/**
 * Provides read only access to the input of a casting table. Prevents extra data from leaking
 */
@RequiredArgsConstructor
public class CastingContainerWrapper implements ICastingContainer {
  private final CastingBlockEntity tile;
  @Setter
  private FluidStack fluid;
  private boolean switchSlots = false;

  @Override
  public ItemStack getStack() {
    ItemStack stack = tile.getItem(switchSlots ? CastingBlockEntity.OUTPUT : CastingBlockEntity.INPUT);
    if (stack.is(tile.getEmptyCastTag())) {
      return ItemStack.EMPTY;
    }
    return stack;
  }

  @Override
  public Fluid getFluid() {
    return fluid.getFluid();
  }

  @Nullable
  @Override
  public CompoundTag getFluidTag() {
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
