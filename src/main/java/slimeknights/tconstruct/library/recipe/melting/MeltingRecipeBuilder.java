package slimeknights.tconstruct.library.recipe.melting;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;
import java.util.Objects;
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
  private boolean isOre = false;
  private boolean isDamagable = false;

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
    int temperature = output.getFluid().getAttributes().getTemperature(output) - 300;
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
  public MeltingRecipeBuilder setOre() {
    this.isOre = true;
    return this;
  }

  /**
   * Marks this item as damagable, the output should scale based on the input damage
   * @return  Builder instance
   */
  public MeltingRecipeBuilder setDamagable() {
    this.isDamagable = true;
    return this;
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumer) {
    build(consumer, Objects.requireNonNull(output.getFluid().getRegistryName()));
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
    if (isOre && isDamagable) {
      throw new IllegalStateException("Builder cannot be both ore and damagable");
    }
    // only build JSON if needed
    ResourceLocation advancementId = this.buildOptionalAdvancement(id, "melting");
    consumer.accept(new Result(id, advancementId));
  }

  private class Result extends AbstractFinishedRecipe {
    public Result(ResourceLocation ID, @Nullable ResourceLocation advancementID) {
      super(ID, advancementID);
    }

    @Override
    public void serialize(JsonObject json) {
      if (!group.isEmpty()) {
        json.addProperty("group", group);
      }
      json.add("ingredient", input.serialize());
      json.add("result", RecipeHelper.serializeFluidStack(output));
      json.addProperty("temperature", temperature);
      json.addProperty("time", time);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      if (isOre) {
        return TinkerSmeltery.oreMeltingSerializer.get();
      }
      if (isDamagable) {
        return TinkerSmeltery.damagableMeltingSerializer.get();
      }
      return TinkerSmeltery.meltingSerializer.get();
    }
  }
}
