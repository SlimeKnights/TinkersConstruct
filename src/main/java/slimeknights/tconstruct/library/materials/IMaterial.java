package slimeknights.tconstruct.library.materials;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.traits.ITrait;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IMaterial {

  /**
   * Fallback material. Used for operations where a material or specific aspects of a material are used,
   * but the given input is missing or does not match the requirements.
   * Think of this as "anti-crash" when trying to build invalid tools.
   *
   * The fallback material needs to have all part types associated with it.
   */
  Material UNKNOWN = new Material(Util.getResource("unknown"), null, false, ItemStack.EMPTY);

  /**
   * Convenience method. Default stats for all part types must exist, to be used when an invalid material with missing stats is used.
   */
  static <T extends IMaterialStats> T getDefaultStatsForType(MaterialStatType partType) {
    return (T) UNKNOWN.getStatsForType(partType).orElseThrow(() -> new IllegalStateException("Trying to get the fallback materials stats for a type that doesn't exist. You're either using something unregistered or some external influence messed things up, since that's impossible by design."));
  }

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
   * Shards are the leftovers when crafting parts, e.g. sticks for wood.
   * Usually the empty itemstack is used as default, signifying that the shard material item should be used.
   *
   * @return The itemstack to use for leftovers when crafting parts or empty itemstack if the shard item shall be used
   */
  ItemStack getShard();

  /**
   * Obtain the stats for the given part type.
   * Those are usually used to calculate the stats of a tool.
   * If an empty optional is returned it means this material is not fit to be used for this part type.
   *
   * @return Optional containing the stats, or empty optional if there are no stats for the given type.
   */
  <T extends IMaterialStats> Optional<T> getStatsForType(MaterialStatType partType);

  /**
   * Get the traits that shall be added to a tool if the given part type is used.
   *
   * @return List of traits to be used.
   */
  List<ITrait> getAllTraitsForStats(MaterialStatType partType);

  /**
   * All stats available with this material. Usually only used for display purposes.
   *
   * @return A collection of all stats registered with this material. Usually ordered, but not guaranteed.
   */
  Collection<IMaterialStats> getAllStats();

  /**
   * All traits possible with this material, regardless of part type.
   * Usually only used for display purposes.
   *
   * @return A collection of all traits registered with this material. Usually ordered, but not guaranteed.
   */
  Collection<ITrait> getAllTraits();
}
