package slimeknights.tconstruct.library.data.recipe;

import net.minecraft.world.level.material.Fluid;

/** Interface for a byproduct for datagen, not required but makes parameters easier */
public interface IByproduct {
  /** Name of this byproduct */
  String getName();

  /** If true, this byproduct is not conditional, it will always be present if the data genning mod is loaded */
  boolean isAlwaysPresent();

  /** Gets the fluid of this byproduct */
  Fluid getFluid();

  /** Gets the number of nuggets produced in this byproduct */
  int getNuggets();
}
