package slimeknights.tconstruct.fluids.util;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipe;

/** Recipe for transforming a bottle, depending on a vanilla brewing recipe to get the ingredient */
public class BottleBrewingRecipe extends BrewingRecipe {
  private final Item from;
  private final Item to;
  public BottleBrewingRecipe(Ingredient input, Item from, Item to, ItemStack output) {
    super(input, Ingredient.EMPTY, output);
    this.from = from;
    this.to = to;
  }

  @Override
  public boolean isIngredient(ItemStack stack) {
    for (PotionBrewing.Mix<Item> recipe : PotionBrewing.CONTAINER_MIXES) {
      if (recipe.from.get() == from && recipe.to.get() == to) {
        return recipe.ingredient.test(stack);
      }
    }
    return false;
  }

  @Override
  public Ingredient getIngredient() {
    for (PotionBrewing.Mix<Item> recipe : PotionBrewing.CONTAINER_MIXES) {
      if (recipe.from.get() == from && recipe.to.get() == to) {
        return recipe.ingredient;
      }
    }
    return Ingredient.EMPTY;
  }
}
