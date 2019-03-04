package slimeknights.tconstruct.library;

import net.minecraft.item.ItemStack;

import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.utils.ListUtil;

public class DryingRecipe {

  public final int time;
  public final RecipeMatch input;
  public final ItemStack output;

  public DryingRecipe(RecipeMatch input, ItemStack output, int time) {
    this.time = time;
    this.input = input;
    this.output = output;
  }

  public boolean matches(ItemStack input) {
    return this.input != null && this.input.matches(ListUtil.getListFrom(input)).isPresent();

  }

  public ItemStack getResult() {
    return output.copy();
  }

  public int getTime() {
    return time;
  }
}
