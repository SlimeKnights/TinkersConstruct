package slimeknights.tconstruct.library.recipe.alloy.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tags.Tag;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.FluidIngredient;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

public class AlloyRecipeBuilder extends AbstractRecipeBuilder<AlloyRecipeBuilder> {
  private final NonNullList<FluidIngredient> fluidIngredients = NonNullList.create();
  private final FluidStack output;
  private final int temperature;

  private AlloyRecipeBuilder(FluidStack outputIn) {
    output = outputIn;
    temperature = outputIn.getFluid().getAttributes().getTemperature();
  }

  public static AlloyRecipeBuilder alloying(FluidStack output) {
    if (output == FluidStack.EMPTY || output.getFluid() == Fluids.EMPTY) {
      throw new IllegalArgumentException("Invalid empty FluidStack output");
    }
    if (output.getFluid().getAttributes().getTemperature() <=0) {
      throw new IllegalArgumentException("Invalid Temperature, must be greater than zero");
    }
    return new AlloyRecipeBuilder(output);
  }

  public AlloyRecipeBuilder addFluid(Tag<Fluid> tagIn, int amount) {
    return this.addFluid(FluidIngredient.of(tagIn, amount));
  }

  public AlloyRecipeBuilder addFluid(FluidIngredient fluid) {
    fluidIngredients.add(fluid);
    return this;
  }

  public AlloyRecipeBuilder addFluid(FluidStack fluidStack) {
    fluidIngredients.add(FluidIngredient.of(fluidStack));
    return this;
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumer) {
    build(consumer, Objects.requireNonNull(output.getFluid().getRegistryName()));
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
    if (output.isEmpty()) {
      throw new IllegalStateException("Result may not be empty");
    }
    if (fluidIngredients.size() < 2) {
      throw new IllegalStateException("Two or more fluid inputs are required");
    }
    for (FluidIngredient fluidIngredient : fluidIngredients) {
      if (fluidIngredient.test(output)) {
        throw new IllegalStateException("Result cannot be contained in inputs");
      }
    }
    ResourceLocation advancementId = this.buildAdvancement(id, "alloying");
    consumer.accept(new Result(id, group, fluidIngredients, output, temperature, advancementBuilder, advancementId));
  }

  @AllArgsConstructor
  private static class Result implements IFinishedRecipe {
    @Getter
    protected final ResourceLocation ID;
    private final String group;
    private final NonNullList<FluidIngredient> fluidIngredients;
    private final FluidStack result;
    private final int temperature;
    private final Advancement.Builder advancementBuilder;
    @Getter
    private final ResourceLocation advancementID;

    @Override
    public void serialize(JsonObject json) {
      if (!group.isEmpty()) {
        json.addProperty("group", group);
      }

      JsonArray fluidArray = new JsonArray();
      for (FluidIngredient fluidIngredient : fluidIngredients) {
        fluidArray.add(fluidIngredient.serialize());
      }
      json.add("ingredients", fluidArray);
      json.add("result", RecipeHelper.serializeFluidStack(result));
      json.addProperty("temperature", this.temperature);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.alloySerializer.get();
    }

    @Nullable
    @Override
    public JsonObject getAdvancementJson() {
      return this.advancementBuilder.serialize();
    }
  }
}
