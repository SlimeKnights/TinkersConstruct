package slimeknights.tconstruct.library.recipe.alloying;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.fluid.Fluid;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.FluidIngredient;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/** Builder for alloy recipes */
@RequiredArgsConstructor(staticName = "alloy")
public class AlloyRecipeBuilder extends AbstractRecipeBuilder<AlloyRecipeBuilder> {
  private final FluidStack output;
  private final int temperature;
  private final List<FluidIngredient> inputs = new ArrayList<>();

  /**
   * Creates a new recipe producing the given fluid
   * @param fluid   Fluid output
   * @return  Builder instance
   */
  public static AlloyRecipeBuilder alloy(FluidStack fluid) {
    return alloy(fluid, fluid.getFluid().getAttributes().getTemperature(fluid) - 300);
  }

  /**
   * Creates a new recipe producing the given fluid
   * @param fluid   Fluid output
   * @param amount  Output amount
   * @return  Builder instance
   */
  public static AlloyRecipeBuilder alloy(Fluid fluid, int amount) {
    return alloy(new FluidStack(fluid, amount));
  }


  /* Inputs */

  /**
   * Adds an input
   * @param input  Input ingredient
   * @return  Builder instance
   */
  public AlloyRecipeBuilder addInput(FluidIngredient input) {
    inputs.add(input);
    return this;
  }

  /**
   * Adds an input
   * @param input  Input fluid
   * @return  Builder instance
   */
  public AlloyRecipeBuilder addInput(FluidStack input) {
    return addInput(FluidIngredient.of(input));
  }

  /**
   * Adds an input
   * @param fluid   Input fluid
   * @param amount  Input amount
   * @return  Builder instance
   */
  public AlloyRecipeBuilder addInput(Fluid fluid, int amount) {
    return addInput(FluidIngredient.of(new FluidStack(fluid, amount)));
  }

  /**
   * Adds an input
   * @param tag     Input tag
   * @param amount  Input amount
   * @return  Builder instance
   */
  public AlloyRecipeBuilder addInput(Tag<Fluid> tag, int amount) {
    return addInput(FluidIngredient.of(tag, amount));
  }


  /* Building */

  @Override
  public void build(Consumer<RecipeJsonProvider> consumer) {
    build(consumer, Objects.requireNonNull(output.getFluid().getRegistryName()));
  }

  @Override
  public void build(Consumer<RecipeJsonProvider> consumer, Identifier id) {
    if (inputs.size() < 2) {
      throw new IllegalStateException("Invalid alloying recipe " + id + ", must have at least two inputs");
    }
    Identifier advancementId = this.buildOptionalAdvancement(id, "alloys");
    consumer.accept(new Result(id, advancementId));
  }

  /** Result class for the builder */
  private class Result extends AbstractFinishedRecipe {
    public Result(Identifier ID, @Nullable Identifier advancementID) {
      super(ID, advancementID);
    }

    @Override
    public void serialize(JsonObject json) {
      JsonArray inputArray = new JsonArray();
      for (FluidIngredient input : inputs) {
        inputArray.add(input.serialize());
      }
      json.add("inputs", inputArray);
      json.add("result", RecipeHelper.serializeFluidStack(output));
      json.addProperty("temperature", temperature);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.alloyingSerializer.get();
    }
  }
}
