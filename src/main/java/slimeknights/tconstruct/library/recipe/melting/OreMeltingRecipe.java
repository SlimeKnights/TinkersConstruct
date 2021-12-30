package slimeknights.tconstruct.library.recipe.melting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.util.List;

/**
 * Extension of melting recipe to boost results of ores
 */
public class OreMeltingRecipe extends MeltingRecipe {
  public OreMeltingRecipe(ResourceLocation id, String group, Ingredient input, FluidStack output, int temperature, int time, List<FluidStack> byproducts) {
    // multiply byproducts by the config amount, this runs on recipe parse so config is loaded by then
    super(id, group, input, output, temperature, time, byproducts.stream()
                                                                 .map(fluid -> new FluidStack(fluid, fluid.getAmount() * Config.COMMON.foundryNuggetsPerOre.get() / IMeltingContainer.BASE_NUGGET_RATE))
                                                                 .toList());
  }

  /**
   * Boosts the value of a fluid stack based on the rate
   * @param fluid          Fluid
   * @param nuggetsPerOre  Nugget per ore rate
   * @return  Boosted fluid
   */
  private static FluidStack boost(FluidStack fluid, int nuggetsPerOre) {
    return new FluidStack(fluid, IMeltingContainer.applyOreBoost(fluid.getAmount(), nuggetsPerOre));
  }

  @Override
  public FluidStack getOutput(IMeltingContainer inv) {
    FluidStack output = getOutput();
    return boost(output, inv.getNuggetsPerOre());
  }

  @Override
  public boolean isOre() {
    return true;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerSmeltery.oreMeltingSerializer.get();
  }
}
