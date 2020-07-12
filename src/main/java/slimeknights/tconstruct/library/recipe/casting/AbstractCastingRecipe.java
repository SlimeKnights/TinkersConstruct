package slimeknights.tconstruct.library.recipe.casting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.recipe.FluidIngredient;
import slimeknights.tconstruct.smeltery.recipe.ICastingInventory;

import java.util.List;

@AllArgsConstructor
public abstract class AbstractCastingRecipe implements IRecipe<ICastingInventory> {
  @Getter
  protected final IRecipeType<?> type;
  @Getter
  protected final ResourceLocation id;
  @Getter
  protected final String group;
  /** 'cast' item for recipe (doesn't have to be an actual 'cast') */
  @Getter
  protected final Ingredient cast;
  /** FluidStack for recipe (fluid and amount) */
  @Getter
  protected final FluidIngredient fluid;
  protected final ItemStack result;
  /** How long it takes to cool off */
  @Getter
  protected final int coolingTime;
  /** Whether the cast is consumed when cast */
  @Getter
  protected final boolean consumed;
  protected final boolean switchSlots;

  @Override
  public ItemStack getCraftingResult(ICastingInventory inv) {
    return this.result.copy();
  }

  @Override
  public ItemStack getRecipeOutput() {
    return this.result;
  }

  @Override
  public abstract IRecipeSerializer<?> getSerializer();

  /**
   * Does this recipe's output get put into the input slot
   * @return  Whether to switch to input after casting
   */
  public boolean switchSlots() {
    return this.switchSlots;
  }

  /**
   * Keeps casting recipes out of the recipe book.
   */
  @Override
  public boolean isDynamic() {
    return true;
  }

  @Override
  public boolean canFit(int width, int height) {
    return true;
  }

  @Override
  public boolean matches(ICastingInventory inv, World worldIn) {
    return this.cast.test(inv.getStack()) && this.fluid.test(inv.getFluid());
  }

  /**
   * Gets the amount of fluid required for this recipe
   * @param inv  Inventory instance
   * @return  Fluid amount when using the fluid in the inventory
   */
  public int getFluidAmount(ICastingInventory inv) {
    return this.fluid.getAmount(inv.getFluid());
  }

  @Override
  public NonNullList<Ingredient> getIngredients() {
    return NonNullList.from(Ingredient.EMPTY, this.cast);
  }

  /**
   * Gets a list of valid fluid inputs for this recipe
   * @return  List of fluids
   */
  public List<FluidStack> getFluids() {
    return this.fluid.getFluids();
  }

  /**
   * Calculates the cooling time for a recipe based on the amount and temperature
   * @param temperature  Temperature baseline in celsius
   * @param amount       Output amount
   * @return  Cooling time based on the given inputs
   */
  public static int calcCoolingTime(int temperature, int amount) {
    return 24 + (temperature * amount) / 1600;
  }

  /**
   * Calculates the cooling time for a recipe based on the fluid input
   * @param fluid  Fluid result
   * @return  Temperature for the recipe in celsius
   */
  public static int calcCoolingTime(FluidStack fluid) {
    return calcCoolingTime(fluid.getFluid().getAttributes().getTemperature(fluid) - 300, fluid.getAmount());
  }
}
