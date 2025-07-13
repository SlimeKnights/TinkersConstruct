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

  /** Recipe cooling time */
  int getCoolingTime();
}
