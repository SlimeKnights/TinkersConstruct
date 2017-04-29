package slimeknights.tconstruct.library.smeltery;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class BucketCastingRecipe implements ICastingRecipe {

  public static final BucketCastingRecipe INSTANCE = new BucketCastingRecipe();

  protected BucketCastingRecipe() {}

  @Override
  public ItemStack getResult(ItemStack cast, Fluid fluid) {
    ItemStack output = new ItemStack(Items.BUCKET);
    IFluidHandler fluidHandler = FluidUtil.getFluidHandler(output);
    assert fluidHandler != null;
    fluidHandler.fill(getFluid(cast, fluid), true);

    return output;
  }

  @Override
  public boolean matches(ItemStack cast, Fluid fluid) {
    return cast.getItem() == Items.BUCKET;
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
