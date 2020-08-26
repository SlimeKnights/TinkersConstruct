package slimeknights.tconstruct.library.recipe.alloy.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.FluidIngredient;
import slimeknights.mantle.recipe.ICustomOutputRecipe;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.alloy.inventory.IAlloyInventory;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.util.List;

public interface IAlloyRecipe extends ICustomOutputRecipe<IAlloyInventory> {
  List<FluidIngredient> getFluidIngredients();
  /**
   * Gets a new instance of the output stack for this recipe.
   * @param inv  Input inventory
   * @return  Output stack
   */
  FluidStack getOutput(IAlloyInventory inv);

  /**
   * Gets the minimum temperature to alloy these fluids.
   * @param inv  Inventory instance
   * @return  Recipe temperature
   */
  int getTemperature(IAlloyInventory inv);

  @Override
  default IRecipeType<?> getType() {
    return RecipeTypes.ALLOY;
  }

  @Override
  default ItemStack getIcon() {
    return new ItemStack(TinkerSmeltery.alloyTank);
  }
}
