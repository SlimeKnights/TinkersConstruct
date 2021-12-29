package slimeknights.tconstruct.library.recipe.tinkerstation.building;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.recipe.helper.RecipeHelper;
import slimeknights.tconstruct.common.recipe.LoggingRecipeSerializer;
import slimeknights.tconstruct.library.tools.item.IModifiable;

import javax.annotation.Nullable;

public class ToolBuildingRecipeSerializer extends LoggingRecipeSerializer<ToolBuildingRecipe> {

  @Override
  public ToolBuildingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
    String group = GsonHelper.getAsString(json, "group", "");
    // output fetch as a modifiable item, its an error if it does not implement that interface or does not have parts
    IModifiable item = RecipeHelper.deserializeItem(GsonHelper.getAsString(json, "result"), "result", IModifiable.class);
    if (!item.getToolDefinition().isMultipart()) {
      throw new JsonSyntaxException("Modifiable item must have tool parts to get a tool building recipe");
    }
    return new ToolBuildingRecipe(recipeId, group, item);
  }

  @Nullable
  @Override
  protected ToolBuildingRecipe readSafe(ResourceLocation recipeId, FriendlyByteBuf buffer) {
    String group = buffer.readUtf(Short.MAX_VALUE);
    IModifiable result = RecipeHelper.readItem(buffer, IModifiable.class);
    return new ToolBuildingRecipe(recipeId, group, result);
  }

  @Override
  protected void writeSafe(FriendlyByteBuf buffer, ToolBuildingRecipe recipe) {
    buffer.writeUtf(recipe.group);
    RecipeHelper.writeItem(buffer, recipe.output);
  }
}
