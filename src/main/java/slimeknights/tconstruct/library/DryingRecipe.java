package slimeknights.tconstruct.library;

import net.minecraft.item.ItemStack;

import slimeknights.mantle.util.RecipeMatch;

public class DryingRecipe {

  public final int time;
  public final RecipeMatch input;
  public final ItemStack output;

  DryingRecipe(RecipeMatch input, ItemStack output, int time) {
    this.time = time;
    this.input = input;
    this.output = output;
  }

  public boolean matches(ItemStack input) {
    return this.input != null && this.input.matches(new ItemStack[]{input}) != null;

  }

  public ItemStack getResult() {
    return output.copy();
  }

  public int getTime() {
    return time;
  }
}
