package slimeknights.tconstruct.library.recipe.tinkerstation.building;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.item.ToolCore;

public class ToolBuildingRecipeSerializer implements RecipeSerializer<ToolBuildingRecipe> {

  @Override
  public ToolBuildingRecipe read(Identifier recipeId, JsonObject json) {
    String group = JsonHelper.getString(json, "group", "");

    // output fetch as a toolcore item, its an error if it does not implement that interface
    ToolCore item = RecipeHelper.deserializeItem(JsonHelper.getString(json, "result"), "result", ToolCore.class);

    return new ToolBuildingRecipe(recipeId, group, item);
  }

  @Nullable
  @Override
  public ToolBuildingRecipe read(Identifier recipeId, PacketByteBuf buffer) {
    try {
      String group = buffer.readString(Short.MAX_VALUE);
      ToolCore result = RecipeHelper.readItem(buffer, ToolCore.class);

      return new ToolBuildingRecipe(recipeId, group, result);
    }
    catch (Exception e) {
      TConstruct.log.error("Error reading tool building recipe from packet.", e);
      throw e;
    }
  }

  @Override
  public void write(PacketByteBuf buffer, ToolBuildingRecipe recipe) {
    try {
      buffer.writeString(recipe.group);
      RecipeHelper.writeItem(buffer, recipe.output);
    }
    catch (Exception e) {
      TConstruct.log.error("Error writing tool building recipe to packet.", e);
      throw e;
    }
  }
}
