package slimeknights.tconstruct.library.smeltery;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
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
   * @return Smeltery Tank if active, null if the smeltery is inactive
   */
  @Nullable
  SmelteryTank getTank();
  /**
   * Updates the fluids in the tank with data from the packet
   */
  @OnlyIn(Dist.CLIENT)
  void updateFluidsFromPacket(List<FluidStack> liquids);
}
