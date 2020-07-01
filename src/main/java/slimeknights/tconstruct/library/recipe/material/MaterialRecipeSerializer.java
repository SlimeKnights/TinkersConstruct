package slimeknights.tconstruct.library.recipe.material;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.MaterialId;

import javax.annotation.Nullable;

public class MaterialRecipeSerializer<T extends MaterialRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {

  private final IFactory<T> factory;

  public MaterialRecipeSerializer(IFactory<T> factory) {
    this.factory = factory;
  }

  @Override
  public T read(ResourceLocation recipeId, JsonObject json) {
    String group = JSONUtils.getString(json, "group", "");
    JsonElement input = JSONUtils.isJsonArray(json, "ingredient") ? JSONUtils.getJsonArray(json, "ingredient") : JSONUtils.getJsonObject(json, "ingredient");
    Ingredient ingredient = Ingredient.deserialize(input);
    int value = JSONUtils.getInt(json, "value", 1);
    int needed = JSONUtils.getInt(json, "needed", 1);
    String materialId = JSONUtils.getString(json, "material", "");

    if (materialId.isEmpty()) {
      throw new JsonSyntaxException("Recipe material must not empty.");
    }

    return this.factory.create(recipeId, group, ingredient, value, needed, new MaterialId(materialId));
  }

  @Nullable
  @Override
  public T read(ResourceLocation recipeId, PacketBuffer buffer) {
    try {
      String group = buffer.readString(32767);
      Ingredient ingredient = Ingredient.read(buffer);
      int value = buffer.readInt();
      int needed = buffer.readInt();
      String materialId = buffer.readString(32767);

      return this.factory.create(recipeId, group, ingredient, value, needed, new MaterialId(materialId));
    } catch (Exception e) {
      TConstruct.log.error("Error reading material recipe from packet.", e);
      throw e;
    }
  }

  @Override
  public void write(PacketBuffer buffer, T recipe) {
    try {
      buffer.writeString(recipe.group);
      recipe.ingredient.write(buffer);
      buffer.writeInt(recipe.value);
      buffer.writeInt(recipe.needed);
      buffer.writeString(recipe.materialId.toString());
    } catch (Exception e) {
      TConstruct.log.error("Error writing material recipe to packet.", e);
      throw e;
    }
  }

  public interface IFactory<T extends MaterialRecipe> {

    T create(ResourceLocation id, String group, Ingredient ingredient, int value, int needed, MaterialId materialId);
  }
}
