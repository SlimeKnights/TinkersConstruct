package slimeknights.tconstruct.library.recipe.alloying;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
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
  private final List<FluidIngredient> inputs = new ArrayList<>();

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
  public AlloyRecipeBuilder addInput(ITag<Fluid> tag, int amount) {
    return addInput(FluidIngredient.of(tag, amount));
  }


  /* Building */

  @Override
  public void build(Consumer<IFinishedRecipe> consumer) {
    build(consumer, Objects.requireNonNull(output.getFluid().getRegistryName()));
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
    if (inputs.size() < 2) {
      throw new IllegalStateException("Invalid alloying recipe " + id + ", must have at least two inputs");
    }
    for (FluidIngredient input : inputs) {
      if (input.test(output)) {
        throw new IllegalStateException("Invalid alloying recipe " + id + ", output contained in inputs");
      }
    }
    ResourceLocation advancementId = this.buildAdvancement(id, "alloys");
    consumer.accept(new Result(id, advancementId));
  }

  /** Result class for the builder */
  @AllArgsConstructor
  private class Result implements IFinishedRecipe {
    @Getter
    private final ResourceLocation ID;
    @Getter
    private final ResourceLocation advancementID;

    @Override
    public void serialize(JsonObject json) {
      json.add("output", RecipeHelper.serializeFluidStack(output));
      JsonArray inputArray = new JsonArray();
      for (FluidIngredient input : inputs) {
        inputArray.add(input.serialize());
      }
      json.add("inputs", inputArray);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.alloyingSerializer.get();
    }

    @Nullable
    @Override
    public JsonObject getAdvancementJson() {
      return advancementBuilder.serialize();
    }
  }
}
