package slimeknights.tconstruct.library.recipe.fuel;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.FluidIngredient;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;
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
  public void build(Consumer<IFinishedRecipe> consumer) {
    if (input.getFluids().isEmpty()) {
      throw new IllegalStateException("Must have at least one fluid for dynamic input");
    }
    build(consumer, Objects.requireNonNull(input.getFluids().get(0).getFluid().getRegistryName()));
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
    ResourceLocation advancementId = this.buildAdvancement(id, "melting_fuel");
    consumer.accept(new Result(id, group, input, duration, temperature, advancementBuilder, advancementId));
  }

  @AllArgsConstructor
  private static class Result implements IFinishedRecipe {
    @Getter
    private final ResourceLocation ID;
    private final String group;
    private final FluidIngredient input;
    private final int duration;
    private final int temperature;
    private final Advancement.Builder advancementBuilder;
    @Getter
    private final ResourceLocation advancementID;

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
    public IRecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.fuelSerializer.get();
    }

    @Nullable
    @Override
    public JsonObject getAdvancementJson() {
      return advancementBuilder.serialize();
    }
  }
}
