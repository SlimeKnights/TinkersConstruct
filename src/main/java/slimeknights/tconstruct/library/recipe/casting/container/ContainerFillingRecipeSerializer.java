package slimeknights.tconstruct.library.recipe.casting.container;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.tconstruct.TConstruct;

/**
 * Serializer for {@link ContainerFillingRecipe}
 * @param <T>  Recipe output class type
 */
@AllArgsConstructor
public class ContainerFillingRecipeSerializer<T extends ContainerFillingRecipe> implements RecipeSerializer<T> {
  private final IFactory<T> factory;

  @Override
  public T read(Identifier recipeId, JsonObject json) {
    String group = JsonHelper.getString(json, "group", "");
    int fluidAmount = JsonHelper.getInt(json, "fluid_amount");
    Item result = JsonHelper.getItem(json, "container");
    return this.factory.create(recipeId, group, fluidAmount, result);
  }

  @Nullable
  @Override
  public T read(Identifier recipeId, PacketByteBuf buffer) {
    try {
      String group = buffer.readString(Short.MAX_VALUE);
      int fluidAmount = buffer.readInt();
      Item result = RecipeHelper.readItem(buffer);
      return this.factory.create(recipeId, group, fluidAmount, result);
    } catch (Exception e) {
      TConstruct.log.error("Error reading container filling recipe from packet.", e);
      throw e;
    }
  }

  @Override
  public void write(PacketByteBuf buffer, T recipe) {
    try {
      buffer.writeString(recipe.group);
      buffer.writeInt(recipe.fluidAmount);
      RecipeHelper.writeItem(buffer, recipe.container);
    } catch (Exception e) {
      TConstruct.log.error("Error writing container filling recipe to packet.", e);
      throw e;
    }
  }

  /**
   * Interface representing a container filling recipe constructor
   * @param <T>  Recipe class type
   */
  public interface IFactory<T extends ContainerFillingRecipe> {
    T create(Identifier idIn, String groupIn, int fluidAmount, Item resultIn);
  }
}
