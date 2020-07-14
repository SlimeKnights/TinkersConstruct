package slimeknights.tconstruct.library.recipe.casting;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.AllArgsConstructor;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import slimeknights.tconstruct.TConstruct;

import javax.annotation.Nullable;

@AllArgsConstructor
public class ContainerFillingRecipeSerializer<T extends ContainerFillingRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>>
  implements IRecipeSerializer<T> {
  private final ContainerFillingRecipeSerializer.IFactory<T> factory;

  @Override
  public T read(ResourceLocation recipeId, JsonObject json) {
    String group = JSONUtils.getString(json, "group", "");
    int fluidAmount = JSONUtils.getInt(json, "fluid_amount");
    String itemName = JSONUtils.getString(json, "container");
    Item result = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));
    if (result == null) {
      throw new JsonSyntaxException("Unknown item '" + itemName + "'");
    }
    return this.factory.create(recipeId, group, fluidAmount, result);
  }

  @Nullable
  @Override
  public T read(ResourceLocation recipeId, PacketBuffer buffer) {
    try {
      String group = buffer.readString(Short.MAX_VALUE);
      int fluidAmount = buffer.readInt();
      int itemId = buffer.readVarInt();
      Item result = Item.getItemById(itemId);
      return this.factory.create(recipeId, group, fluidAmount, result);
    } catch (Exception e) {
      TConstruct.log.error("Error reading container filling recipe from packet.", e);
      throw e;
    }
  }

  @Override
  public void write(PacketBuffer buffer, T recipe) {
    try {
      buffer.writeString(recipe.group);
      buffer.writeInt(recipe.fluidAmount);
      buffer.writeInt(Item.getIdFromItem(recipe.container.asItem()));
    } catch (Exception e) {
      TConstruct.log.error("Error writing container filling recipe to packet.", e);
      throw e;
    }
  }

  public interface IFactory<T extends ContainerFillingRecipe> {
    T create(ResourceLocation idIn, String groupIn, int fluidAmount, Item resultIn);
  }
}
