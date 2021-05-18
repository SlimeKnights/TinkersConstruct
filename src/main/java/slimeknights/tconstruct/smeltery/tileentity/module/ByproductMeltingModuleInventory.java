package slimeknights.tconstruct.smeltery.tileentity.module;

import net.minecraftforge.fluids.capability.IFluidHandler;
import slimeknights.mantle.tileentity.MantleTileEntity;
import slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe;

import java.util.function.IntSupplier;

public class ByproductMeltingModuleInventory extends MeltingModuleInventory {
  public ByproductMeltingModuleInventory(MantleTileEntity parent, IFluidHandler fluidHandler, IntSupplier nuggetsPerOre, int size) {
    super(parent, fluidHandler, nuggetsPerOre, size);
  }

  public ByproductMeltingModuleInventory(MantleTileEntity parent, IFluidHandler fluidHandler, IntSupplier nuggetsPerOre) {
    super(parent, fluidHandler, nuggetsPerOre);
  }

  @Override
  protected boolean tryFillTank(int index, IMeltingRecipe recipe) {
    if (super.tryFillTank(index, recipe)) {
      recipe.handleByproducts(getModule(index), fluidHandler);
      return true;
    }
    return false;
  }
}
