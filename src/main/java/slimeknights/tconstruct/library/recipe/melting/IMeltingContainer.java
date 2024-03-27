package slimeknights.tconstruct.library.recipe.melting;

import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.container.ISingleStackContainer;

/** Interface for melting inventories */
public interface IMeltingContainer extends ISingleStackContainer {
  /**
   * Gets the logic to boost an ore with the ore rate
   * @return  Nuggets per ore
   */
  IOreRate getOreRate();

  /** Ore rate logic in a melting container */
  interface IOreRate {
    /** Boosts the given integer by the rate */
    int applyOreBoost(OreRateType rate, int amount);

    /** Boosts the given fluid stack by the rate */
    default FluidStack applyOreBoost(OreRateType rate, FluidStack fluid) {
      if (rate == OreRateType.DEFAULT || rate == OreRateType.NONE) {
        return fluid;
      }
      return new FluidStack(fluid, applyOreBoost(rate, fluid.getAmount()));
    }
  }

  /** Ore rate options */
  enum OreRateType {
    /** No boost */
    NONE,
    /** Metal boost, works with divisions of 9 */
    METAL,
    /** Gem boost, works with divisions of 4 */
    GEM,
    /** Default value, used in place of null to indicate the value should be fetched from another source. If o default exits acts as NONE. */
    DEFAULT;

    /** Returns the passed argument if this is default, else return self */
    public OreRateType orElse(OreRateType type) {
      if (this == DEFAULT) {
        return type;
      }
      return this;
    }
  }
}
