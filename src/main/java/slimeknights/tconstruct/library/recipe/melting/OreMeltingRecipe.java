package slimeknights.tconstruct.library.recipe.melting;

import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.util.List;

/**
 * Extension of melting recipe to boost results of ores
 */
public class OreMeltingRecipe extends MeltingRecipe {
  private List<List<FluidVolume>> displayOutput;
  public OreMeltingRecipe(Identifier id, String group, Ingredient input, FluidVolume output, int temperature, int time) {
    super(id, group, input, output, temperature, time);
  }

  /**
   * Boosts the value of a fluid stack based on the rate
   * @param fluid          Fluid
   * @param nuggetsPerOre  Nugget per ore rate
   * @return  Boosted fluid
   */
  private static FluidVolume boost(FluidVolume fluid, int nuggetsPerOre) {
    return FluidVolume.create(fluid.getRawFluid(), IMeltingInventory.applyOreBoost(fluid.getAmount(), nuggetsPerOre));
  }

  @Override
  public FluidVolume getOutput(IMeltingInventory inv) {
    FluidVolume output = getOutput();
    return boost(output, inv.getNuggetsPerOre());
  }

  @Override
  public boolean isOre() {
    return true;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerSmeltery.oreMeltingSerializer;
  }

  @Override
  public FluidVolume getOutput() {
    return super.getOutput();
  }
}
