package slimeknights.tconstruct.library.recipe.casting;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.recipe.RecipeSerializer;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;

import javax.annotation.Nullable;

/** Shared logic between item and material casting */
@AllArgsConstructor
public abstract class AbstractCastingRecipe implements ICastingRecipe {
  @Getter
  protected final IRecipeType<?> type;
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
  public abstract ItemStack getRecipeOutput();

  /**
   * Seralizer for {@link ItemCastingRecipe}.
   * @param <T>  Casting recipe class type
   */
  @AllArgsConstructor
  public abstract static class Serializer<T extends AbstractCastingRecipe> extends RecipeSerializer<T> {
    /** Creates a new instance from JSON */
    protected abstract T create(ResourceLocation idIn, String groupIn, @Nullable Ingredient cast, boolean consumed, boolean switchSlots, JsonObject json);

    /** Creates a new instance from the packet buffer */
    protected abstract T create(ResourceLocation idIn, String groupIn, @Nullable Ingredient cast, boolean consumed, boolean switchSlots, PacketBuffer buffer);

    /** Writes extra data to the packet buffer */
    protected abstract void writeExtra(PacketBuffer buffer, T recipe);

    @Override
    public T read(ResourceLocation recipeId, JsonObject json) {
      Ingredient cast = Ingredient.EMPTY;
      String group = JSONUtils.getString(json, "group", "");
      boolean consumed = false;
      boolean switchSlots = JSONUtils.getBoolean(json, "switch_slots", false);
      if (json.has("cast")) {
        cast = Ingredient.deserialize(JsonHelper.getElement(json, "cast"));
        consumed = JSONUtils.getBoolean(json, "cast_consumed", false);
      }
      return create(recipeId, group, cast, consumed, switchSlots, json);
    }

    @Nullable
    @Override
    public T read(ResourceLocation recipeId, PacketBuffer buffer) {
      try {
        String group = buffer.readString(Short.MAX_VALUE);
        Ingredient cast = Ingredient.read(buffer);
        boolean consumed = buffer.readBoolean();
        boolean switchSlots = buffer.readBoolean();
        return create(recipeId, group, cast, consumed, switchSlots, buffer);
      } catch (Exception e) {
        TConstruct.log.error("Error reading item casting recipe from packet.", e);
        throw e;
      }
    }

    @Override
    public void write(PacketBuffer buffer, T recipe) {
      try {
        buffer.writeString(recipe.group);
        recipe.cast.write(buffer);
        buffer.writeBoolean(recipe.consumed);
        buffer.writeBoolean(recipe.switchSlots);
        writeExtra(buffer, recipe);
      } catch (Exception e) {
        TConstruct.log.error("Error writing item casting recipe to packet.", e);
        throw e;
      }
    }
  }
}
