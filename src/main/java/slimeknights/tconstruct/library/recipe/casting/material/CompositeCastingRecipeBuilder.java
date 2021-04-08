package slimeknights.tconstruct.library.recipe.casting.material;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.fluid.Fluid;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.FluidIngredient;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/** Builder for composite recipes */
@RequiredArgsConstructor(staticName = "composite")
public class CompositeCastingRecipeBuilder extends AbstractRecipeBuilder<CompositeCastingRecipeBuilder> {
  private final MaterialId input;
  private final MaterialId output;
  private final CompositeCastingRecipe.Serializer<?> recipeSerializer;
  private FluidIngredient fluid = FluidIngredient.EMPTY;
  @Setter @Accessors(chain = true)
  private int temperature = -1;

  /** Creates a new builder for a basin recipe */
  public static CompositeCastingRecipeBuilder basin(MaterialId input, MaterialId output) {
    return composite(input, output, TinkerSmeltery.basinCompositeSerializer.get());
  }

  /** Creates a new builder for a table recipe */
  public static CompositeCastingRecipeBuilder table(MaterialId input, MaterialId output) {
    return composite(input, output, TinkerSmeltery.tableCompositeSerializer.get());
  }

  /* Fluid */

  /**
   * Sets the fluid ingredient
   * @param fluid  Fluid ingredient instance
   * @return  Builder instance
   */
  public CompositeCastingRecipeBuilder setFluid(FluidIngredient fluid) {
    this.fluid = fluid;
    return this;
  }

  /**
   * Sets the fluid for this recipe
   * @param tagIn   Tag<Fluid> instance
   * @param amount  amount of fluid
   * @return  Builder instance
   */
  public CompositeCastingRecipeBuilder setFluid(Tag<Fluid> tagIn, int amount) {
    return this.setFluid(FluidIngredient.of(tagIn, amount));
  }

  /**
   * Sets the fluid for this recipe, and cooling time if unset.
   * @param fluidStack  Fluid input
   * @return  Builder instance
   */
  public CompositeCastingRecipeBuilder setFluid(FluidStack fluidStack) {
    if (this.temperature == -1) {
      this.temperature = fluidStack.getFluid().getAttributes().getTemperature(fluidStack) - 300;
    }
    return setFluid(FluidIngredient.of(fluidStack));
  }


  @Override
  public void build(Consumer<RecipeJsonProvider> consumer) {
    build(consumer, output);
  }

  @Override
  public void build(Consumer<RecipeJsonProvider> consumer, Identifier id) {
    if (this.fluid == FluidIngredient.EMPTY) {
      throw new IllegalStateException("Composite recipes require a fluid input");
    }
    if (this.temperature < 0) {
      throw new IllegalStateException("Cooling time is too low, must be at least 0");
    }
    Identifier advancementId = this.buildOptionalAdvancement(id, "casting");
    consumer.accept(new Result(id, advancementId));
  }

  private class Result extends AbstractFinishedRecipe {
    public Result(Identifier ID, @Nullable Identifier advancementID) {
      super(ID, advancementID);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
      return recipeSerializer;
    }

    @Override
    public void serialize(JsonObject json) {
      json.addProperty("input", input.toString());
      json.add("fluid", fluid.serialize());
      json.addProperty("result", output.toString());
      json.addProperty("temperature", temperature);
    }
  }
}
