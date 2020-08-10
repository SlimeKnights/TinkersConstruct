package slimeknights.tconstruct.library.recipe.casting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;
import slimeknights.mantle.recipe.FluidIngredient;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Seralizer for {@link ItemCastingRecipe}.
 * @param <T>  Casting recipe class type
 */
@AllArgsConstructor
public class ItemCastingRecipeSerializer<T extends ItemCastingRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>>
  implements IRecipeSerializer<T> {

  private final IFactory<T> factory;

  @Override
  public T read(ResourceLocation recipeId, JsonObject json) {
    Ingredient cast = Ingredient.EMPTY;
    String group = JSONUtils.getString(json, "group", "");
    boolean consumed = false;
    boolean switchSlots = JSONUtils.getBoolean(json, "switch_slots", false);
    if (json.has("cast")) {
      cast = Ingredient.deserialize(JsonHelper.getElement(json, "cast"));
      consumed = JSONUtils.getBoolean(json, "cast_consumed", false);
    }
    FluidIngredient fluid = FluidIngredient.deserialize(json, "fluid");
    // result can either be "mod:name" or {"item": "mod:name"} Second form supports NBT
    ItemStack result;
    JsonElement resultElement = json.get("result");
    if (resultElement.isJsonPrimitive()) {
      result = new ItemStack(JSONUtils.getItem(resultElement, "result"));
    } else {
      result = CraftingHelper.getItemStack(JSONUtils.getJsonObject(json, "result"), true);
      result.setCount(1);
    }
    int coolingTime = JSONUtils.getInt(json, "cooling_time");
    return this.factory.create(recipeId, group, cast, fluid, result, coolingTime, consumed, switchSlots);
  }

  @Nullable
  @Override
  public T read(ResourceLocation recipeId, PacketBuffer buffer) {
    try {
      String group = buffer.readString(Short.MAX_VALUE);
      Ingredient cast = Ingredient.read(buffer);
      FluidIngredient fluid = FluidIngredient.read(buffer);
      ItemStack result = buffer.readItemStack();
      int coolingTime = buffer.readInt();
      boolean consumed = buffer.readBoolean();
      boolean switchSlots = buffer.readBoolean();
      return this.factory.create(recipeId, group, cast, fluid, result, coolingTime, consumed, switchSlots);
    } catch (Exception e) {
      TConstruct.log.error("Error reading item casting recipe from packet.", e);
      throw e;
    }
  }

  @Override
  public void write(PacketBuffer buffer, T recipe) {
    try {
      buffer.writeString(recipe.group);
      recipe.cast.write(buffer);
      recipe.fluid.write(buffer);
      buffer.writeItemStack(recipe.result);
      buffer.writeInt(recipe.coolingTime);
      buffer.writeBoolean(recipe.consumed);
      buffer.writeBoolean(recipe.switchSlots);
    } catch (Exception e) {
      TConstruct.log.error("Error writing item casting recipe to packet.", e);
      throw e;
    }
  }

  /**
   * Interface representing a item casting recipe constructor
   * @param <T>  Recipe class type
   */
  public interface IFactory<T extends ItemCastingRecipe> {
    T create(ResourceLocation idIn, String groupIn, @Nullable Ingredient cast, @Nonnull FluidIngredient fluidIn,
             ItemStack result, int coolingTime, boolean consumed, boolean switchSlots);
  }
}
