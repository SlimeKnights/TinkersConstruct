package slimeknights.tconstruct.library.recipe.casting;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.FluidIngredient;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.recipe.ICastingInventory;

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
  public ItemCastingRecipe(IRecipeType<?> type, ResourceLocation id, String group, Ingredient cast, FluidIngredient fluid, ItemOutput result, int coolingTime, boolean consumed, boolean switchSlots) {
    super(type, id, group, cast, consumed, switchSlots);
    this.fluid = fluid;
    this.result = result;
    this.coolingTime = coolingTime;
  }

  @Override
  public int getFluidAmount(ICastingInventory inv) {
    return this.fluid.getAmount(inv.getFluid());
  }

  @Override
  public boolean matches(ICastingInventory inv, World worldIn) {
    return getCast().test(inv.getStack()) && fluid.test(inv.getFluid());
  }

  @Override
  public ItemStack getRecipeOutput() {
    return this.result.get();
  }

  @Override
  public int getCoolingTime(ICastingInventory inv) {
    return this.coolingTime;
  }


  /* JEI */

  @Override
  public boolean hasCast() {
    return cast != Ingredient.EMPTY;
  }

  @Override
  public List<ItemStack> getCastItems() {
    return Arrays.asList(cast.getMatchingStacks());
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
      super(RecipeTypes.CASTING_BASIN, id, group, cast, fluid, result, coolingTime, consumed, switchSlots);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.basinRecipeSerializer.get();
    }
  }

  /** Subclass for table recipes */
  public static class Table extends ItemCastingRecipe {
    public Table(ResourceLocation id, String group, Ingredient cast, FluidIngredient fluid, ItemOutput result, int coolingTime, boolean consumed, boolean switchSlots) {
      super(RecipeTypes.CASTING_TABLE, id, group, cast, fluid, result, coolingTime, consumed, switchSlots);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.tableRecipeSerializer.get();
    }
  }

  @AllArgsConstructor
  public static class Serializer<T extends ItemCastingRecipe> extends AbstractCastingRecipe.Serializer<T> {
    private final IFactory<T> factory;

    @Override
    protected T create(ResourceLocation idIn, String groupIn, @Nullable Ingredient cast, boolean consumed, boolean switchSlots, JsonObject json) {
      FluidIngredient fluid = FluidIngredient.deserialize(json, "fluid");
      ItemOutput output = ItemOutput.fromJson(JsonHelper.getElement(json, "result"));
      int coolingTime = JSONUtils.getInt(json, "cooling_time");
      return factory.create(idIn, groupIn, cast, fluid, output, coolingTime, consumed, switchSlots);
    }

    @Override
    protected T create(ResourceLocation idIn, String groupIn, @Nullable Ingredient cast, boolean consumed, boolean switchSlots, PacketBuffer buffer) {
      FluidIngredient fluid = FluidIngredient.read(buffer);
      ItemOutput result = ItemOutput.read(buffer);
      int coolingTime = buffer.readInt();
      return factory.create(idIn, groupIn, cast, fluid, result, coolingTime, consumed, switchSlots);
    }

    @Override
    public void writeExtra(PacketBuffer buffer, T recipe) {
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
