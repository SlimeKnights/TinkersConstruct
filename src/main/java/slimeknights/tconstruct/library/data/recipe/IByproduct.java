package slimeknights.tconstruct.library.data.recipe;

import slimeknights.mantle.recipe.helper.FluidOutput;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer.OreRateType;

/** Interface for a byproduct for datagen, not required but makes parameters easier */
public interface IByproduct {
  /** Name of this byproduct */
  String getName();

  /** If true, this byproduct is not conditional, it will always be present if the data genning mod is loaded */
  boolean isAlwaysPresent();

  /** Gets the fluid of this byproduct */
  FluidOutput getFluid(float scale);

  /** Gets the rate for the given byproduct */
  OreRateType getOreRate();

  /** Gets the scaling unit for the byproduct for damagable melting recipes */
  default int getDamageUnit() {
    return 1;
  }
}
