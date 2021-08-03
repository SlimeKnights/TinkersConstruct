package slimeknights.tconstruct.library.recipe.tinkerstation.building;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.tconstruct.common.recipe.LoggingRecipeSerializer;
import slimeknights.tconstruct.library.tools.item.IModifiable;

import javax.annotation.Nullable;

public class ToolBuildingRecipeSerializer extends LoggingRecipeSerializer<ToolBuildingRecipe> {

  @Override
  public ToolBuildingRecipe read(ResourceLocation recipeId, JsonObject json) {
    String group = JSONUtils.getString(json, "group", "");
    // output fetch as a modifiable item, its an error if it does not implement that interface or does not have parts
    IModifiable item = RecipeHelper.deserializeItem(JSONUtils.getString(json, "result"), "result", IModifiable.class);
    if (!item.getToolDefinition().isMultipart()) {
      throw new JsonSyntaxException("Modifiable item must have tool parts to get a tool building recipe");
    }
    return new ToolBuildingRecipe(recipeId, group, item);
  }

  @Nullable
  @Override
  protected ToolBuildingRecipe readSafe(ResourceLocation recipeId, PacketBuffer buffer) {
    String group = buffer.readString(Short.MAX_VALUE);
    IModifiable result = RecipeHelper.readItem(buffer, IModifiable.class);
    return new ToolBuildingRecipe(recipeId, group, result);
  }

  @Override
  protected void writeSafe(PacketBuffer buffer, ToolBuildingRecipe recipe) {
    buffer.writeString(recipe.group);
    RecipeHelper.writeItem(buffer, recipe.output);
  }
}
