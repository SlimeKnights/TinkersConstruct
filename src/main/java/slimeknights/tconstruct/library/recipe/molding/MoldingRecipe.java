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
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.mantle.recipe.RecipeSerializer;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;

/** Recipe to combine two items on the top of a casting table, changing the first */
@RequiredArgsConstructor
public abstract class MoldingRecipe implements ICommonRecipe<IMoldingInventory> {
  @Getter
  private final ResourceLocation id;
  private final Ingredient material;
  @Getter
  private final Ingredient pattern;
  @Getter
  private final boolean patternConsumed;
  private final ItemOutput recipeOutput;

	@Override
  public boolean matches(IMoldingInventory inv, World worldIn) {
    return material.test(inv.getMaterial()) && pattern.test(inv.getPattern());
  }

  @Override
  public NonNullList<Ingredient> getIngredients() {
    return NonNullList.from(Ingredient.EMPTY, material, pattern);
  }

  @Override
  public ItemStack getRecipeOutput() {
    return recipeOutput.get();
  }

  /** Subclass for table recipes */
  public static class Table extends MoldingRecipe {
    public Table(ResourceLocation id, Ingredient material, Ingredient mold, boolean moldConsumed, ItemOutput recipeOutput) {
      super(id, material, mold, moldConsumed, recipeOutput);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.moldingTableSerializer.get();
    }

    @Override
    public IRecipeType<?> getType() {
      return RecipeTypes.MOLDING_TABLE;
    }
  }

  /** Subclass for basin recipes */
  public static class Basin extends MoldingRecipe {
    public Basin(ResourceLocation id, Ingredient material, Ingredient mold, boolean moldConsumed, ItemOutput recipeOutput) {
      super(id, material, mold, moldConsumed, recipeOutput);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.moldingBasinSerializer.get();
    }

    @Override
    public IRecipeType<?> getType() {
      return RecipeTypes.MOLDING_BASIN;
    }
  }

  /** Serializer factory interface */
  @FunctionalInterface
  public interface IFactory<T extends MoldingRecipe> {
    T create(ResourceLocation id, Ingredient material, Ingredient mold, boolean moldConsumed, ItemOutput recipeOutput);
  }

  /** Generic serializer to both types */
  @RequiredArgsConstructor
  public static class Serializer<T extends MoldingRecipe> extends RecipeSerializer<T> {
    private final IFactory<T> factory;

    @Override
    public T read(ResourceLocation id, JsonObject json) {
      Ingredient material = Ingredient.deserialize(JsonHelper.getElement(json, "material"));
      Ingredient pattern = Ingredient.EMPTY;
      boolean patternConsumed = false;
      if (json.has("pattern")) {
        pattern = Ingredient.deserialize(json.get("pattern"));
        patternConsumed = JSONUtils.getBoolean(json, "pattern_consumed", false);
      }
      ItemOutput output = ItemOutput.fromJson(json.get("result"));
      return factory.create(id, material, pattern, patternConsumed, output);
    }

    @Nullable
    @Override
    public T read(ResourceLocation id, PacketBuffer buffer) {
      Ingredient material = Ingredient.read(buffer);
      Ingredient mold = Ingredient.read(buffer);
      boolean moldConsumed = buffer.readBoolean();
      ItemOutput output = ItemOutput.read(buffer);
      return factory.create(id, material, mold, moldConsumed, output);
    }

    @Override
    public void write(PacketBuffer buffer, MoldingRecipe recipe) {
      recipe.material.write(buffer);
      recipe.pattern.write(buffer);
      buffer.writeBoolean(recipe.patternConsumed);
      recipe.recipeOutput.write(buffer);
    }
  }
}
