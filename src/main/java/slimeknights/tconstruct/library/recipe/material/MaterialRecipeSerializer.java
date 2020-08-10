package slimeknights.tconstruct.library.recipe.material;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.MaterialId;

import javax.annotation.Nullable;

/**
 * Serialiser for {@link MaterialRecipe}
 */
public class MaterialRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<MaterialRecipe> {
  @Override
  public MaterialRecipe read(ResourceLocation recipeId, JsonObject json) {
    String group = JSONUtils.getString(json, "group", "");
    Ingredient ingredient = Ingredient.deserialize(JsonHelper.getElement(json, "ingredient"));
    int value = JSONUtils.getInt(json, "value", 1);
    int needed = JSONUtils.getInt(json, "needed", 1);
    String materialId = JSONUtils.getString(json, "material");
    if (materialId.isEmpty()) {
      throw new JsonSyntaxException("Recipe material must not empty.");
    }
    return new MaterialRecipe(recipeId, group, ingredient, value, needed, new MaterialId(materialId));
  }

  @Nullable
  @Override
  public MaterialRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
    try {
      String group = buffer.readString(Short.MAX_VALUE);
      Ingredient ingredient = Ingredient.read(buffer);
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
  public void write(PacketBuffer buffer, MaterialRecipe recipe) {
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
