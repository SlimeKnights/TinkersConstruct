package slimeknights.tconstruct.fluids.util;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.brewing.BrewingRecipe;

/** Recipe for transforming a bottle, depending on a vanilla brewing recipe to get the ingredient */
public class BottleBrewingRecipe extends BrewingRecipe {
  private final Item from;
  private final Item to;
  public BottleBrewingRecipe(Ingredient input, Item from, Item to, ItemStack output) {
    super(input, getIngredient(from, to), output);
    this.from = from;
    this.to = to;
  }

  private static Ingredient getIngredient(Item from, Item to) {
    if (from == Items.POTION && to == Items.SPLASH_POTION) {
      return Ingredient.of(Items.GUNPOWDER);
    }
    if (from == Items.SPLASH_POTION && to == Items.LINGERING_POTION) {
      return Ingredient.of(Items.DRAGON_BREATH);
    }
    return Ingredient.EMPTY;
  }
}
