package slimeknights.tconstruct.library.recipe.fuel;

import lombok.AllArgsConstructor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;

import java.util.function.Consumer;

/**
 * Builds a new recipe for a melter or smeltery fuel
 */
@AllArgsConstructor(staticName="fuel")
public class MeltingFuelBuilder extends AbstractRecipeBuilder<MeltingFuelBuilder> {
  private final FluidIngredient input;
  private final int duration;
  private final int temperature;

  /**
   * Creates a new builder instance with automatic temperature
   * @param fluid     Fluid stack
   * @param duration  Fluid duration
   * @return  Builder instance
   */
  public static MeltingFuelBuilder fuel(FluidStack fluid, int duration) {
    return fuel(FluidIngredient.of(fluid), duration, fluid.getFluid().getFluidType().getTemperature(fluid) - 300);
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer) {
    if (input.getFluids().isEmpty()) {
      throw new IllegalStateException("Must have at least one fluid for dynamic input");
    }
    save(consumer, BuiltInRegistries.FLUID.getKey(input.getFluids().get(0).getFluid()));
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    ResourceLocation advancementId = this.buildOptionalAdvancement(id, "melting_fuel");
    consumer.accept(new LoadableFinishedRecipe<>(new MeltingFuel(id, group, input, duration, temperature), MeltingFuel.LOADER, advancementId));
  }
}
