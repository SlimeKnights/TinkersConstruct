package slimeknights.tconstruct.library.recipe.tinkerstation;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Interface for tool modifications that should show in the crafting recipe category in JEI.
 * Generally implemented on a crafting recipe, but may be used for dynamic recipes.
 * If you wish to use {@link #isFiltered()} or {@link #showUnfocused()} in the crafting table, this must not be implemented on the recipe, and instead returned from {@link slimeknights.tconstruct.library.recipe.display.VanillaFilteredRecipe}.
 */
public interface IDisplayCraftingTinkering extends IDisplayToolModification, CraftingRecipe {
  /** Gets the ID for the crafting table tab. */
  @Override
  ResourceLocation getId();

  /** Gets the ID for the tinker station tab. */
  @Nullable
  @Override
  default ResourceLocation getRecipeId() {
    return getId();
  }

  /** Gets the tooltip for the information icon in the crafting table tab. By default, calls {@link #getTitle()} and {@link #getTooltip()}, but can be overridden to return separate strings. */
  default List<Component> getInformation() {
    return List.of(getTitle(), getTooltip());
  }


  /* Implement crafting table methods, probably no need to override. */

  @Override
  default boolean isSpecial() {
    // ensure standard JEI skips this recipe
    return true;
  }

  @Override
  default boolean canCraftInDimensions(int width, int height) {
    return getInputCount() + 1 < width * height;
  }


  /* Implement various methods that don't matter. Can always be overridden on an actual crafting recipe. */

  @Override
  default CraftingBookCategory category() {
    return CraftingBookCategory.MISC;
  }

  @Override
  default boolean matches(CraftingContainer container, Level pLevel) {
    return false;
  }

  @Override
  default ItemStack getResultItem(RegistryAccess access) {
    return ItemStack.EMPTY;
  }

  @Override
  default ItemStack assemble(CraftingContainer container, RegistryAccess access) {
    return ItemStack.EMPTY;
  }

  @Override
  default RecipeSerializer<?> getSerializer() {
    throw new UnsupportedOperationException();
  }
}
