package slimeknights.tconstruct.library.recipe.melting;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.util.List;

/**
 * Extension of melting recipe to boost results of ores
 */
public class OreMeltingRecipe extends MeltingRecipe {
  private List<List<FluidStack>> displayOutput;
  public OreMeltingRecipe(ResourceLocation id, String group, Ingredient input, FluidStack output, int temperature, int time) {
    super(id, group, input, output, temperature, time);
  }

  /**
   * Boosts the value of a fluid stack based on the rate
   * @param fluid          Fluid
   * @param nuggetsPerOre  Nugget per ore rate
   * @return  Boosted fluid
   */
  private static FluidStack boost(FluidStack fluid, int nuggetsPerOre) {
    return new FluidStack(fluid, IMeltingInventory.applyOreBoost(fluid.getAmount(), nuggetsPerOre));
  }

  @Override
  public FluidStack getOutput(IMeltingInventory inv) {
    FluidStack output = getOutput();
    return boost(output, inv.getNuggetsPerOre());
  }

  @Override
  public boolean isOre() {
    return true;
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerSmeltery.oreMeltingSerializer.get();
  }
}
