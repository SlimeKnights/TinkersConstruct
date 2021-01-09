package slimeknights.tconstruct.library.recipe.casting;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.mantle.recipe.RecipeSerializer;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;

import javax.annotation.Nullable;

/**
 * Serializer for {@link MaterialCastingRecipe}
 * @param <T>  Recipe class type
 */
@AllArgsConstructor
public class MaterialCastingRecipeSerializer<T extends MaterialCastingRecipe> extends RecipeSerializer<T> {
  private final MaterialCastingRecipeSerializer.IFactory<T> factory;

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
    int fluidAmount = JSONUtils.getInt(json, "fluid_amount");
    IMaterialItem result = RecipeHelper.deserializeItem(JSONUtils.getString(json, "result"), "result", IMaterialItem.class);
    return this.factory.create(recipeId, group, cast, fluidAmount, result, consumed, switchSlots);
  }

  @Nullable
  @Override
  public T read(ResourceLocation recipeId, PacketBuffer buffer) {
    try {
      String group = buffer.readString(Short.MAX_VALUE);
      Ingredient cast = Ingredient.read(buffer);
      int fluidAmount = buffer.readInt();
      IMaterialItem result = RecipeHelper.readItem(buffer, IMaterialItem.class);
      boolean consumed = buffer.readBoolean();
      boolean switchSlots = buffer.readBoolean();
      return this.factory.create(recipeId, group, cast, fluidAmount, result, consumed, switchSlots);
    } catch (Exception e) {
      TConstruct.log.error("Error reading material casting recipe from packet.", e);
      throw e;
    }
  }

  @Override
  public void write(PacketBuffer buffer, T recipe) {
    try {
      buffer.writeString(recipe.group);
      recipe.cast.write(buffer);
      buffer.writeInt(recipe.fluidAmount);
      RecipeHelper.writeItem(buffer, recipe.result);
      buffer.writeBoolean(recipe.consumed);
      buffer.writeBoolean(recipe.switchSlots);
    } catch (Exception e) {
      TConstruct.log.error("Error writing material casting recipe to packet.", e);
      throw e;
    }
  }

  /**
   * Interface representing a material casting recipe constructor
   * @param <T>  Recipe class type
   */
  public interface IFactory<T extends MaterialCastingRecipe> {
    T create(ResourceLocation id, String group, Ingredient cast, int fluidAmount, IMaterialItem result,
             boolean consumed, boolean switchSlots);
  }
}
