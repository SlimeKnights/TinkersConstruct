package slimeknights.tconstruct.library.recipe.material;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.MaterialId;

/**
 * Serialiser for {@link MaterialRecipe}
 */
public class MaterialRecipeSerializer implements RecipeSerializer<MaterialRecipe> {
  /**
   * Gets a material ID from JSON
   * @param json  Json parent
   * @param key  Key to get
   * @return  Material id
   */
  public static MaterialId getMaterial(JsonObject json, String key) {
    String materialId = net.minecraft.util.JsonHelper.getString(json, key);
    if (materialId.isEmpty()) {
      throw new JsonSyntaxException("Material ID at " + key + " must not be empty");
    }
    return new MaterialId(materialId);
  }

  @Override
  public MaterialRecipe read(Identifier recipeId, JsonObject json) {
    String group = net.minecraft.util.JsonHelper.getString(json, "group", "");
    Ingredient ingredient = Ingredient.fromJson(JsonHelper.getElement(json, "ingredient"));
    int value = net.minecraft.util.JsonHelper.getInt(json, "value", 1);
    int needed = net.minecraft.util.JsonHelper.getInt(json, "needed", 1);
    MaterialId materialId = getMaterial(json, "material");
    return new MaterialRecipe(recipeId, group, ingredient, value, needed, new MaterialId(materialId));
  }

  @Nullable
  @Override
  public MaterialRecipe read(Identifier recipeId, PacketByteBuf buffer) {
    try {
      String group = buffer.readString(Short.MAX_VALUE);
      Ingredient ingredient = Ingredient.fromPacket(buffer);
      int value = buffer.readInt();
      int needed = buffer.readInt();
      String materialId = buffer.readString(Short.MAX_VALUE);
      return new MaterialRecipe(recipeId, group, ingredient, value, needed, new MaterialId(materialId));
    } catch (Exception e) {
      TConstruct.log.error("Error reading material recipe from packet.", e);
      throw e;
    }
  }

  @Override
  public void write(PacketByteBuf buffer, MaterialRecipe recipe) {
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
}
