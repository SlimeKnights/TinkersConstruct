package slimeknights.tconstruct.smeltery.tileentity.tank;

import net.minecraftforge.common.extensions.IForgeTileEntity;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

// TODO: reassess
public interface ISmelteryTankHandler extends IForgeTileEntity {
  /**
   * Updates the fluids in the tank with data from the packet, should only be called client side
   */
  void updateFluidsFromPacket(List<FluidStack> fluids);

  /**
   * Gets the smeltery tank
   * @return  Tank instance
   */
  SmelteryTank getTank();
}
