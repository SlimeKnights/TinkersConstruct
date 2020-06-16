package slimeknights.tconstruct.tables.recipe.part;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import slimeknights.tconstruct.TConstruct;

import javax.annotation.Nullable;

public class PartRecipeSerializer<T extends PartRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {

  private final IFactory<T> factory;

  public PartRecipeSerializer(IFactory<T> factory) {
    this.factory = factory;
  }

  @Override
  public T read(ResourceLocation recipeId, JsonObject json) {
    String group = JSONUtils.getString(json, "group", "");
    String pattern = JSONUtils.getString(json, "pattern", "");
    int cost = JSONUtils.getInt(json, "cost", 0);
    ItemStack output = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "output"));

    return this.factory.create(recipeId, group, pattern, cost, output);
  }

  @Nullable
  @Override
  public T read(ResourceLocation recipeId, PacketBuffer buffer) {
    try {
      String group = buffer.readString(32767);
      String pattern = buffer.readString(32767);
      int cost = buffer.readInt();
      ItemStack output = buffer.readItemStack();

      return this.factory.create(recipeId, group, pattern, cost, output);
    } catch (Exception e) {
      TConstruct.log.error("Error reading material recipe from packet.", e);
      throw e;
    }
  }

  @Override
  public void write(PacketBuffer buffer, T recipe) {
    try {
      buffer.writeString(recipe.group);
      buffer.writeString(recipe.pattern);
      buffer.writeInt(recipe.cost);
      buffer.writeItemStack(recipe.output);
    } catch (Exception e) {
      TConstruct.log.error("Error writing material recipe to packet.", e);
      throw e;
    }
  }

  public interface IFactory<T extends PartRecipe> {

    T create(ResourceLocation id, String group, String pattern, int cost, ItemStack output);
  }
}
