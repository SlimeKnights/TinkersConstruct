package slimeknights.tconstruct.library.recipe.casting;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

/**
 * Casting recipe that takes a fluid and optional cast and outputs an item
 */
public abstract class ItemCastingRecipe extends AbstractCastingRecipe implements IDisplayableCastingRecipe {
  @Getter
  protected final FluidIngredient fluid;
  protected final ItemOutput result;
  @Getter
  protected final int coolingTime;
  public ItemCastingRecipe(RecipeType<?> type, ResourceLocation id, String group, Ingredient cast, FluidIngredient fluid, ItemOutput result, int coolingTime, boolean consumed, boolean switchSlots) {
    super(type, id, group, cast, consumed, switchSlots);
    this.fluid = fluid;
    this.result = result;
    this.coolingTime = coolingTime;
  }

  @Override
  public int getFluidAmount(ICastingContainer inv) {
    return this.fluid.getAmount(inv.getFluid());
  }

  @Override
  public boolean matches(ICastingContainer inv, Level worldIn) {
    return getCast().test(inv.getStack()) && fluid.test(inv.getFluid());
  }

  @Override
  public ItemStack getResultItem() {
    return this.result.get();
  }

  @Override
  public int getCoolingTime(ICastingContainer inv) {
    return this.coolingTime;
  }


  /* JEI */

  @Override
  public boolean hasCast() {
    return cast != Ingredient.EMPTY;
  }

  @Override
  public List<ItemStack> getCastItems() {
    return Arrays.asList(cast.getItems());
  }

  @Override
  public ItemStack getOutput() {
    return this.result.get();
  }

  /**
   * Gets a list of valid fluid inputs for this recipe, for display in JEI
   * @return  List of fluids
   */
  @Override
  public List<FluidStack> getFluids() {
    return this.fluid.getFluids();
  }

  /** Subclass for basin recipes */
  public static class Basin extends ItemCastingRecipe {
    public Basin(ResourceLocation id, String group, Ingredient cast, FluidIngredient fluid, ItemOutput result, int coolingTime, boolean consumed, boolean switchSlots) {
      super(TinkerRecipeTypes.CASTING_BASIN.get(), id, group, cast, fluid, result, coolingTime, consumed, switchSlots);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.basinRecipeSerializer.get();
    }
  }

  /** Subclass for table recipes */
  public static class Table extends ItemCastingRecipe {
    public Table(ResourceLocation id, String group, Ingredient cast, FluidIngredient fluid, ItemOutput result, int coolingTime, boolean consumed, boolean switchSlots) {
      super(TinkerRecipeTypes.CASTING_TABLE.get(), id, group, cast, fluid, result, coolingTime, consumed, switchSlots);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.tableRecipeSerializer.get();
    }
  }

  // TODO 1.19: migrate serializer to work like PotionCastingRecipe
  @AllArgsConstructor
  public static class Serializer<T extends ItemCastingRecipe> extends AbstractCastingRecipe.Serializer<T> {
    private final IFactory<T> factory;

    @Override
    protected T create(ResourceLocation idIn, String groupIn, @Nullable Ingredient cast, boolean consumed, boolean switchSlots, JsonObject json) {
      FluidIngredient fluid = FluidIngredient.deserialize(json, "fluid");
      ItemOutput output = ItemOutput.fromJson(JsonHelper.getElement(json, "result"));
      int coolingTime = GsonHelper.getAsInt(json, "cooling_time");
      return factory.create(idIn, groupIn, cast, fluid, output, coolingTime, consumed, switchSlots);
    }

    @Override
    protected T create(ResourceLocation idIn, String groupIn, @Nullable Ingredient cast, boolean consumed, boolean switchSlots, FriendlyByteBuf buffer) {
      FluidIngredient fluid = FluidIngredient.read(buffer);
      ItemOutput result = ItemOutput.read(buffer);
      int coolingTime = buffer.readInt();
      return factory.create(idIn, groupIn, cast, fluid, result, coolingTime, consumed, switchSlots);
    }

    @Override
    public void writeExtra(FriendlyByteBuf buffer, T recipe) {
      recipe.fluid.write(buffer);
      recipe.result.write(buffer);
      buffer.writeInt(recipe.coolingTime);
    }
  }

  /**
   * Interface representing a item casting recipe constructor
   * @param <T>  Recipe class type
   */
  public interface IFactory<T extends AbstractCastingRecipe> {
    /** Creates a new instance of this factory */
    T create(ResourceLocation idIn, String groupIn, @Nullable Ingredient cast, FluidIngredient fluidIn, ItemOutput output, int coolingTime, boolean consumed, boolean switchSlots);
  }
}
