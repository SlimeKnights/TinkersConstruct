package slimeknights.tconstruct.library.recipe.casting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.RecipeUtil;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;

import javax.annotation.Nullable;

@AllArgsConstructor
public class MaterialCastingRecipeSerializer<T extends MaterialCastingRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>>
  implements IRecipeSerializer<T> {
  private final MaterialCastingRecipeSerializer.IFactory<T> factory;

  @Override
  public T read(ResourceLocation recipeId, JsonObject json) {
    Ingredient cast = Ingredient.EMPTY;
    String group = JSONUtils.getString(json, "group", "");
    boolean consumed = false;
    boolean switchSlots = JSONUtils.getBoolean(json, "switch_slots", false);
    if (json.has("cast")) {
      JsonElement jsonElement = JSONUtils.getJsonObject(json, "cast");
      cast = Ingredient.deserialize(jsonElement);
      consumed = JSONUtils.getBoolean(json, "cast_consumed", false);
    }
    int fluidAmount = JSONUtils.getInt(json, "fluid_amount");
    IMaterialItem result = RecipeUtil.deserializeMaterialItem(JSONUtils.getString(json, "result"), "result");
    return this.factory.create(recipeId, group, cast, fluidAmount, result, consumed, switchSlots);
  }

  @Nullable
  @Override
  public T read(ResourceLocation recipeId, PacketBuffer buffer) {
    try {
      String group = buffer.readString(Short.MAX_VALUE);
      Ingredient cast = Ingredient.read(buffer);
      int fluidAmount = buffer.readInt();
      int itemId = buffer.readVarInt();
      IMaterialItem result = RecipeUtil.readMaterialItem(buffer);
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
      buffer.writeInt(Item.getIdFromItem(recipe.result.asItem()));
      buffer.writeBoolean(recipe.consumed);
      buffer.writeBoolean(recipe.switchSlots);
    } catch (Exception e) {
      TConstruct.log.error("Error writing material casting recipe to packet.", e);
      throw e;
    }
  }

  public interface IFactory<T extends MaterialCastingRecipe> {
    T create(ResourceLocation id, String group, Ingredient cast, int fluidAmount, IMaterialItem result,
             boolean consumed, boolean switchSlots);
  }
}
