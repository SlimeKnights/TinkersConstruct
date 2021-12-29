package slimeknights.tconstruct.library.recipe.melting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import slimeknights.mantle.recipe.ICustomOutputRecipe;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

/**
 * Common interface for all melting recipes
 */
public interface IMeltingRecipe extends ICustomOutputRecipe<IMeltingInventory> {
  /**
   * Gets a new instance of the output stack for this recipe
   * @param inv  Input inventory
   * @return  Output stack
   */
  FluidStack getOutput(IMeltingInventory inv);

  /**
   * Gets the minimum temperature to melt this item
   * @param inv  Inventory instance
   * @return  Recipe temperature
   */
  int getTemperature(IMeltingInventory inv);

  /**
   * Gets the time this recipe takes in 1/5th second increments. Smeltery updates every 4 ticks
   * @param inv  Inventory instance
   * @return  Recipe time
   */
  int getTime(IMeltingInventory inv);

  /**
   * Adds recipe byproducts into the given inventory, used in the foundry, but not all smelteries
   * @param inv      Input inventory
   * @param handler  Fluid handler to fill with the byproduct
   */
  default void handleByproducts(IMeltingInventory inv, IFluidHandler handler) {}

  /* Recipe data */

  @Override
  default RecipeType<?> getType() {
    return RecipeTypes.MELTING;
  }

  @Override
  default ItemStack getToastSymbol() {
    return new ItemStack(TinkerSmeltery.searedMelter);
  }

  /* Utils */

  double LOG9_2 = 0.31546487678;

  /**
   * Calculates the temperature for a recipe based on the given temperature and factor
   * @param temperature  Required melting temperature in Celsius
   * @param factor       Multiplier based on material type
   * @return  Time for the recipe in celsius
   */
  static int calcTime(int temperature, float factor) {
    // base formula is temp^(.585), which will produce a time in 1/5th second increments
    return (int)Math.round((Math.pow(temperature + 300, 0.585f) * factor));
  }

  /**
   * Calculates a time factor for the given fluid amount
   * @param amount  Fluid amount
   * @return  Time factor
   */
  static float calcTimeFactor(int amount) {
    return (float)Math.sqrt(amount / (float)FluidValues.INGOT);
  }

  /**
   * Calculates the temperature for a recipe based on the given temperature and factor
   * @param temperature  Required melting temperature
   * @param amount       Amount of relevant fluid
   * @return  Time for the recipe in celsius
   */
  static int calcTimeForAmount(int temperature, int amount) {
    return calcTime(temperature, calcTimeFactor(amount));
  }
}
