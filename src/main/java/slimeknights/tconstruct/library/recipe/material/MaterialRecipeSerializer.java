package slimeknights.tconstruct.library.recipe.material;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.common.recipe.LoggingRecipeSerializer;
import slimeknights.tconstruct.library.materials.MaterialId;

import javax.annotation.Nullable;

/**
 * Serialiser for {@link MaterialRecipe}
 */
public class MaterialRecipeSerializer extends LoggingRecipeSerializer<MaterialRecipe> {
  private static final ItemOutput EMPTY = ItemOutput.fromStack(ItemStack.EMPTY);

  /**
   * Gets a material ID from JSON
   * @param json  Json parent
   * @param key  Key to get
   * @return  Material id
   */
  public static MaterialId getMaterial(JsonObject json, String key) {
    String materialId = JSONUtils.getString(json, key);
    if (materialId.isEmpty()) {
      throw new JsonSyntaxException("Material ID at " + key + " must not be empty");
    }
    return new MaterialId(materialId);
  }

  @Override
  public MaterialRecipe read(ResourceLocation recipeId, JsonObject json) {
    String group = JSONUtils.getString(json, "group", "");
    Ingredient ingredient = Ingredient.deserialize(JsonHelper.getElement(json, "ingredient"));
    int value = JSONUtils.getInt(json, "value", 1);
    int needed = JSONUtils.getInt(json, "needed", 1);
    MaterialId materialId = getMaterial(json, "material");
    ItemOutput leftover = EMPTY;
    if (value > 1 && json.has("leftover")) {
      leftover = ItemOutput.fromJson(json.get("leftover"));
    }
    return new MaterialRecipe(recipeId, group, ingredient, value, needed, new MaterialId(materialId), leftover);
  }

  @Nullable
  @Override
  protected MaterialRecipe readSafe(ResourceLocation recipeId, PacketBuffer buffer) {
    String group = buffer.readString(Short.MAX_VALUE);
    Ingredient ingredient = Ingredient.read(buffer);
    int value = buffer.readInt();
    int needed = buffer.readInt();
    String materialId = buffer.readString(Short.MAX_VALUE);
    ItemOutput leftover = ItemOutput.read(buffer);
    return new MaterialRecipe(recipeId, group, ingredient, value, needed, new MaterialId(materialId), leftover);
  }

  @Override
  protected void writeSafe(PacketBuffer buffer, MaterialRecipe recipe) {
    buffer.writeString(recipe.group);
    recipe.ingredient.write(buffer);
    buffer.writeInt(recipe.value);
    buffer.writeInt(recipe.needed);
    buffer.writeString(recipe.materialId.toString());
    recipe.leftover.write(buffer);
  }
}
