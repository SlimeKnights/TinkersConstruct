package slimeknights.tconstruct.library.recipe.casting;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.recipe.FluidIngredient;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CastingTableRecipe extends AbstractCastingRecipe {

  public CastingTableRecipe(ResourceLocation idIn, String groupIn, @Nullable Ingredient ingredient, @Nonnull FluidIngredient fluidIn, ItemStack result, int coolingTime, boolean consumed, boolean switchSlots) {
    super(RecipeTypes.CASTING_TABLE, idIn, groupIn, ingredient, fluidIn, result, coolingTime, consumed, switchSlots);
  }

  @Override
  public ItemStack getIcon() {
    return new ItemStack(TinkerSmeltery.castingTable);
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerSmeltery.tableRecipeSerializer.get();
  }
}
