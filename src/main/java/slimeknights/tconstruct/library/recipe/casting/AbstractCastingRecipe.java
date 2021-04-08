package slimeknights.tconstruct.library.recipe.casting;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import slimeknights.mantle.recipe.RecipeSerializer;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;

import org.jetbrains.annotations.Nullable;

/** Shared logic between item and material casting */
@AllArgsConstructor
public abstract class AbstractCastingRecipe implements ICastingRecipe {
  @Getter
  protected final RecipeType<?> type;
  @Getter
  protected final Identifier id;
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
  public abstract ItemStack getOutput();

  /**
   * Seralizer for {@link ItemCastingRecipe}.
   * @param <T>  Casting recipe class type
   */
  @AllArgsConstructor
  public abstract static class Serializer<T extends AbstractCastingRecipe> extends RecipeSerializer<T> {
    /** Creates a new instance from JSON */
    protected abstract T create(Identifier idIn, String groupIn, @Nullable Ingredient cast, boolean consumed, boolean switchSlots, JsonObject json);

    /** Creates a new instance from the packet buffer */
    protected abstract T create(Identifier idIn, String groupIn, @Nullable Ingredient cast, boolean consumed, boolean switchSlots, PacketByteBuf buffer);

    /** Writes extra data to the packet buffer */
    protected abstract void writeExtra(PacketByteBuf buffer, T recipe);

    @Override
    public T read(Identifier recipeId, JsonObject json) {
      Ingredient cast = Ingredient.EMPTY;
      String group = net.minecraft.util.JsonHelper.getString(json, "group", "");
      boolean consumed = false;
      boolean switchSlots = net.minecraft.util.JsonHelper.getBoolean(json, "switch_slots", false);
      if (json.has("cast")) {
        cast = Ingredient.fromJson(JsonHelper.getElement(json, "cast"));
        consumed = net.minecraft.util.JsonHelper.getBoolean(json, "cast_consumed", false);
      }
      return create(recipeId, group, cast, consumed, switchSlots, json);
    }

    @Nullable
    @Override
    public T read(Identifier recipeId, PacketByteBuf buffer) {
      try {
        String group = buffer.readString(Short.MAX_VALUE);
        Ingredient cast = Ingredient.fromPacket(buffer);
        boolean consumed = buffer.readBoolean();
        boolean switchSlots = buffer.readBoolean();
        return create(recipeId, group, cast, consumed, switchSlots, buffer);
      } catch (Exception e) {
        TConstruct.log.error("Error reading item casting recipe from packet.", e);
        throw e;
      }
    }

    @Override
    public void write(PacketByteBuf buffer, T recipe) {
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
