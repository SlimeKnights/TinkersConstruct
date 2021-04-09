package slimeknights.tconstruct.library.recipe.casting;

import net.minecraft.item.ItemStack;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import slimeknights.mantle.recipe.ICommonRecipe;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.recipe.ICastingInventory;

/**
 * Base interface for all casting recipes
 */
public interface ICastingRecipe extends ICommonRecipe<ICastingInventory> {
  @Override
  default ItemStack getRecipeKindIcon() {
    return new ItemStack(getType() == RecipeTypes.CASTING_TABLE ? TinkerSmeltery.castingTable : TinkerSmeltery.castingBasin);
  }

  /**
   * Gets the amount of fluid required for this recipe
   * @param inv  Inventory instance
   * @return  Fluid amount when using the fluid in the inventory
   */
  int getFluidAmount(ICastingInventory inv);

  /**
   * @return true if the cast item is consumed on crafting
   */
  boolean isConsumed();

  /**
   * @return true if the recipe output is placed into the casting input slot
   */
  boolean switchSlots();

  /**
   * @param inv ICastingInventory for casting recipe
   * @return  cooling time for the output.
   */
  int getCoolingTime(ICastingInventory inv);

  /**
   * Calculates the cooling time for a recipe based on the amount and temperature
   * @param temperature  Temperature baseline in celsius
   * @param amount       Output amount
   * @return  Cooling time based on the given inputs
   */
  static int calcCoolingTime(int temperature, int amount) {
    // the time in melting reipes assumes updating 5 times a second
    // we update 20 times a second, so get roughly a quart of those values
    return IMeltingRecipe.calcTimeForAmount(temperature, amount);
  }

  /**
   * Calculates the cooling time for a recipe based on the fluid input
   * @param fluid  Fluid input
   * @return  Time for the recipe
   */
  static int calcCoolingTime(FluidVolume fluid) {
    return calcCoolingTime((int) (fluid.getFluidKey().getTemperature().getTemperature(fluid) - 300), fluid.getAmount());
  }
}
