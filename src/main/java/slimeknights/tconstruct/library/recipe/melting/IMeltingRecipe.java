package slimeknights.tconstruct.library.recipe.melting;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import slimeknights.mantle.recipe.ICustomOutputRecipe;
import slimeknights.tconstruct.library.materials.MaterialValues;
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
  FluidVolume getOutput(IMeltingInventory inv);

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

  /* Recipe data */

  @Override
  default RecipeType<?> getType() {
    return RecipeTypes.MELTING;
  }

  @Override
  default ItemStack getRecipeKindIcon() {
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
    return (float)Math.sqrt(amount / (float)MaterialValues.INGOT.asInexactDouble());
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
