package slimeknights.tconstruct.library.recipe.casting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.recipe.RecipeSlot;

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


  /* Cooling time */

  /** If true, the cooling time is animated and will be computed using {@link #getCoolingTime(FluidStack)}. If false, it is static and {@link #getCastItems()} is used. */
  default boolean isCoolingTimeDynamic() {
    return false;
  }

  /** Recipe cooling time */
  int getCoolingTime();

  /** Gets the cooling time for the given fluid. Only called if {@link #isCoolingTimeDynamic()} is true. */
  default int getCoolingTime(FluidStack fluid) {
    return getCoolingTime();
  }


  /* Dynamic display */

  /**
   * Gets the cast based on the current focus.
   * @param focus        Focus stack
   * @param focusOutput  If true, the focus is an output. If false, it is an input.
   * @return focus sensitive cast items.
   */
  default List<ItemStack> getCastItems(ItemStack focus, boolean focusOutput) {
    return getCastItems();
  }

  /**
   * Gets the fluids based on the current focus.
   * @param focus        Focus stack
   * @param focusOutput  If true, the focus is an output. If false, it is an input.
   * @return focus sensitive fluids.
   */
  default List<FluidStack> getFluids(ItemStack focus, boolean focusOutput) {
    return getFluids();
  }

  /**
   * Gets the outputs based on the current focus.
   * @param focus        Focus stack
   * @param focusOutput  If true, the focus is an output. If false, it is an input.
   * @return focus sensitive outputs.
   */
  default List<ItemStack> getOutputs(ItemStack focus, boolean focusOutput) {
    return getOutputs();
  }

  /** If true, the displayed ingredients can be dynamically updated using {@link #onDisplayUpdate(RecipeSlot, RecipeSlot, RecipeSlot)} */
  default boolean isSlotsDynamic() {
    return false;
  }

  /**
   * Called when the display updates in JEI to allow a recipe to dynamically change the displayed values.
   * Only called if {@link #isSlotsDynamic()} is true.
   * @param cast         Cast slot to get or set displayed cast contents. Will be {@link RecipeSlot#EMPTY_ITEM} if {@link #hasCast()} is false (meaning you cannot change the cast).
   * @param fluid        Fluid slot to get or set displayed fluid contents.
   * @param output       Result slot to get or set displayed output contents.
   */
  default void onDisplayUpdate(RecipeSlot<ItemStack> cast, RecipeSlot<FluidStack> fluid, RecipeSlot<ItemStack> output) {}
}
