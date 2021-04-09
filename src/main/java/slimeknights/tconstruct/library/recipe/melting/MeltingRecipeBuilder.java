package slimeknights.tconstruct.library.recipe.melting;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.fluid.Fluid;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import org.jetbrains.annotations.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Builder for a recipe that melts an ingredient into a fuel
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MeltingRecipeBuilder extends AbstractRecipeBuilder<MeltingRecipeBuilder> {
  private final Ingredient input;
  private final FluidVolume output;
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
  public static MeltingRecipeBuilder melting(Ingredient input, FluidVolume output, int temperature, int time) {
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
  public static MeltingRecipeBuilder melting(Ingredient input, FluidVolume output, float timeFactor) {
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
    return melting(input, new FluidVolume(fluid, amount), timeFactor);
  }

  /**
   * Creates a new builder instance using a specific temperature
   * @param input       Recipe input
   * @param fluid       Fluid result
   * @param amount      Fluid returned from recipe
   * @return  Builder instance
   */
  public static MeltingRecipeBuilder melting(Ingredient input, Fluid fluid, int amount) {
    return melting(input, new FluidVolume(fluid, amount), IMeltingRecipe.calcTimeFactor(amount));
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
  public void build(Consumer<RecipeJsonProvider> consumer) {
    build(consumer, Objects.requireNonNull(output.getFluid().getRegistryName()));
  }

  @Override
  public void build(Consumer<RecipeJsonProvider> consumer, Identifier id) {
    if (isOre && isDamagable) {
      throw new IllegalStateException("Builder cannot be both ore and damagable");
    }
    // only build JSON if needed
    Identifier advancementId = this.buildOptionalAdvancement(id, "melting");
    consumer.accept(new Result(id, advancementId));
  }

  private class Result extends AbstractFinishedRecipe {
    public Result(Identifier ID, @Nullable Identifier advancementID) {
      super(ID, advancementID);
    }

    @Override
    public void serialize(JsonObject json) {
      if (!group.isEmpty()) {
        json.addProperty("group", group);
      }
      json.add("ingredient", input.toJson());
      json.add("result", RecipeHelper.serializeFluidVolume(output));
      json.addProperty("temperature", temperature);
      json.addProperty("time", time);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
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
