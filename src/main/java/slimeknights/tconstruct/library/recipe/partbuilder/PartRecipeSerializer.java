package slimeknights.tconstruct.library.recipe.partbuilder;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.tconstruct.common.recipe.LoggingRecipeSerializer;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;

import javax.annotation.Nullable;

public class PartRecipeSerializer extends LoggingRecipeSerializer<PartRecipe> {
  @Override
  public PartRecipe read(ResourceLocation recipeId, JsonObject json) {
    String group = JSONUtils.getString(json, "group", "");
    Pattern pattern = new Pattern(JSONUtils.getString(json, "pattern"));
    int cost = JSONUtils.getInt(json, "cost");

    // output fetch as a material item, its an error if it does not implement that interface
    JsonObject output = JSONUtils.getJsonObject(json, "result");
    IMaterialItem item = RecipeHelper.deserializeItem(JSONUtils.getString(output, "item"), "result", IMaterialItem.class);
    int count = JSONUtils.getInt(output, "count", 1);

    return new PartRecipe(recipeId, group, pattern, cost, item, count);
  }

  @Nullable
  @Override
  protected PartRecipe readSafe(ResourceLocation recipeId, PacketBuffer buffer) {
    String group = buffer.readString(Short.MAX_VALUE);
    Pattern pattern = new Pattern(buffer.readString(Short.MAX_VALUE));
    int cost = buffer.readInt();
    // output must be a material item
    IMaterialItem item = RecipeHelper.readItem(buffer, IMaterialItem.class);
    int count = buffer.readByte();
    return new PartRecipe(recipeId, group, pattern, cost, item, count);
  }

  @Override
  protected void writeSafe(PacketBuffer buffer, PartRecipe recipe) {
    buffer.writeString(recipe.group);
    buffer.writeString(recipe.pattern.toString());
    buffer.writeInt(recipe.cost);
    RecipeHelper.writeItem(buffer, recipe.output);
    buffer.writeByte(recipe.outputCount);
  }
}
