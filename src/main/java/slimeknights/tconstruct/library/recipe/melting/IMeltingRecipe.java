package slimeknights.tconstruct.library.recipe.melting;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.inventory.ISingleItemInventory;

public interface IMeltingRecipe extends IRecipe<ISingleItemInventory> {
  /**
   * Gets a new instance of the output stack for this recipe
   * @param inv  Input inventory
   * @return  Output stack
   */
  FluidStack getOutput(ISingleItemInventory inv);

  /**
   * Gets the minimum temperatue to melt this item. Doubles as the time
   * @param inv  Inventory instance
   * @return  Recipe temperature
   */
  int getTemperature(ISingleItemInventory inv);


  /* Recipe data */

  @Override
  default IRecipeType<?> getType() {
    return RecipeTypes.MELTING;
  }

  @Override
  default boolean isDynamic() {
    return true;
  }


  /* Required methods */

  /** @deprecated unused */
  @Deprecated
  @Override
  default boolean canFit(int width, int height) {
    return true;
  }

  /** @deprecated unsupported */
  @Deprecated
  @Override
  default ItemStack getRecipeOutput() {
    return ItemStack.EMPTY;
  }

  /** @deprecated unsupported */
  @Deprecated
  @Override
  default ItemStack getCraftingResult(ISingleItemInventory inv) {
    return ItemStack.EMPTY;
  }


  /* Utils */

  double LOG9_2 = 0.31546487678;

  /**
   * Calculates the temperature for a recipe based on the amount and temperature
   * @param temperature  Temperature baseline
   * @param amount       Output amount
   * @return
   */
  static int calcTemperature(int temperature, int amount) {
    int base = MaterialValues.VALUE_Block;
    int maxTemp = Math.max(0, temperature); // we use 0 as baseline, not 300
    double f = (double) amount / (double) base;

    // we calculate 2^log9(f), which effectively gives us 2^(1 for each multiple of 9)
    // so 1 = 1, 9 = 2, 81 = 4, 1/9 = 1/2, 1/81 = 1/4 etc
    // we simplify it to f^log9(2) to make calculation simpler
    f = Math.pow(f, LOG9_2);

    return (int) (f * (double) maxTemp);
  }

  /**
   * Calculates the temperature for a recipe based on the fluid result
   * @param fluid  Fluid result
   * @return  Temperature for the recipe in celsius
   */
  static int calcTemperature(FluidStack fluid) {
    return calcTemperature(fluid.getFluid().getAttributes().getTemperature(fluid) - 300, fluid.getAmount());
  }
}
