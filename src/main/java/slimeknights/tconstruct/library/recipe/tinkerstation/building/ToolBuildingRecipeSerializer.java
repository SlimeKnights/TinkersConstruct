package slimeknights.tconstruct.library.recipe.tinkerstation.building;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.tconstruct.common.recipe.LoggingRecipeSerializer;
import slimeknights.tconstruct.library.tools.item.ToolCore;

import javax.annotation.Nullable;

public class ToolBuildingRecipeSerializer extends LoggingRecipeSerializer<ToolBuildingRecipe> {

  @Override
  public ToolBuildingRecipe read(ResourceLocation recipeId, JsonObject json) {
    String group = JSONUtils.getString(json, "group", "");
    // output fetch as a toolcore item, its an error if it does not implement that interface
    ToolCore item = RecipeHelper.deserializeItem(JSONUtils.getString(json, "result"), "result", ToolCore.class);
    return new ToolBuildingRecipe(recipeId, group, item);
  }

  @Nullable
  @Override
  protected ToolBuildingRecipe readSafe(ResourceLocation recipeId, PacketBuffer buffer) {
    String group = buffer.readString(Short.MAX_VALUE);
    ToolCore result = RecipeHelper.readItem(buffer, ToolCore.class);
    return new ToolBuildingRecipe(recipeId, group, result);
  }

  @Override
  protected void writeSafe(PacketBuffer buffer, ToolBuildingRecipe recipe) {
    buffer.writeString(recipe.group);
    RecipeHelper.writeItem(buffer, recipe.output);
  }
}
