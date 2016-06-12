package slimeknights.tconstruct.library.fluid;

/**
 * Allows a tile entity containing an instance of FluidTankAnimated to perform action whenever the tank's contents change
 */
public interface IFluidTankUpdater {

  /**
   * Called when the contained fluid tank changes its contents
   * <p>
   * This does not contain the actual changes, the class containing this needs to manually store the data it wants to check
   */
  void onTankContentsChanged();
}
