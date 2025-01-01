package slimeknights.tconstruct.library.recipe.melting;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer.OreRateType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Builder for a recipe that melts an ingredient into a fuel
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MeltingRecipeBuilder extends AbstractRecipeBuilder<MeltingRecipeBuilder> {
  private final Ingredient input;
  private final FluidStack output;
  private final int temperature;
  private final int time;
  @Nullable
  private OreRateType oreRate = null;
  private List<OreRateType> byproductRates = List.of();
  @Nullable
  private int[] unitSizes;
  private final List<FluidStack> byproducts = new ArrayList<>();

  /**
   * Creates a new builder instance using a specific temperature
   * @param input        Recipe input
   * @param output       Recipe output
   * @param temperature  Temperature required
   * @param time         Time this recipe takes
   * @return  Builder instance
   */
  public static MeltingRecipeBuilder melting(Ingredient input, FluidStack output, int temperature, int time) {
    if (temperature < 0) throw new IllegalArgumentException("Invalid temperature " + temperature + ", must be greater than zero");
    if (time <= 0) throw new IllegalArgumentException("Invalid time " + time + ", must be greater than zero");
    return new MeltingRecipeBuilder(input, output, temperature, time);
  }

  /**
   * Creates a new builder instance using a specific temperature
   * @param input        Recipe input
   * @param output       Recipe output
   * @param timeFactor   Factor this recipe takes compared to the standard of ingots
   * @return  Builder instance
   */
  public static MeltingRecipeBuilder melting(Ingredient input, FluidStack output, float timeFactor) {
    int temperature = output.getFluid().getFluidType().getTemperature(output) - 300;
    return melting(input, output, temperature, IMeltingRecipe.calcTime(temperature, timeFactor));
  }

  /**
   * Creates a new builder instance using a specific temperature
   * @param input       Recipe input
   * @param fluid       Fluid result
   * @param amount      Fluid returned from recipe
   * @param timeFactor  Factor this recipe takes compared to the standard of ingots
   * @return  Builder instance
   */
  public static MeltingRecipeBuilder melting(Ingredient input, Fluid fluid, int amount, float timeFactor) {
    return melting(input, new FluidStack(fluid, amount), timeFactor);
  }

  /**
   * Creates a new builder instance using a specific temperature
   * @param input       Recipe input
   * @param fluid       Fluid result
   * @param amount      Fluid returned from recipe
   * @return  Builder instance
   */
  public static MeltingRecipeBuilder melting(Ingredient input, Fluid fluid, int amount) {
    return melting(input, new FluidStack(fluid, amount), IMeltingRecipe.calcTimeFactor(amount));
  }

  /**
   * Sets this recipe as an ore recipe, output multiplied based on the melter
   * @return  Builder instance
   */
  public MeltingRecipeBuilder setOre(OreRateType rate, OreRateType... byproductRates) {
    this.oreRate = rate;
    this.byproductRates = List.of(byproductRates);
    return this;
  }

  /**
   * Marks this item as damagable, the output should scale based on the input damage
   * @return  Builder instance
   */
  public MeltingRecipeBuilder setDamagable(int... unitSizes) {
    this.unitSizes = unitSizes;
    return this;
  }

  /**
   * Adds a byproduct to this recipe
   * @param fluidStack  Byproduct to add
   * @return  Builder instance
   */
  public MeltingRecipeBuilder addByproduct(FluidStack fluidStack) {
    byproducts.add(fluidStack);
    return this;
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer) {
    save(consumer, BuiltInRegistries.FLUID.getKey(output.getFluid()));
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    if (oreRate != null && unitSizes != null) {
      throw new IllegalStateException("Builder cannot be both ore and damagable");
    }
    // only build JSON if needed
    ResourceLocation advancementId = this.buildOptionalAdvancement(id, "melting");
    // based on properties, choose which recipe to build
    if (oreRate != null) {
      consumer.accept(new LoadableFinishedRecipe<>(
        new OreMeltingRecipe(id, group, input, output, temperature, time, byproducts, oreRate, byproductRates),
        OreMeltingRecipe.LOADER, advancementId));
    } else if (unitSizes != null) {
      consumer.accept(new LoadableFinishedRecipe<>(
        new DamageableMeltingRecipe(id, group, input, output, temperature, time, byproducts, unitSizes[0], List.of(Arrays.stream(unitSizes, 1, unitSizes.length).boxed().toArray(Integer[]::new))),
        DamageableMeltingRecipe.LOADER, advancementId));
    } else {
      consumer.accept(new LoadableFinishedRecipe<>(
        new MeltingRecipe(id, group, input, output, temperature, time, byproducts),
        MeltingRecipe.LOADER, advancementId));
    }
  }
}
