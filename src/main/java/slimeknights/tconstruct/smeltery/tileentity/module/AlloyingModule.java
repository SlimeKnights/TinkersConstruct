package slimeknights.tconstruct.smeltery.tileentity.module;

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
import java.util.function.Predicate;

/** Module to handle running alloys via a fluid handler */
public class AlloyingModule {
  private final MantleTileEntity parent;
  private final IAlloyTank alloyTank;

  /** List of recipes that succeeded last time in {@link #doAlloy()}, only these will be used for the next iteration */
  @Nullable
  private List<AlloyRecipe> lastRecipes;

  /** Predicates for common behaviors */
  private final Predicate<AlloyRecipe> canPerform, performRecipe;

  public AlloyingModule(MantleTileEntity parent, IFluidHandler fluidHandler, IAlloyTank alloyTank) {
    this.parent = parent;
    this.alloyTank = alloyTank;
    this.canPerform = recipe -> recipe.canPerform(alloyTank);
    this.performRecipe = recipe -> {
      recipe.performRecipe(alloyTank, fluidHandler);
      return false;
    };
  }

  /** Gets a nonnull world instance from the parent */
  private World getWorld() {
    return Objects.requireNonNull(parent.getWorld(), "Parent tile entity has null world");
  }

  /**
   * Gets a list of recipes that currently match the tank
   * @return  List of recipes that match the tank
   */
  private List<AlloyRecipe> getRecipes() {
    if (lastRecipes == null) {
      lastRecipes = getWorld().getRecipeManager().getRecipes(RecipeTypes.ALLOYING, alloyTank, getWorld());
    }
    return lastRecipes;
  }

  /**
   * Runs all the recipes, removing any that no longer match
   * @param predicate  Logic to run for recipes, return true to stop looping
   * @return  True if any recipe returned true
   */
  private boolean iterateRecipes(Predicate<AlloyRecipe> predicate) {
    List<AlloyRecipe> recipes = getRecipes();
    if (recipes.isEmpty()) {
      return false;
    }

    World world = getWorld();
    Iterator<AlloyRecipe> iterator = recipes.iterator();
    while (iterator.hasNext()) {
      // if the recipe no longer matches, remove
      // if it matches, run their function and stop if requested
      AlloyRecipe recipe = iterator.next();
      if (recipe.matches(alloyTank, world)) {
        if (predicate.test(recipe)) {
          return true;
        }
      } else {
        iterator.remove();
      }
    }
    return false;
  }

  /** Checks if any alloy recipe can be used */
  public boolean canAlloy() {
    return iterateRecipes(canPerform);
  }

  /**
   * Actually performs alloys for the tank
   */
  public void doAlloy() {
    List<AlloyRecipe> recipes = getRecipes();
    if (recipes.isEmpty()) return;
    // shuffle the recipe list, in case we have mutually exclusive recipes it makes them less dependant on name order
    Collections.shuffle(recipes);
    // recipes is the same as lastRecipes at this time, so the iterator will use the shuffled list
    iterateRecipes(performRecipe);
  }

  /**
   * Clears the list of cached recipes, called when the tank gains a new fluid
   */
  public void clearCachedRecipes() {
    lastRecipes = null;
  }
}
