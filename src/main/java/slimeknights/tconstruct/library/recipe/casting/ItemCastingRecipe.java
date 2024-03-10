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

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/** Casting recipe that takes a fluid and optional cast and outputs an item. */
public class ItemCastingRecipe extends AbstractCastingRecipe implements IDisplayableCastingRecipe {
  @Getter
  private final RecipeSerializer<?> serializer;
  @Getter
  protected final FluidIngredient fluid;
  protected final ItemOutput result;
  @Getter
  protected final int coolingTime;
  public ItemCastingRecipe(RecipeType<?> type, RecipeSerializer<? extends ItemCastingRecipe> serializer, ResourceLocation id, String group, @Nullable Ingredient cast, FluidIngredient fluid, ItemOutput result, int coolingTime, boolean consumed, boolean switchSlots) {
    super(type, id, group, cast, consumed, switchSlots);
    this.serializer = serializer;
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

  @AllArgsConstructor
  public static class Serializer extends AbstractCastingRecipe.Serializer<ItemCastingRecipe> {
    protected final Supplier<RecipeType<ICastingRecipe>> type;

    /** Creates a recipe instance */
    protected ItemCastingRecipe create(ResourceLocation id, String group, @Nullable Ingredient cast, FluidIngredient fluid, ItemOutput result, int coolingTime, boolean consumed, boolean switchSlots) {
      return new ItemCastingRecipe(type.get(), this, id, group, cast, fluid, result, coolingTime, consumed, switchSlots);
    }

    @Override
    protected ItemCastingRecipe create(ResourceLocation idIn, String groupIn, @Nullable Ingredient cast, boolean consumed, boolean switchSlots, JsonObject json) {
      FluidIngredient fluid = FluidIngredient.deserialize(json, "fluid");
      ItemOutput output = ItemOutput.fromJson(JsonHelper.getElement(json, "result"));
      int coolingTime = GsonHelper.getAsInt(json, "cooling_time");
      return create(idIn, groupIn, cast, fluid, output, coolingTime, consumed, switchSlots);
    }

    @Override
    protected ItemCastingRecipe create(ResourceLocation idIn, String groupIn, @Nullable Ingredient cast, boolean consumed, boolean switchSlots, FriendlyByteBuf buffer) {
      FluidIngredient fluid = FluidIngredient.read(buffer);
      ItemOutput result = ItemOutput.read(buffer);
      int coolingTime = buffer.readInt();
      return create(idIn, groupIn, cast, fluid, result, coolingTime, consumed, switchSlots);
    }

    @Override
    public void writeExtra(FriendlyByteBuf buffer, ItemCastingRecipe recipe) {
      recipe.fluid.write(buffer);
      recipe.result.write(buffer);
      buffer.writeInt(recipe.coolingTime);
    }
  }
}
