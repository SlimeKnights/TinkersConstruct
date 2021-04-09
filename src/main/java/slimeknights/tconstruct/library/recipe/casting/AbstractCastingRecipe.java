package slimeknights.tconstruct.library.recipe.casting;

import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.recipe.RecipeSerializer;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;

/** Shared logic between item and material casting */
public abstract class AbstractCastingRecipe implements ICastingRecipe {
  protected final RecipeType<?> type;
  protected final Identifier id;
  protected final String group;
  /** 'cast' item for recipe (doesn't have to be an actual 'cast') */
  protected final Ingredient cast;
  protected final boolean consumed;
  @Accessors(fluent = true)
  protected final boolean switchSlots;

  public AbstractCastingRecipe(RecipeType<?> type, Identifier id, String group, Ingredient cast, boolean consumed, boolean switchSlots) {
    this.type = type;
    this.id = id;
    this.group = group;
    this.cast = cast;
    this.consumed = consumed;
    this.switchSlots = switchSlots;
  }

  @Override
  public abstract ItemStack getOutput();

  public RecipeType<?> getType() {
    return this.type;
  }

  public Identifier getId() {
    return this.id;
  }

  public String getGroup() {
    return this.group;
  }

  public Ingredient getCast() {
    return this.cast;
  }

  public boolean isConsumed() {
    return this.consumed;
  }

  public boolean switchSlots() {
    return this.switchSlots;
  }

  /**
   * Seralizer for {@link ItemCastingRecipe}.
   * @param <T>  Casting recipe class type
   */
  public abstract static class Serializer<T extends AbstractCastingRecipe> extends RecipeSerializer<T> {
    public Serializer() {
    }

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
