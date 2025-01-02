package slimeknights.tconstruct.library.recipe.tinkerstation;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import slimeknights.mantle.recipe.ICommonRecipe;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.tools.nbt.LazyToolStack;

/**
 * Main interface for all recipes in the Tinker Station
 */
public interface ITinkerStationRecipe extends ICommonRecipe<ITinkerStationContainer> {
  /** Max number of tools in the tinker station slot, if the stack size is larger than this, only some of the tool is consumed */
  int DEFAULT_TOOL_STACK_SIZE = 16;

  /* Recipe data */

  @Override
  default RecipeType<?> getType() {
    return TinkerRecipeTypes.TINKER_STATION.get();
  }

  /** If true, this recipe matches the given inputs, ignoring current tool state */
  @Override
  boolean matches(ITinkerStationContainer inv, Level world);

  /**
   * Gets the recipe result, or an object containing an error message if the recipe matches but cannot be applied.
   * @return Validated result
   */
  RecipeResult<LazyToolStack> getValidatedResult(ITinkerStationContainer inv, RegistryAccess access);

  /** Gets the number to shrink the tool slot by, perfectly valid for this to be higher than the contained number of tools */
  default int shrinkToolSlotBy() {
    return DEFAULT_TOOL_STACK_SIZE;
  }

  /**
   * Updates the input stacks upon crafting this recipe
   * @param result  Result from {@link #assemble(ITinkerStationContainer, RegistryAccess)}. Generally should not be modified.
   * @param inv     Inventory instance to modify inputs
   * @param isServer  If true, this is on the serverside. Use to handle randomness, {@link IMutableTinkerStationContainer#giveItem(ItemStack)} should handle being called serverside only
   */
  default void updateInputs(LazyToolStack result, IMutableTinkerStationContainer inv, boolean isServer) {
    // shrink all stacks by 1
    for (int index = 0; index < inv.getInputCount(); index++) {
      inv.shrinkInput(index, 1);
    }
  }


  /* Deprecated */

  /** @deprecated use {@link #getValidatedResult(ITinkerStationContainer, RegistryAccess)}*/
  @Deprecated
  @Override
  default ItemStack getResultItem(RegistryAccess pRegistryAccess) {
    return ItemStack.EMPTY;
  }

  /** @deprecated use {@link #getValidatedResult(ITinkerStationContainer, RegistryAccess)}*/
  @Deprecated
  @Override
  default ItemStack assemble(ITinkerStationContainer inv, RegistryAccess access) {
    return getResultItem(access).copy();
  }

  /** @deprecated use {@link #updateInputs(LazyToolStack, IMutableTinkerStationContainer, boolean)} */
  @Override
  @Deprecated
  default NonNullList<ItemStack> getRemainingItems(ITinkerStationContainer inv) {
    return NonNullList.of(ItemStack.EMPTY);
  }
}
