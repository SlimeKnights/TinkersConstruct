package slimeknights.tconstruct.library.recipe.casting;

import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import slimeknights.mantle.recipe.FluidIngredient;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.recipe.ICastingInventory;

import org.jetbrains.annotations.Nullable;
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
  public ItemCastingRecipe(RecipeType<?> type, Identifier id, String group, Ingredient cast, FluidIngredient fluid, ItemOutput result, int coolingTime, boolean consumed, boolean switchSlots) {
    super(type, id, group, cast, consumed, switchSlots);
    this.fluid = fluid;
    this.result = result;
    this.coolingTime = coolingTime;
  }

  @Override
  public int getFluidAmount(ICastingInventory inv) {
    return this.fluid.getAmount(FluidKeys.get(inv.getFluid())).asInt(1000);
  }

  @Override
  public boolean matches(ICastingInventory inv, World worldIn) {
    return getCast().test(inv.getStack()) && fluid.test(FluidKeys.get(inv.getFluid()));
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
    return Arrays.asList(cast.getMatchingStacksClient());
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
  public List<FluidVolume> getFluids() {
    return this.fluid.getFluids();
  }

  /** Subclass for basin recipes */
  public static class Basin extends ItemCastingRecipe {
    public Basin(Identifier id, String group, Ingredient cast, FluidIngredient fluid, ItemOutput result, int coolingTime, boolean consumed, boolean switchSlots) {
      super(RecipeTypes.CASTING_BASIN, id, group, cast, fluid, result, coolingTime, consumed, switchSlots);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.basinRecipeSerializer;
    }
  }

  /** Subclass for table recipes */
  public static class Table extends ItemCastingRecipe {
    public Table(Identifier id, String group, Ingredient cast, FluidIngredient fluid, ItemOutput result, int coolingTime, boolean consumed, boolean switchSlots) {
      super(RecipeTypes.CASTING_TABLE, id, group, cast, fluid, result, coolingTime, consumed, switchSlots);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.tableRecipeSerializer;
    }
  }

  @AllArgsConstructor
  public static class Serializer<T extends ItemCastingRecipe> extends AbstractCastingRecipe.Serializer<T> {
    private final IFactory<T> factory;

    @Override
    protected T create(Identifier idIn, String groupIn, @Nullable Ingredient cast, boolean consumed, boolean switchSlots, JsonObject json) {
      FluidIngredient fluid = FluidIngredient.deserialize(json, "fluid");
      ItemOutput output = ItemOutput.fromJson(JsonHelper.getElement(json, "result"));
      int coolingTime = net.minecraft.util.JsonHelper.getInt(json, "cooling_time");
      return factory.create(idIn, groupIn, cast, fluid, output, coolingTime, consumed, switchSlots);
    }

    @Override
    protected T create(Identifier idIn, String groupIn, @Nullable Ingredient cast, boolean consumed, boolean switchSlots, PacketByteBuf buffer) {
      FluidIngredient fluid = FluidIngredient.read(buffer);
      ItemOutput result = ItemOutput.read(buffer);
      int coolingTime = buffer.readInt();
      return factory.create(idIn, groupIn, cast, fluid, result, coolingTime, consumed, switchSlots);
    }

    @Override
    public void writeExtra(PacketByteBuf buffer, T recipe) {
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
    T create(Identifier idIn, String groupIn, @Nullable Ingredient cast, FluidIngredient fluidIn, ItemOutput output, int coolingTime, boolean consumed, boolean switchSlots);
  }
}
