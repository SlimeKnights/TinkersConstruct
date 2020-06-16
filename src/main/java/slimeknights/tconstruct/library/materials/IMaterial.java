package slimeknights.tconstruct.library.materials;

import net.minecraft.fluid.Fluid;
import slimeknights.tconstruct.library.Util;

public interface IMaterial {

  /**
   * Fallback material. Used for operations where a material or specific aspects of a material are used,
   * but the given input is missing or does not match the requirements.
   * Think of this as "anti-crash" when trying to build invalid tools.
   * <p>
   * The fallback material needs to have all part types associated with it.
   */
  Material UNKNOWN = new Material(Util.getResource("unknown"), null, false);

  /**
   * Used to identify the material in NBT and other constructs.
   * Basically everywhere where the material has to be referenced and persisted.
   */
  MaterialId getIdentifier();

  /**
   * If the material can be crafted into items in the part builder.
   *
   * @return Return false if the material can only be cast or is not craftable at all.
   */
  boolean isCraftable();

  /**
   * The fluid associated with this material, if not Fluids.EMPTY.
   * Prerequisite for parts to be cast using the casting table and a cast.
   * Just to make this completely clear: This is the indicator if a material is castable.
   *
   * @return The associated fluid or Fluids.EMPTY if material is not castable
   */
  // todo: check if we should replace this with a FluidStack or IFluidState. Probably best done after we know usage
  Fluid getFluid();

  /**
   * Gets the translation key for this material
   * @return the translation key
   */
  String getTranslationKey();

  /**
   * Gets the encoded text color for this material
   * @return the encoded text color
   */
  String getEncodedTextColor();

  /**
   * Gets the text color for this material
   * @return the text color
   */
  String getTextColor();
}
