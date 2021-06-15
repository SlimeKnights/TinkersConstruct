package slimeknights.tconstruct.library.recipe.casting.container;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import net.minecraft.item.Item;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.tconstruct.common.recipe.LoggingRecipeSerializer;

import javax.annotation.Nullable;

/**
 * Serializer for {@link ContainerFillingRecipe}
 * @param <T>  Recipe output class type
 */
@AllArgsConstructor
public class ContainerFillingRecipeSerializer<T extends ContainerFillingRecipe> extends LoggingRecipeSerializer<T> {
  private final ContainerFillingRecipeSerializer.IFactory<T> factory;

  @Override
  public T read(ResourceLocation recipeId, JsonObject json) {
    String group = JSONUtils.getString(json, "group", "");
    int fluidAmount = JSONUtils.getInt(json, "fluid_amount");
    Item result = JSONUtils.getItem(json, "container");
    return this.factory.create(recipeId, group, fluidAmount, result);
  }

  @Nullable
  @Override
  protected T readSafe(ResourceLocation recipeId, PacketBuffer buffer) {
    String group = buffer.readString(Short.MAX_VALUE);
    int fluidAmount = buffer.readInt();
    Item result = RecipeHelper.readItem(buffer);
    return this.factory.create(recipeId, group, fluidAmount, result);
  }

  @Override
  protected void writeSafe(PacketBuffer buffer, T recipe) {
    buffer.writeString(recipe.group);
    buffer.writeInt(recipe.fluidAmount);
    RecipeHelper.writeItem(buffer, recipe.container);
  }

  /**
   * Interface representing a container filling recipe constructor
   * @param <T>  Recipe class type
   */
  public interface IFactory<T extends ContainerFillingRecipe> {
    T create(ResourceLocation idIn, String groupIn, int fluidAmount, Item resultIn);
  }
}
