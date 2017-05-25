package slimeknights.tconstruct.library.smeltery;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class BucketCastingRecipe implements ICastingRecipe {

  private Item bucket;

  /**
   * Casting recipe to fill a fluid container
   * @param bucket  Fluid container item, must have a fluid handler capability
   */
  public BucketCastingRecipe(Item bucket) {
    this.bucket = bucket;
  }

  @Override
  public ItemStack getResult(ItemStack cast, Fluid fluid) {
    ItemStack output = new ItemStack(bucket);
    IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(output);
    assert fluidHandler != null;
    fluidHandler.fill(getFluid(cast, fluid), true);

    return fluidHandler.getContainer();
  }

  @Override
  public boolean matches(ItemStack cast, Fluid fluid) {
    return cast.getItem() == bucket;
  }

  @Override
  public boolean switchOutputs() {
    return false;
  }

  @Override
  public boolean consumesCast() {
    return true;
  }

  @Override
  public int getTime() {
    return 5;
  }

  @Override
  public int getFluidAmount() {
    return Fluid.BUCKET_VOLUME;
  }
}
