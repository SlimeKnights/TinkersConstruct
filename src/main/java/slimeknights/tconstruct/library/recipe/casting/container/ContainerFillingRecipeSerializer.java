package slimeknights.tconstruct.library.recipe.casting.container;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import slimeknights.mantle.recipe.helper.LoggingRecipeSerializer;
import slimeknights.mantle.recipe.helper.RecipeHelper;

import javax.annotation.Nullable;

/**
 * Serializer for {@link ContainerFillingRecipe}
 * @param <T>  Recipe output class type
 */
@AllArgsConstructor
public class ContainerFillingRecipeSerializer<T extends ContainerFillingRecipe> extends LoggingRecipeSerializer<T> {
  private final ContainerFillingRecipeSerializer.IFactory<T> factory;

  @Override
  public T fromJson(ResourceLocation recipeId, JsonObject json) {
    String group = GsonHelper.getAsString(json, "group", "");
    int fluidAmount = GsonHelper.getAsInt(json, "fluid_amount");
    Item result = GsonHelper.getAsItem(json, "container");
    return this.factory.create(recipeId, group, fluidAmount, result);
  }

  @Nullable
  @Override
  protected T fromNetworkSafe(ResourceLocation recipeId, FriendlyByteBuf buffer) {
    String group = buffer.readUtf(Short.MAX_VALUE);
    int fluidAmount = buffer.readInt();
    Item result = RecipeHelper.readItem(buffer);
    return this.factory.create(recipeId, group, fluidAmount, result);
  }

  @Override
  protected void toNetworkSafe(FriendlyByteBuf buffer, T recipe) {
    buffer.writeUtf(recipe.group);
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
