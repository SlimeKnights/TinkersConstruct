package slimeknights.tconstruct.library.recipe.casting;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.recipe.FluidIngredient;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

public class CastingBasinRecipe extends AbstractCastingRecipe {


  public CastingBasinRecipe(ResourceLocation id, String group, Ingredient cast, FluidIngredient fluid, ItemStack result, int coolingTime, boolean consumed, boolean switchSlots) {
    super(RecipeTypes.CASTING_BASIN, id, group, cast, fluid, result, coolingTime, consumed, switchSlots);
  }

  @Override
  public ItemStack getIcon() {
    return new ItemStack(TinkerSmeltery.castingBasin);
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerSmeltery.basinRecipeSerializer.get();
  }
}
