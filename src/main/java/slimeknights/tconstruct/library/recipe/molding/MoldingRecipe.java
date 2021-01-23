package slimeknights.tconstruct.library.recipe.molding;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import slimeknights.mantle.recipe.ICommonRecipe;
import slimeknights.mantle.recipe.RecipeSerializer;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipe;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;

/** Recipe to combine two items on the top of a casting table, changing the first */
@RequiredArgsConstructor
public class MoldingRecipe implements ICommonRecipe<IMoldingInventory> {
  @Getter
  private final ResourceLocation id;
  private final Ingredient material;
  @Getter
  private final Ingredient mold;
  @Getter
  private final boolean moldConsumed;
  @Getter
  private final ItemStack recipeOutput;

  @Override
  public boolean matches(IMoldingInventory inv, World worldIn) {
    return material.test(inv.getMaterial()) && mold.test(inv.getMold());
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerSmeltery.moldingSerializer.get();
  }

  @Override
  public IRecipeType<?> getType() {
    return RecipeTypes.MOLDING;
  }

  @Override
  public NonNullList<Ingredient> getIngredients() {
    return NonNullList.from(Ingredient.EMPTY, material, mold);
  }

  public static class Serializer extends RecipeSerializer<MoldingRecipe> {
    @Override
    public MoldingRecipe read(ResourceLocation id, JsonObject json) {
      Ingredient material = Ingredient.deserialize(JsonHelper.getElement(json, "material"));
      Ingredient mold = Ingredient.EMPTY;
      boolean moldConsumed = false;
      if (json.has("mold")) {
        mold = Ingredient.deserialize(json.get("mold"));
        moldConsumed = JSONUtils.getBoolean(json, "mold_consumed", false);
      }
      ItemStack output = ItemCastingRecipe.deseralizeResultItem(json, "result");
      return new MoldingRecipe(id, material, mold, moldConsumed, output);
    }

    @Nullable
    @Override
    public MoldingRecipe read(ResourceLocation id, PacketBuffer buffer) {
      Ingredient material = Ingredient.read(buffer);
      Ingredient mold = Ingredient.read(buffer);
      boolean moldConsumed = buffer.readBoolean();
      ItemStack output = buffer.readItemStack();
      return new MoldingRecipe(id, material, mold, moldConsumed, output);
    }

    @Override
    public void write(PacketBuffer buffer, MoldingRecipe recipe) {
      recipe.material.write(buffer);
      recipe.mold.write(buffer);
      buffer.writeBoolean(recipe.moldConsumed);
      buffer.writeItemStack(recipe.recipeOutput);
    }
  }
}
