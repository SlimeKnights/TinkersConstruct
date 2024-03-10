package slimeknights.tconstruct.library.recipe.molding;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import slimeknights.mantle.recipe.ICommonRecipe;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.helper.LoggingRecipeSerializer;
import slimeknights.mantle.util.JsonHelper;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/** Recipe to combine two items on the top of a casting table, changing the first */
@RequiredArgsConstructor
public class MoldingRecipe implements ICommonRecipe<IMoldingContainer> {
  @Getter
  private final RecipeType<MoldingRecipe> type;
  @Getter
  private final RecipeSerializer<?> serializer;
  @Getter
  private final ResourceLocation id;
  @Getter
  private final Ingredient material;
  @Getter
  private final Ingredient pattern;
  @Getter
  private final boolean patternConsumed;
  private final ItemOutput recipeOutput;

	@Override
  public boolean matches(IMoldingContainer inv, Level worldIn) {
    return material.test(inv.getMaterial()) && pattern.test(inv.getPattern());
  }

  @Override
  public NonNullList<Ingredient> getIngredients() {
    return NonNullList.of(Ingredient.EMPTY, material, pattern);
  }

  @Override
  public ItemStack getResultItem() {
    return recipeOutput.get();
  }

  /** Generic serializer to both types */
  @RequiredArgsConstructor
  public static class Serializer extends LoggingRecipeSerializer<MoldingRecipe> {
    private final Supplier<RecipeType<MoldingRecipe>> type;

    @Override
    public MoldingRecipe fromJson(ResourceLocation id, JsonObject json) {
      Ingredient material = Ingredient.fromJson(JsonHelper.getElement(json, "material"));
      Ingredient pattern = Ingredient.EMPTY;
      boolean patternConsumed = false;
      if (json.has("pattern")) {
        pattern = Ingredient.fromJson(json.get("pattern"));
        patternConsumed = GsonHelper.getAsBoolean(json, "pattern_consumed", false);
      }
      ItemOutput output = ItemOutput.fromJson(json.get("result"));
      return new MoldingRecipe(type.get(), this, id, material, pattern, patternConsumed, output);
    }

    @Nullable
    @Override
    protected MoldingRecipe fromNetworkSafe(ResourceLocation id, FriendlyByteBuf buffer) {
      Ingredient material = Ingredient.fromNetwork(buffer);
      Ingredient mold = Ingredient.fromNetwork(buffer);
      boolean moldConsumed = buffer.readBoolean();
      ItemOutput output = ItemOutput.read(buffer);
      return new MoldingRecipe(type.get(), this, id, material, mold, moldConsumed, output);
    }

    @Override
    protected void toNetworkSafe(FriendlyByteBuf buffer, MoldingRecipe recipe) {
      recipe.material.toNetwork(buffer);
      recipe.pattern.toNetwork(buffer);
      buffer.writeBoolean(recipe.patternConsumed);
      recipe.recipeOutput.write(buffer);
    }
  }
}
