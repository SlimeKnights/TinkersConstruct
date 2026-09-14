package slimeknights.tconstruct.library.recipe.melting;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.helper.FluidOutput;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer.OreRateType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/** Standard display melting recipe implementation. */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DisplayMeltingRecipe implements IDisplayableMeltingRecipe {
  private final ResourceLocation recipeId;
  private final List<ItemStack> inputs;
  private final List<FluidStack> outputs;
  private final List<List<FluidStack>> outputWithByproducts;
  private final int temperature;
  private final int time;
  @Nullable
  private final OreRateType oreType;
  private final boolean timeDynamic;

  @Override
  public int getTime(FluidStack fluid) {
    return IMeltingRecipe.calcTimeForAmount(temperature, fluid.getAmount());
  }


  /* Builder */

  /** Creates a new builder instance */
  public static Builder id(ResourceLocation id) {
    return new Builder(id);
  }

  @Setter
  @Accessors(fluent = true)
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder {
    private final ResourceLocation id;
    private List<ItemStack> inputs;
    /** List of output options. Size should match {@link #inputs(List)} for focus linking. */
    private List<FluidStack> outputs;
    /** List of byproducts before scaling */
    private final List<List<FluidStack>> byproducts = new ArrayList<>();
    private int temperature = 0;
    private int time = 0;
    private OreRateType oreType = null;
    private boolean timeDynamic = false;

    /** Sets the input to an ingredient. */
    public Builder input(Ingredient ingredient) {
      return inputs(List.of(ingredient.getItems()));
    }

    /** Sets the input to a single item. */
    public Builder input(ItemStack item) {
      return inputs(List.of(item));
    }

    /** Sets the input to a single item. */
    public Builder input(ItemLike item) {
      return input(new ItemStack(item));
    }

    /** Sets the output to a single fluid. */
    public Builder output(FluidStack fluid) {
      return outputs(List.of(fluid));
    }

    /** Sets the output to a single fluid. */
    public Builder output(FluidOutput fluid) {
      return output(fluid.get());
    }

    /** Adds a byproduct to the builder. */
    public Builder byproduct(List<FluidStack> byproduct) {
      this.byproducts.add(byproduct);
      return this;
    }

    /** Adds a byproduct to the builder. */
    public Builder byproduct(FluidStack byproduct) {
      return byproduct(List.of(byproduct));
    }

    /** Adds a byproduct to the builder. */
    public Builder byproduct(FluidOutput byproduct) {
      return byproduct(byproduct.get());
    }

    /** Adds a list of byproducts to the builder. */
    public Builder byproducts(List<FluidOutput> byproducts) {
      for (FluidOutput byproduct : byproducts) {
        byproduct(byproduct);
      }
      return this;
    }

    /** Adds an ore byproduct to the builder, scaled for an ore recipe. */
    public Builder byproduct(OreRateType oreType, List<FluidStack> byproduct) {
      return byproduct(byproduct.stream().map(fluid -> Config.COMMON.foundryByproductRate.applyOreBoost(oreType, fluid, false)).toList());
    }

    /** Adds a byproduct to the builder, scaled for an ore recipe. */
    public Builder byproduct(OreRateType oreType, FluidStack byproduct) {
      return byproduct(List.of(Config.COMMON.foundryByproductRate.applyOreBoost(oreType, byproduct, false)));
    }

    /** Marks the time as dynamic */
    public Builder timeDynamic() {
      return timeDynamic(true);
    }

    /** Builds the final display recipe */
    public IDisplayableMeltingRecipe build() {
      if (inputs.isEmpty()) throw new IllegalStateException("Casts cannot be empty");
      if (outputs.isEmpty()) throw new IllegalStateException("Fluids cannot be empty");
      List<List<FluidStack>> outputsWithByproducts = new ArrayList<>(byproducts.size() + 1);
      // if we have an ore rate, apply it to the output for the foundry
      if (oreType != null) {
        outputsWithByproducts.add(outputs.stream().map(fluid -> Config.COMMON.foundryOreRate.applyOreBoost(oreType, fluid, true)).toList());
      } else {
        outputsWithByproducts.add(outputs);
      }
      outputsWithByproducts.addAll(byproducts);
      // if time is not set, calculate it from the outputs
      int time = this.time;
      if (time == 0) {
        time = outputs.stream().mapToInt(fluid -> IMeltingRecipe.calcTimeForAmount(temperature, fluid.getAmount())).max().orElse(1);
      }
      return new DisplayMeltingRecipe(id, inputs, outputs, outputsWithByproducts, temperature, time, oreType, timeDynamic);
    }
  }
}
