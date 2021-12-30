package slimeknights.tconstruct.library.recipe.modifiers.spilling;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.ISpillingEffect;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Builder to implement spilling for a fluid
 */
@SuppressWarnings("unused")
@RequiredArgsConstructor(staticName = "forFluid")
public class SpillingRecipeBuilder extends AbstractRecipeBuilder<SpillingRecipeBuilder> {
  private final FluidIngredient fluid;
  private final List<ISpillingEffect> effects = new ArrayList<>();

  /** Creates a builder for a fluid stack */
  public static SpillingRecipeBuilder forFluid(FluidStack fluid) {
    return new SpillingRecipeBuilder(FluidIngredient.of(fluid));
  }
  /** Creates a builder for a fluid and amount */
  public static SpillingRecipeBuilder forFluid(Fluid fluid, int amount) {
    return new SpillingRecipeBuilder(FluidIngredient.of(fluid, amount));
  }

  /** Creates a builder for a tag and amount */
  public static SpillingRecipeBuilder forFluid(Tag<Fluid> fluid, int amount) {
    return new SpillingRecipeBuilder(FluidIngredient.of(fluid, amount));
  }

  /** Adds an effect to the given fluid */
  public SpillingRecipeBuilder addEffect(ISpillingEffect effect) {
    effects.add(effect);
    return this;
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer) {
    save(consumer, Objects.requireNonNull(fluid.getFluids().get(0).getFluid().getRegistryName()));
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    if (effects.isEmpty()) {
      throw new IllegalStateException("Must have at least one effect to build");
    }
    consumer.accept(new Finished(id, null));
  }

  private class Finished extends AbstractFinishedRecipe {
    public Finished(ResourceLocation ID, @Nullable ResourceLocation advancementID) {
      super(ID, advancementID);
    }

    @Override
    public void serializeRecipeData(JsonObject json) {
      json.add("fluid", fluid.serialize());
      JsonArray effectArray = new JsonArray();
      for (ISpillingEffect effect : effects) {
        effectArray.add(SpillingRecipeLookup.serializeEffect(effect));
      }
      json.add("effects", effectArray);
    }

    @Override
    public RecipeSerializer<?> getType() {
      return TinkerModifiers.spillingSerializer.get();
    }
  }
}
