package slimeknights.tconstruct.library.recipe.melting;

import lombok.Getter;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.data.loadable.common.IngredientLoadable;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.helper.FluidOutput;
import slimeknights.mantle.recipe.helper.LoadableRecipeSerializer;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer.OreRateType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Stream;

/**
 * Recipe to melt an ingredient into a specific fuel
 */
public class MeltingRecipe implements IMeltingRecipe {
  /* Reusable fields */
  protected static final LoadableField<Ingredient, MeltingRecipe> INPUT = IngredientLoadable.DISALLOW_EMPTY.requiredField("ingredient", MeltingRecipe::getInput);
  protected static final LoadableField<FluidOutput, MeltingRecipe> OUTPUT = FluidOutput.Loadable.REQUIRED.requiredField("result", r -> r.output);
  protected static final LoadableField<Integer, MeltingRecipe> TEMPERATURE = IntLoadable.FROM_ZERO.requiredField("temperature", MeltingRecipe::getTemperature);
  protected static final LoadableField<Integer, MeltingRecipe> TIME = IntLoadable.FROM_ONE.requiredField("time", MeltingRecipe::getTime);
  protected static final LoadableField<List<FluidOutput>, MeltingRecipe> BYPRODUCTS = FluidOutput.Loadable.REQUIRED.list(0).defaultField("byproducts", List.of(), r -> r.byproducts);
  /** Loader instance */
  public static final RecordLoadable<MeltingRecipe> LOADER = RecordLoadable.create(ContextKey.ID.requiredField(), LoadableRecipeSerializer.RECIPE_GROUP, INPUT, OUTPUT, TEMPERATURE, TIME, BYPRODUCTS, MeltingRecipe::new);

  @Getter
  private final ResourceLocation id;
  @Getter
  protected final String group;
  @Getter
  protected final Ingredient input;
  protected final FluidOutput output;
  @Getter
  protected final int temperature;
  /** Number of "steps" needed to melt this, by default lava increases steps by 1 every 4 ticks (5 a second) */
  @Getter
  protected final int time;
  protected final List<FluidOutput> byproducts;
  protected List<List<FluidStack>> outputWithByproducts;

  public MeltingRecipe(ResourceLocation id, String group, Ingredient input, FluidOutput output, int temperature, int time, List<FluidOutput> byproducts) {
    this(id, group, input, output, temperature, time, byproducts, true);
  }

  /** Constructor that allows canceling the lookup addition, for generated recipes in JEI */
  public MeltingRecipe(ResourceLocation id, String group, Ingredient input, FluidOutput output, int temperature, int time, List<FluidOutput> byproducts, boolean addLookup) {
    this.id = id;
    this.group = group;
    this.input = input;
    this.output = output;
    this.temperature = temperature;
    this.time = time;
    this.byproducts = byproducts;
    if (addLookup) {
      MeltingRecipeLookup.addMeltingFluid(input, output, temperature);
    }
  }

  @Override
  public boolean matches(IMeltingContainer inv, Level world) {
    return input.test(inv.getStack());
  }

  @Override
  public int getTemperature(IMeltingContainer inv) {
    return temperature;
  }

  @Override
  public int getTime(IMeltingContainer inv) {
    return time;
  }

  /** Gets the output of this recipe */
  public FluidStack getOutput() {
    return output.get();
  }

  @Override
  public FluidStack getOutput(IMeltingContainer inv) {
    return output.copy();
  }

  @Override
  public NonNullList<Ingredient> getIngredients() {
    return NonNullList.of(Ingredient.EMPTY, input);
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerSmeltery.meltingSerializer.get();
  }

  /** If nonnull, recipe is boosted by this ore type */
  @Nullable
  public OreRateType getOreType() {
    return null;
  }

  @Override
  public void handleByproducts(IMeltingContainer inv, IFluidHandler handler) {
    // fill byproducts until we run out of space or byproducts
    for (FluidOutput fluid : byproducts) {
      handler.fill(fluid.copy(), FluidAction.EXECUTE);
    }
  }

  /** Scales the output for display in the foundry tab */
  private Stream<FluidStack> scaleOutput() {
    return Stream.of(output).map(output -> {
      // boost for foundry rate, this method is used for the foundry only
      OreRateType rate = getOreType();
      if (rate != null) {
        return output.get().copyWithAmount(Config.COMMON.foundryOreRate.applyOreBoost(rate, output.getAmount()));
      }
      return output.get();
    });
  }

  /** Gets the recipe output for foundry display in JEI */
  public List<List<FluidStack>> getOutputWithByproducts() {
    if (outputWithByproducts == null) {
      outputWithByproducts = Stream.concat(Stream.of(output), byproducts.stream()).map(fluid -> List.of(fluid.get())).toList();
    }
    return outputWithByproducts;
  }
}
