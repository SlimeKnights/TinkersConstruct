package slimeknights.tconstruct.library.recipe.casting;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.common.recipe.LoggingRecipeSerializer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/** Shared logic between item and material casting */
@AllArgsConstructor
public abstract class AbstractCastingRecipe implements ICastingRecipe {
  @Getter @Nonnull
  protected final RecipeType<?> type;
  @Getter
  protected final ResourceLocation id;
  @Getter
  protected final String group;
  /** 'cast' item for recipe (doesn't have to be an actual 'cast') */
  @Getter
  protected final Ingredient cast;
  @Getter
  protected final boolean consumed;
  @Getter @Accessors(fluent = true)
  protected final boolean switchSlots;

  @Override
  public abstract ItemStack getResultItem();

  @Override
  public NonNullList<Ingredient> getIngredients() {
    return NonNullList.of(Ingredient.EMPTY, this.cast);
  }

  /**
   * Seralizer for {@link ItemCastingRecipe}.
   * @param <T>  Casting recipe class type
   */
  @AllArgsConstructor
  public abstract static class Serializer<T extends AbstractCastingRecipe> extends LoggingRecipeSerializer<T> {
    /** Creates a new instance from JSON */
    protected abstract T create(ResourceLocation idIn, String groupIn, @Nullable Ingredient cast, boolean consumed, boolean switchSlots, JsonObject json);

    /** Creates a new instance from the packet buffer */
    protected abstract T create(ResourceLocation idIn, String groupIn, @Nullable Ingredient cast, boolean consumed, boolean switchSlots, FriendlyByteBuf buffer);

    /** Writes extra data to the packet buffer */
    protected abstract void writeExtra(FriendlyByteBuf buffer, T recipe);

    @Override
    public T fromJson(ResourceLocation recipeId, JsonObject json) {
      Ingredient cast = Ingredient.EMPTY;
      String group = GsonHelper.getAsString(json, "group", "");
      boolean consumed = false;
      boolean switchSlots = GsonHelper.getAsBoolean(json, "switch_slots", false);
      if (json.has("cast")) {
        cast = Ingredient.fromJson(JsonHelper.getElement(json, "cast"));
        consumed = GsonHelper.getAsBoolean(json, "cast_consumed", false);
      }
      return create(recipeId, group, cast, consumed, switchSlots, json);
    }

    @Nullable
    @Override
    protected T readSafe(ResourceLocation recipeId, FriendlyByteBuf buffer) {
      String group = buffer.readUtf(Short.MAX_VALUE);
      Ingredient cast = Ingredient.fromNetwork(buffer);
      boolean consumed = buffer.readBoolean();
      boolean switchSlots = buffer.readBoolean();
      return create(recipeId, group, cast, consumed, switchSlots, buffer);
    }

    @Override
    protected void writeSafe(FriendlyByteBuf buffer, T recipe) {
      buffer.writeUtf(recipe.group);
      recipe.cast.toNetwork(buffer);
      buffer.writeBoolean(recipe.consumed);
      buffer.writeBoolean(recipe.switchSlots);
      writeExtra(buffer, recipe);
    }
  }
}
