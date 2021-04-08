package slimeknights.tconstruct.library.recipe.fuel;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.FluidIngredient;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import org.jetbrains.annotations.Nullable;
import java.util.Objects;
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
    return fuel(FluidIngredient.of(fluid), duration, fluid.getFluid().getAttributes().getTemperature(fluid) - 300);
  }

  @Override
  public void build(Consumer<RecipeJsonProvider> consumer) {
    if (input.getFluids().isEmpty()) {
      throw new IllegalStateException("Must have at least one fluid for dynamic input");
    }
    build(consumer, Objects.requireNonNull(input.getFluids().get(0).getFluid().getRegistryName()));
  }

  @Override
  public void build(Consumer<RecipeJsonProvider> consumer, Identifier id) {
    Identifier advancementId = this.buildOptionalAdvancement(id, "melting_fuel");
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
      json.add("fluid", input.serialize());
      json.addProperty("duration", duration);
      json.addProperty("temperature", temperature);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.fuelSerializer.get();
    }
  }
}
