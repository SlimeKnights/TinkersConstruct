package slimeknights.tconstruct.smeltery.tileentity.module.alloying;

import lombok.RequiredArgsConstructor;
import net.minecraft.world.World;
import slimeknights.mantle.tileentity.MantleTileEntity;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.alloying.AlloyRecipe;
import slimeknights.tconstruct.library.recipe.alloying.IMutableAlloyTank;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

/** Alloying module that supports only a single output */
@RequiredArgsConstructor
public class SingleAlloyingModule implements IAlloyingModule {
  private final MantleTileEntity parent;
  private final IMutableAlloyTank alloyTank;
  private AlloyRecipe lastRecipe;

  /** Gets a nonnull world instance from the parent */
  private World getWorld() {
    return Objects.requireNonNull(parent.getWorld(), "Parent tile entity has null world");
  }

  /** Finds the recipe to perform */
  @Nullable
  private AlloyRecipe findRecipe() {
    World world = getWorld();
    if (lastRecipe != null && lastRecipe.canPerform(alloyTank)) {
      return lastRecipe;
    }
    // fetch the first recipe that matches the inputs and fits in the tank
    // means if for some reason two recipes both are vaiud, the tank contents can be used to choose
    Optional<AlloyRecipe> recipe = world.getRecipeManager()
                                        .getRecipes(RecipeTypes.ALLOYING)
                                        .values().stream()
                                        .filter(r -> r instanceof AlloyRecipe)
                                        .map(r -> (AlloyRecipe) r)
                                        .filter(r -> alloyTank.canFit(r.getOutput(), 0) && r.canPerform(alloyTank))
                                        .findAny();
    // if found, cache and return
    if (recipe.isPresent()) {
      lastRecipe = recipe.get();
      return lastRecipe;
    } else {
      return null;
    }
  }

  @Override
  public boolean canAlloy() {
    return findRecipe() != null;
  }

  @Override
  public void doAlloy() {
    AlloyRecipe recipe = findRecipe();
    if (recipe != null) {
      recipe.performRecipe(alloyTank);
    }
  }
}
