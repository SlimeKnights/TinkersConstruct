package slimeknights.tconstruct.library.recipe.melting;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.advancements.Advancement.Builder;
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
import java.util.function.ToIntFunction;

/**
 * Builder for a recipe that melts an ingredient into a fuel
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MeltingRecipeBuilder extends AbstractRecipeBuilder<MeltingRecipeBuilder> {
  private final Ingredient input;
  private final FluidStack output;
  private final int temperature;

  /**
   * Creates a new builder instance using a specific temperature
   * @param input        Recipe input
   * @param output       Recipe output
   * @param temperature  Temperature required
   * @return  Builder instance
   */
  public static MeltingRecipeBuilder melting(Ingredient input, FluidStack output, int temperature) {
    if (temperature <= 0) {
      throw new IllegalArgumentException("Invalid temperature " + temperature + ", must be greater than zero");
    }
    return new MeltingRecipeBuilder(input, output, temperature);
  }

  /**
   * Creates a new builder instance using a specific temperature
   * @param input        Recipe input
   * @param fluid        Fluid result
   * @param amount       Fluid returned from recipe
   * @param temperature  Function to get a temperature for the given fluid amount
   * @return  Builder instance
   */
  public static MeltingRecipeBuilder melting(Ingredient input, Fluid fluid, int amount, ToIntFunction<Integer> temperature) {
    return melting(input, new FluidStack(fluid, amount), temperature.applyAsInt(amount));
  }

  /**
   * Creates a new builder instance using a calculated temperature
   * @param input   Recipe input
   * @param output  Recipe output
   * @return  Builder instance
   */
  public static MeltingRecipeBuilder melting(Ingredient input, FluidStack output) {
    return melting(input, output, IMeltingRecipe.calcTemperature(output));
  }

  /**
   * Creates a new builder instance using a calculated temperature
   * @param input   Recipe input
   * @param fluid   Fluid result
   * @param amount  Fluid returned from recipe
   * @return  Builder instance
   */
  public static MeltingRecipeBuilder melting(Ingredient input, Fluid fluid, int amount) {
    return melting(input, new FluidStack(fluid, amount));
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumer) {
    build(consumer, Objects.requireNonNull(output.getFluid().getRegistryName()));
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
    ResourceLocation advancementId = this.buildAdvancement(id, "melting");
    consumer.accept(new Result(id, getGroup(), input, output, temperature, advancementBuilder, advancementId));
  }

  @AllArgsConstructor
  private static class Result implements IFinishedRecipe {
    @Getter
    private final ResourceLocation ID;
    private final String group;
    private final Ingredient input;
    private final FluidStack output;
    private final int temperature;
    private final Builder advancementBuilder;
    @Getter
    private final ResourceLocation advancementID;

    @Override
    public void serialize(JsonObject json) {
      if (!group.isEmpty()) {
        json.addProperty("group", group);
      }
      json.add("ingredient", this.input.serialize());
      json.add("result", RecipeHelper.serializeFluidStack(this.output));
      json.addProperty("temperature", this.temperature);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.meltingSerializer.get();
    }

    @Override
    @Nullable
    public JsonObject getAdvancementJson() {
      return this.advancementBuilder.serialize();
    }
  }
}
