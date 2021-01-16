package slimeknights.tconstruct.smeltery.tileentity.tank;

import net.minecraft.fluid.Fluid;
import net.minecraftforge.client.model.data.ModelProperty;

/**
 * Interface for blocks to be notified when the smeltery has a new bottommost fluid
 */
public interface IDisplayFluidListener {
  /** Property for fluid models */
  ModelProperty<Fluid> PROPERTY = new ModelProperty<>();

  /**
   * Called when the display fluid changes
   * @param fluid New display fluid
   */
  void notifyDisplayFluidUpdated(Fluid fluid);
}
