package slimeknights.tconstruct.plugin.jei.drying;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.util.List;

import javax.annotation.Nonnull;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import slimeknights.tconstruct.library.DryingRecipe;

public class DryingRecipeWrapper implements IRecipeWrapper {

  protected final List<List<ItemStack>> input;
  protected final List<ItemStack> output;
  protected final int time;

  public DryingRecipeWrapper(DryingRecipe recipe) {
    this.input = ImmutableList.of(recipe.input.getInputs());
    this.output = ImmutableList.of(recipe.getResult());
    this.time = recipe.getTime();
  }

  @Override
  public void getIngredients(IIngredients ingredients) {
    ingredients.setInputLists(ItemStack.class, input);
    ingredients.setOutputs(ItemStack.class, output);
  }

  @Override
  public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
    String minStr = "", secStr = "";

    // minutes is time divided by 20 ticks and 60 seconds
    int minutes = time / 20 / 60;
    if(minutes > 0) {
      minStr = String.valueOf(minutes) + "m";
    }

    // seconds is remainder of the previous. Note that ticks beyond seconds are ignored
    int seconds = (time / 20) % 60;
    if(seconds > 0) {
      // if using both minutes and seconds, add a space between
      if(minutes > 0) {
        secStr += " ";
      }

      secStr += String.valueOf(seconds) + "s";
    }

    // center the string above the recipe then draw it
    String timeStr = minStr + secStr;
    int x = 80 - minecraft.fontRenderer.getStringWidth(timeStr) / 2;
    minecraft.fontRenderer.drawString(timeStr, x, 5, Color.gray.getRGB());
  }
}
