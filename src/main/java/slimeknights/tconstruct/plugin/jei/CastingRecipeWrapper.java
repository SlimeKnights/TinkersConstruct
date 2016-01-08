package slimeknights.tconstruct.plugin.jei;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

import javax.annotation.Nonnull;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;

public class CastingRecipeWrapper extends BlankRecipeWrapper {

  protected final List<ItemStack> cast;
  protected final List<FluidStack> inputFluid;
  protected List<ItemStack> output;

  public final IDrawable castingBlock;

  private final CastingRecipe recipe;

  // do not call with the oredict casting recipes
  public CastingRecipeWrapper(List<ItemStack> casts, CastingRecipe recipe, IDrawable castingBlock) {
    this.cast = casts;
    this.recipe = recipe;
    this.inputFluid = ImmutableList.of(recipe.fluid);
    this.output = ImmutableList.of(recipe.getResult());
    this.castingBlock = castingBlock;
  }

  public CastingRecipeWrapper(CastingRecipe recipe, IDrawable castingBlock) {
    // cast is not required
    if(recipe.cast != null) {
      cast = recipe.cast.getInputs();
    }
    else {
      cast = ImmutableList.of();
    }
    this.inputFluid = ImmutableList.of(recipe.fluid);
    this.recipe = recipe;
    // special treatment of oredict output recipies
    if(recipe.getResult() == null) {
      output = null;
    }
    else {
      output = ImmutableList.of(recipe.getResult());
    }

    this.castingBlock = castingBlock;
  }

  @Override
  public List<FluidStack> getFluidInputs() {
    return inputFluid;
  }

  @Override
  public List getInputs() {
    if(cast == null) {
      return super.getInputs();
    }
    return cast;
  }

  @Override
  public List getOutputs() {
    if(output == null) {
      if(recipe.getResult() == null) {
        return ImmutableList.of();
      }
      // we lazily evaluate the output in case the oredict wasn't there before
      output = ImmutableList.of(recipe.getResult());
    }
    return output;
  }

  @Override
  public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight) {
    castingBlock.draw(minecraft, 59, 42);
  }
}
