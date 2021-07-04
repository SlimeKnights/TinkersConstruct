package slimeknights.tconstruct.common.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.data.recipe.IRecipeHelper;

import java.util.function.Consumer;

/**
 * Shared logic for each module's recipe provider
 */
public abstract class BaseRecipeProvider extends RecipeProvider implements IConditionBuilder, IRecipeHelper {
  public BaseRecipeProvider(DataGenerator generator) {
    super(generator);
  }

  @Override
  protected abstract void registerRecipes(Consumer<IFinishedRecipe> consumer);

  @Override
  public abstract String getName();

  @Override
  public String getModId() {
    return TConstruct.MOD_ID;
  }
}
