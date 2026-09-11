package slimeknights.tconstruct.library.recipe.casting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.List;

/** Interface for casting recipes that are displayable in JEI */
public interface IDisplayableCastingRecipe {
  /** Gets the ID of this recipe. If this is a generated display recipe, uses the parent recipe ID */
  @Nullable
  default ResourceLocation getRecipeId() {
    return null;
  }

  /** If true, the recipe has a cast item */
  boolean hasCast();

  /** Gets a list of cast items */
  List<ItemStack> getCastItems();

  /** If true, the cast is consumed */
  boolean isConsumed();

  /** Gets a list of fluid */
  List<FluidStack> getFluids();

  /** @deprecated use {@link #getOutputs()}. Okay to override. */
  @Deprecated
  ItemStack getOutput();

  /** Gets a list of recipe outputs. Size should either be 1 or match the size of {@link #getCastItems()}. */
  default List<ItemStack> getOutputs() {
    return List.of(getOutput());
  }

  /** If true, will attempt to link the output slot with the cast slot. */
  default boolean linkCastToOutput() {
    return true;
  }

  /** If true, will attempt to link the output slot with the fluids. */
  default boolean linkFluidsToOutput() {
    return false;
  }


  /* Cooling time *.

  /** If true, the cooling time is animated and will be computed using {@link #getCoolingTime(FluidStack)}. If false, it is static and {@link #getCastItems()} is used. */
  default boolean isCoolingTimeDynamic() {
    return false;
  }

  /** Recipe cooling time */
  int getCoolingTime();

  /** Gets the cooling time for the given fluid */
  default int getCoolingTime(FluidStack fluid) {
    return getCoolingTime();
  }
}
