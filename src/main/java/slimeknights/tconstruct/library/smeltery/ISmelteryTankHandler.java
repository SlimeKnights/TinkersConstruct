package slimeknights.tconstruct.library.smeltery;

import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public interface ISmelteryTankHandler {

  /**
   * Called when the liquids in the smeltery tank change.
   *
   * @param fluids  All fluids in the tank, new state. Same as tank.getFluids
   * @param changed The fluidstack that got changed or null if all got changed.
   */
  void onTankChanged(List<FluidStack> fluids, FluidStack changed);
}
