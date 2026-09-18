package slimeknights.tconstruct.library.recipe.material;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;

import javax.annotation.Nullable;
import java.util.List;

/** Interface for recipes in the materials tab, showing items and fluids used to create materials. Common interface between {@link MaterialRecipe} and {@link slimeknights.tconstruct.library.recipe.casting.material.MaterialFluidRecipe} */
public interface IDisplayMaterialRecipe {
  /** Gets the ID of this recipe */
  ResourceLocation getRecipeId();

  /** Gets the material result for this recipe */
  MaterialVariant getMaterial();

  /* Items */

  /** Gets the list of items used to craft this recipe, for {@link MaterialRecipe} */
  default List<ItemStack> getDisplayItems() {
    return List.of();
  }

  /** Checks if a leftover is present without creating item stack copies. */
  default boolean hasLeftover() {
    return false;
  }

  /** Gets the item to use when too much of this material is spent  */
  default ItemStack getLeftover() {
    return ItemStack.EMPTY;
  }

  /** Gets the value of {@link #getDisplayItems()}. For the needed amount, use the stack size. */
  default int getValue() {
    return 0;
  }


  /* Fluids */

  /** Gets the list of fluids used to craft this recipe, for {@link slimeknights.tconstruct.library.recipe.casting.material.MaterialFluidRecipe} */
  default List<FluidStack> getFluids() {
    return List.of();
  }

  /** Gets the base material required to create this using {@link #getFluids()} in composite casting. */
  @Nullable
  default MaterialVariant getInput() {
    return null;
  }
}
