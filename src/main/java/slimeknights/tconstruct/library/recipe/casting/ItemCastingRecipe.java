package slimeknights.tconstruct.library.recipe.casting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import slimeknights.mantle.recipe.FluidIngredient;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;

/**
 * Casting recipe that takes a fluid and optional cast and outputs an item
 */
public abstract class ItemCastingRecipe extends AbstractCastingRecipe {
  protected final ItemStack result;
  public ItemCastingRecipe(IRecipeType<?> type, ResourceLocation id, String group, Ingredient cast, FluidIngredient fluid, ItemStack result, int coolingTime, boolean consumed, boolean switchSlots) {
    super(type, id, group, cast, fluid, coolingTime, consumed, switchSlots);
    this.result = result;
  }

  @Override
  public ItemStack getRecipeOutput() {
    return this.result;
  }

  /** Subclass for basin recipes */
  public static class Basin extends ItemCastingRecipe {
    public Basin(ResourceLocation id, String group, Ingredient cast, FluidIngredient fluid, ItemStack result, int coolingTime, boolean consumed, boolean switchSlots) {
      super(RecipeTypes.CASTING_BASIN, id, group, cast, fluid, result, coolingTime, consumed, switchSlots);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.basinRecipeSerializer.get();
    }
  }

  /** Subclass for table recipes */
  public static class Table extends ItemCastingRecipe {
    public Table(ResourceLocation id, String group, Ingredient cast, FluidIngredient fluid, ItemStack result, int coolingTime, boolean consumed, boolean switchSlots) {
      super(RecipeTypes.CASTING_TABLE, id, group, cast, fluid, result, coolingTime, consumed, switchSlots);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.tableRecipeSerializer.get();
    }
  }

  /**
   * Reads the result from the given JSON
   * @param parent  Parent JSON
   * @param name    Tag name
   * @return  Item stack result
   * @throws com.google.gson.JsonSyntaxException If the syntax is invalid
   */
  public static ItemStack deseralizeResultItem(JsonObject parent, String name) {
    JsonElement element = JsonHelper.getElement(parent, name);
    if (element.isJsonPrimitive()) {
      return new ItemStack(JSONUtils.getItem(element, name));
    } else {
      ItemStack result = CraftingHelper.getItemStack(JSONUtils.getJsonObject(element, name), true);
      result.setCount(1);
      return result;
    }
  }

  @AllArgsConstructor
  public static class Serializer<T extends ItemCastingRecipe> extends AbstractCastingRecipe.Serializer<T> {
    private final IFactory<T> factory;

    @Override
    protected T create(ResourceLocation idIn, String groupIn, @Nullable Ingredient cast, FluidIngredient fluidIn, int coolingTime, boolean consumed, boolean switchSlots, JsonObject json) {
      // result can either be "mod:name" or {"item": "mod:name"} Second form supports NBT
      ItemStack result = deseralizeResultItem(json, "result");
      return factory.create(idIn, groupIn, cast, fluidIn, result, coolingTime, consumed, switchSlots);
    }

    @Override
    protected T create(ResourceLocation idIn, String groupIn, @Nullable Ingredient cast, FluidIngredient fluidIn, int coolingTime, boolean consumed, boolean switchSlots, PacketBuffer buffer) {
      ItemStack result = buffer.readItemStack();
      return factory.create(idIn, groupIn, cast, fluidIn, result, coolingTime, consumed, switchSlots);
    }

    @Override
    public void writeExtra(PacketBuffer buffer, T recipe) {
      buffer.writeItemStack(recipe.result);
    }
  }

  /**
   * Interface representing a item casting recipe constructor
   * @param <T>  Recipe class type
   */
  public interface IFactory<T extends AbstractCastingRecipe> {
    /** Creates a new instance of this factory */
    T create(ResourceLocation idIn, String groupIn, @Nullable Ingredient cast, FluidIngredient fluidIn, ItemStack output, int coolingTime, boolean consumed, boolean switchSlots);
  }
}
