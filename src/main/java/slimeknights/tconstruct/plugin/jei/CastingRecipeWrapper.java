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
  protected final List<ItemStack> output;

  public final IDrawable castingBlock;

  public CastingRecipeWrapper(CastingRecipe recipe, IDrawable castingBlock) {
    cast = recipe.cast.getInputs();
    inputFluid = ImmutableList.of(recipe.fluid);
    output = ImmutableList.of(recipe.getResult());

    this.castingBlock = castingBlock;
  }

  @Override
  public List<FluidStack> getFluidInputs() {
    return inputFluid;
  }

  @Override
  public List getInputs() {
    return cast;
  }

  @Override
  public List getOutputs() {
    return output;
  }

  @Override
  public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight) {
    castingBlock.draw(minecraft, 59, 42);
  }
}
