package slimeknights.tconstruct.library.recipe.partbuilder;

import com.google.gson.JsonObject;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.RecipeUtil;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;

import javax.annotation.Nullable;

public class PartRecipeSerializer<T extends PartRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {

  private final IFactory<T> factory;

  public PartRecipeSerializer(IFactory<T> factory) {
    this.factory = factory;
  }

  @Override
  public T read(ResourceLocation recipeId, JsonObject json) {
    String group = JSONUtils.getString(json, "group", "");
    ResourceLocation pattern = new ResourceLocation(JSONUtils.getString(json, "pattern"));
    int cost = JSONUtils.getInt(json, "cost");

    // output fetch as a material item, its an error if it does not implement that interface
    JsonObject output = JSONUtils.getJsonObject(json, "output");
    IMaterialItem item = RecipeUtil.deserializeMaterialItem(JSONUtils.getString(output, "item"), "output");
    int count = JSONUtils.getInt(output, "count", 1);

    return this.factory.create(recipeId, group, pattern, cost, item, count);
  }

  @Nullable
  @Override
  public T read(ResourceLocation recipeId, PacketBuffer buffer) {
    try {
      String group = buffer.readString(32767);
      ResourceLocation pattern = new ResourceLocation(buffer.readString(32767));
      int cost = buffer.readInt();
      // output must be a material item
      IMaterialItem item = RecipeUtil.readItem(buffer, IMaterialItem.class);
      int count = buffer.readByte();
      return this.factory.create(recipeId, group, pattern, cost, item, count);
    } catch (Exception e) {
      TConstruct.log.error("Error reading material recipe from packet.", e);
      throw e;
    }
  }

  @Override
  public void write(PacketBuffer buffer, T recipe) {
    try {
      buffer.writeString(recipe.group);
      buffer.writeString(recipe.pattern.toString());
      buffer.writeInt(recipe.cost);
      RecipeUtil.writeItem(buffer, recipe.output);
      buffer.writeByte(recipe.outputCount);
    } catch (Exception e) {
      TConstruct.log.error("Error writing material recipe to packet.", e);
      throw e;
    }
  }

  public interface IFactory<T extends PartRecipe> {
    T create(ResourceLocation id, String group, ResourceLocation pattern, int cost, IMaterialItem output, int outputCount);
  }
}
