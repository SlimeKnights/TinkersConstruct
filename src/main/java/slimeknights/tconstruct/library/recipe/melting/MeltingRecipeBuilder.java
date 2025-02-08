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
import slimeknights.mantle.recipe.helper.FluidOutput;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer.OreRateType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe.getTemperature;

/**
 * Builder for a recipe that melts an ingredient into a fuel
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MeltingRecipeBuilder extends AbstractRecipeBuilder<MeltingRecipeBuilder> {
  private final Ingredient input;
  private final FluidOutput output;
  private final int temperature;
  private final int time;
  @Nullable
  private OreRateType oreRate = null;
  private List<OreRateType> byproductRates = List.of();
  @Nullable
  private int[] unitSizes;
  private final List<FluidOutput> byproducts = new ArrayList<>();

  /**
   * Creates a new builder instance using a specific temperature
   * @param input        Recipe input
   * @param output       Recipe output
   * @param temperature  Temperature required
   * @param time         Time this recipe takes
   * @return  Builder instance
   */
  public static MeltingRecipeBuilder melting(Ingredient input, FluidOutput output, int temperature, int time) {
    if (temperature < 0) throw new IllegalArgumentException("Invalid temperature " + temperature + ", must be greater than zero");
    if (time <= 0) throw new IllegalArgumentException("Invalid time " + time + ", must be greater than zero");
    return new MeltingRecipeBuilder(input, output, temperature, time);
  }

  /**
   * Creates a new builder instance using a factored temperature
   * @param input        Recipe input
   * @param output       Recipe output
   * @param temperature  Temperature required
   * @return  Builder instance
   */
  public static MeltingRecipeBuilder melting(Ingredient input, FluidOutput output, int temperature, float timeFactor) {
    return melting(input, output, temperature, IMeltingRecipe.calcTime(temperature, timeFactor));
  }

  /**
   * Creates a new builder instance using a specific temperature
   * @param input        Recipe input
   * @param output       Recipe output
   * @param temperature  Temperature required
   * @param time         Time this recipe takes
   * @return  Builder instance
   */
  public static MeltingRecipeBuilder melting(Ingredient input, FluidStack output, int temperature, int time) {
    return melting(input, FluidOutput.fromStack(output), temperature, time);
  }

  /**
   * Creates a new builder instance using a specific temperature
   * @param input        Recipe input
   * @param fluid        Recipe result
   * @param amount       Result amount
   * @param timeFactor   Factor this recipe takes compared to the standard of ingots
   * @return  Builder instance
   */
  public static MeltingRecipeBuilder melting(Ingredient input, FluidObject<?> fluid, int amount, float timeFactor) {
    int temperature = getTemperature(fluid);
    return melting(input, fluid.result(amount), temperature, IMeltingRecipe.calcTime(temperature, timeFactor));
  }

  /**
   * Creates a new builder instance using a specific temperature
   * @param input        Recipe input
   * @param output       Recipe output
   * @param timeFactor   Factor this recipe takes compared to the standard of ingots
   * @return  Builder instance
   */
  public static MeltingRecipeBuilder melting(Ingredient input, FluidStack output, float timeFactor) {
    int temperature = getTemperature(output);
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
  public static MeltingRecipeBuilder melting(Ingredient input, FluidObject<?> fluid, int amount) {
    return melting(input, fluid, amount, IMeltingRecipe.calcTimeFactor(amount));
  }

  /**
   * Creates a new builder instance using a specific temperature
   * @param input       Recipe input
   * @param fluid       Fluid result
   * @param amount      Fluid returned from recipe
   * @return  Builder instance
   */
  public static MeltingRecipeBuilder melting(Ingredient input, Fluid fluid, int amount) {
    return melting(input, fluid, amount, IMeltingRecipe.calcTimeFactor(amount));
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
    // unit size of 0 is not useful, neither is empty array. So treat 0 as ignored to make it easier to do optional arguments
    if (unitSizes.length > 0 && unitSizes[0] > 0) {
      this.unitSizes = unitSizes;
    }
    return this;
  }

  /**
   * Adds a byproduct to this recipe
   * @param fluid  Byproduct to add
   * @return  Builder instance
   */
  public MeltingRecipeBuilder addByproduct(FluidOutput fluid) {
    byproducts.add(fluid);
    return this;
  }

  /**
   * Adds a byproduct to this recipe
   * @param fluid  Byproduct to add
   * @return  Builder instance
   */
  public MeltingRecipeBuilder addByproduct(FluidStack fluid) {
    return addByproduct(FluidOutput.fromStack(fluid));
  }

  @SuppressWarnings("deprecation")
  @Override
  public void save(Consumer<FinishedRecipe> consumer) {
    save(consumer, BuiltInRegistries.FLUID.getKey(output.get().getFluid()));
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
