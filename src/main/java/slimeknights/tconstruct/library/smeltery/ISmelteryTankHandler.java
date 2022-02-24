package slimeknights.tconstruct.library.smeltery;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public interface ISmelteryTankHandler {

  /**
   * Called when the liquids in the smeltery tank change.
   *
   * @param fluids  All fluids in the tank, new state. Same as tank.getFluids
   * @param changed The fluidstack that got changed or null if all got changed.
   */
  void onTankChanged(List<FluidStack> fluids, FluidStack changed);

  /**
   * Returns a copy of the SmelteryTank of the TE
   */
  SmelteryTank getTank();

  /**
   * Updates the fluids in the tank with data from the packet
   */
  @SideOnly(Side.CLIENT)
  void updateFluidsFromPacket(List<FluidStack> liquids);
}
