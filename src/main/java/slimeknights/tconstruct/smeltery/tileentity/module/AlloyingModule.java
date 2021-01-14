package slimeknights.tconstruct.smeltery.tileentity.module;

import lombok.RequiredArgsConstructor;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.IFluidHandler;
import slimeknights.mantle.tileentity.MantleTileEntity;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.alloying.AlloyRecipe;
import slimeknights.tconstruct.library.recipe.alloying.IAlloyTank;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/** Module to handle running alloys via a fluid handler */
@RequiredArgsConstructor
public class AlloyingModule {
  private final MantleTileEntity parent;
  private final IFluidHandler fluidHandler;
  private final IAlloyTank alloyTank;

  /** Cache of the last recipe that matched. Only used in {@link #canAlloy()} as we have to run all recipes in {@link #doAlloy()} */
  @Nullable
  private AlloyRecipe lastMatch;
  /** List of recipes that succeeded last time in {@link #doAlloy()}, only these will be used for the next iteration */
  @Nullable
  private List<AlloyRecipe> lastRecipes;

  /** Gets a nonnull world instance from the parent */
  private World getWorld() {
    return Objects.requireNonNull(parent.getWorld(), "Parent tile entity has null world");
  }

  /** Checks if any alloy recipe can be used */
  public boolean canAlloy() {
    World world = getWorld();
    if (lastMatch != null && lastMatch.matches(alloyTank, world)) {
      return true;
    }

    // find a new recipe
    return world.getRecipeManager().getRecipe(RecipeTypes.ALLOYING, alloyTank, world).filter(recipe -> {
      lastMatch = recipe;
      return true;
    }).isPresent();
  }

  /**
   * Clears the list of cached recipes, called when the tank gains a new fluid
   */
  public void clearCachedRecipes() {
    lastRecipes = null;
  }

  /**
   * Actually performs alloys for the tank
   */
  public void doAlloy() {
    World world = getWorld();

    // if no cached recipes, find a new list of recipes for this set of fluids
    if (lastRecipes == null) {
      lastRecipes = world.getRecipeManager().getRecipes(RecipeTypes.ALLOYING, alloyTank, world);
    }

    // no recipes? done
    if (lastRecipes.isEmpty()) {
      return;
    }

    // shuffle the recipe list, in case we have mutually exclusive recipes it makes them less dependant on name order
    Collections.shuffle(lastRecipes);

    Iterator<AlloyRecipe> recipeIterator = lastRecipes.iterator();
    AlloyRecipe recipe;
    while (recipeIterator.hasNext()) {
      recipe = recipeIterator.next();
      if (recipe.matches(alloyTank, world)) {
        recipe.handleRecipe(alloyTank, fluidHandler);
        // store this recipe as the last successful recipe to speed up canAlloy()
        lastMatch = recipe;
      } else {
        // remove the recipe if it no longer matches, means we have fewer fluids than we did when this list was cached
        recipeIterator.remove();
      }
    }
  }
}
