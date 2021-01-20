package slimeknights.tconstruct.library.recipe.casting;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.recipe.FluidIngredient;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.TagPreference;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;

/**
 * Recipe with a preferred output from a tag
 */
public abstract class PreferenceCastingRecipe extends AbstractCastingRecipe {
  private final TagPreference.Entry<Item> result;
  public PreferenceCastingRecipe(IRecipeType<?> type, ResourceLocation id, String group, Ingredient cast, FluidIngredient fluid, TagPreference.Entry<Item> result, int coolingTime, boolean consumed, boolean switchSlots) {
    super(type, id, group, cast, fluid, coolingTime, consumed, switchSlots);
    this.result = result;
  }

  @Override
  public ItemStack getRecipeOutput() {
    return this.result.map(ItemStack::new).orElse(ItemStack.EMPTY);
  }

  /** Subclass for basin recipes */
  public static class Basin extends PreferenceCastingRecipe {
    public Basin(ResourceLocation id, String group, Ingredient cast, FluidIngredient fluid, TagPreference.Entry<Item> result, int coolingTime, boolean consumed, boolean switchSlots) {
      super(RecipeTypes.CASTING_BASIN, id, group, cast, fluid, result, coolingTime, consumed, switchSlots);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.basinPreferenceSerializer.get();
    }
  }

  /** Subclass for table recipes */
  public static class Table extends PreferenceCastingRecipe {
    public Table(ResourceLocation id, String group, Ingredient cast, FluidIngredient fluid, TagPreference.Entry<Item> result, int coolingTime, boolean consumed, boolean switchSlots) {
      super(RecipeTypes.CASTING_TABLE, id, group, cast, fluid, result, coolingTime, consumed, switchSlots);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.tablePreferenceSerializer.get();
    }
  }

  @AllArgsConstructor
  public static class Serializer<T extends AbstractCastingRecipe> extends AbstractCastingRecipe.Serializer<T> {
    private final IFactory<T> factory;
    private final ItemCastingRecipe.IFactory<T> itemFactory;

    @Override
    protected T create(ResourceLocation idIn, String groupIn, @Nullable Ingredient cast, FluidIngredient fluidIn, int coolingTime, boolean consumed, boolean switchSlots, JsonObject json) {
      TagPreference.Entry<Item> entry = TagPreference.getItems().deserialize(JSONUtils.getJsonObject(json, "result"));
      return factory.create(idIn, groupIn, cast, fluidIn, entry, coolingTime, consumed, switchSlots);
    }

    @Override
    protected T create(ResourceLocation idIn, String groupIn, @Nullable Ingredient cast, FluidIngredient fluidIn, int coolingTime, boolean consumed, boolean switchSlots, PacketBuffer buffer) {
      // read from buffer into an item casting recipe
      ItemStack result = buffer.readItemStack();
      return itemFactory.create(idIn, groupIn, cast, fluidIn, result, coolingTime, consumed, switchSlots);
    }

    @Override
    public void writeExtra(PacketBuffer buffer, T recipe) {
      buffer.writeItemStack(recipe.getRecipeOutput());
    }
  }

  /**
   * Interface representing a item casting recipe constructor
   * @param <T>  Recipe class type
   */
  public interface IFactory<T extends AbstractCastingRecipe> {
    /** Creates a new instance of this factory */
    T create(ResourceLocation idIn, String groupIn, @Nullable Ingredient cast, FluidIngredient fluidIn, TagPreference.Entry<Item> output, int coolingTime, boolean consumed, boolean switchSlots);
  }
}
