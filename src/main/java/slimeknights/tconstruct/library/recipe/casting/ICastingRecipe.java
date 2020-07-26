package slimeknights.tconstruct.library.recipe.casting;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.recipe.ICastingInventory;

public interface ICastingRecipe extends IRecipe<ICastingInventory> {
  @Override
  default ItemStack getCraftingResult(ICastingInventory inv) {
    return getRecipeOutput().copy();
  }

  @Override
  default boolean canFit(int width, int height) {
    return true;
  }

  @Override
  default boolean isDynamic() {
    return true;
  }

  @Override
  default ItemStack getIcon() {
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
    return 24 + (temperature * amount) / 1600;
  }

  /**
   * Calculates the cooling time for a recipe based on the fluid input
   * @param fluid  Fluid input
   * @return  Temperature for the recipe in celsius
   */
  static int calcCoolingTime(FluidStack fluid) {
    return calcCoolingTime(fluid.getFluid().getAttributes().getTemperature(fluid) - 300, fluid.getAmount());
  }
}
