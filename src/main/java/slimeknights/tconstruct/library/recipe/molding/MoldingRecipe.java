package slimeknights.tconstruct.library.recipe.molding;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import slimeknights.mantle.recipe.ICommonRecipe;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.mantle.recipe.RecipeSerializer;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import org.jetbrains.annotations.Nullable;

/** Recipe to combine two items on the top of a casting table, changing the first */
@RequiredArgsConstructor
public abstract class MoldingRecipe implements ICommonRecipe<IMoldingInventory> {
  @Getter
  private final Identifier id;
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
  public DefaultedList<Ingredient> getPreviewInputs() {
    return DefaultedList.copyOf(Ingredient.EMPTY, material, pattern);
  }

  @Override
  public ItemStack getOutput() {
    return recipeOutput.get();
  }

  /** Subclass for table recipes */
  public static class Table extends MoldingRecipe {
    public Table(Identifier id, Ingredient material, Ingredient mold, boolean moldConsumed, ItemOutput recipeOutput) {
      super(id, material, mold, moldConsumed, recipeOutput);
    }

    @Override
    public net.minecraft.recipe.RecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.moldingTableSerializer.get();
    }

    @Override
    public RecipeType<?> getType() {
      return RecipeTypes.MOLDING_TABLE;
    }
  }

  /** Subclass for basin recipes */
  public static class Basin extends MoldingRecipe {
    public Basin(Identifier id, Ingredient material, Ingredient mold, boolean moldConsumed, ItemOutput recipeOutput) {
      super(id, material, mold, moldConsumed, recipeOutput);
    }

    @Override
    public net.minecraft.recipe.RecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.moldingBasinSerializer.get();
    }

    @Override
    public RecipeType<?> getType() {
      return RecipeTypes.MOLDING_BASIN;
    }
  }

  /** Serializer factory interface */
  @FunctionalInterface
  public interface IFactory<T extends MoldingRecipe> {
    T create(Identifier id, Ingredient material, Ingredient mold, boolean moldConsumed, ItemOutput recipeOutput);
  }

  /** Generic serializer to both types */
  @RequiredArgsConstructor
  public static class Serializer<T extends MoldingRecipe> extends RecipeSerializer<T> {
    private final IFactory<T> factory;

    @Override
    public T read(Identifier id, JsonObject json) {
      Ingredient material = Ingredient.fromJson(JsonHelper.getElement(json, "material"));
      Ingredient pattern = Ingredient.EMPTY;
      boolean patternConsumed = false;
      if (json.has("pattern")) {
        pattern = Ingredient.fromJson(json.get("pattern"));
        patternConsumed = net.minecraft.util.JsonHelper.getBoolean(json, "pattern_consumed", false);
      }
      ItemOutput output = ItemOutput.fromJson(json.get("result"));
      return factory.create(id, material, pattern, patternConsumed, output);
    }

    @Nullable
    @Override
    public T read(Identifier id, PacketByteBuf buffer) {
      Ingredient material = Ingredient.fromPacket(buffer);
      Ingredient mold = Ingredient.fromPacket(buffer);
      boolean moldConsumed = buffer.readBoolean();
      ItemOutput output = ItemOutput.read(buffer);
      return factory.create(id, material, mold, moldConsumed, output);
    }

    @Override
    public void write(PacketByteBuf buffer, MoldingRecipe recipe) {
      recipe.material.write(buffer);
      recipe.pattern.write(buffer);
      buffer.writeBoolean(recipe.patternConsumed);
      recipe.recipeOutput.write(buffer);
    }
  }
}
