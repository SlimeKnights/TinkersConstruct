package slimeknights.tconstruct.library.recipe.melting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer.OreRateType;

import javax.annotation.Nullable;
import java.util.List;

/** Interface for melting recipes that are displayable in JEI */
public interface IDisplayableMeltingRecipe {
  /** Gets the ID of this recipe, usually just a call to {@link net.minecraft.world.item.crafting.Recipe#getId} */
  ResourceLocation getRecipeId();

  /** List of input items to melt. Will be focus linked to  */
  List<ItemStack> getInputs();

  /**
   * List of fluid results for the melter and smeltery.
   * If the size is larger than 1, should match to outputs for each separate {@link #getInputs()}. Generally should just animate output size.
   */
  List<FluidStack> getOutputs();

  /**
   * Gets a list of fluid results for the foundry.
   * Outer list size is the number of outputs. Inner list size is the animated size for each output.
   * If the inner list size is larger than 1, should match to outputs for each separate {@link #getInputs()}. Generally should just animate output size.
   * In ore recipes (e.g. {@link OreMeltingRecipe}, the foundry ore rates are applied directly. TODO 1.21: don't apply them directly for the sake of the sublimery.
   */
  default List<List<FluidStack>> getOutputWithByproducts() {
    return List.of(getOutputs());
  }

  /** Gets the method of ore boosting for this recipe. If null, recipe is not boosted by ore rates. */
  @Nullable
  default OreRateType getOreType() {
    // TODO 1.21: change default to OreRateType.NONE.
    return null;
  }

  /** Gets the minimum temperature needed to perform this recipe. */
  int getTemperature();

  /** Gets the time it takes to melt this recipe. */
  int getTime();
}
