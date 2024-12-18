package slimeknights.tconstruct.library.recipe.melting;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.data.loadable.common.FluidStackLoadable;
import slimeknights.mantle.data.loadable.common.IngredientLoadable;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.helper.LoadableRecipeSerializer;
import slimeknights.mantle.recipe.helper.LoggingRecipeSerializer;
import slimeknights.mantle.recipe.helper.RecipeHelper;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer.OreRateType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Recipe to melt an ingredient into a specific fuel
 */
public class MeltingRecipe implements IMeltingRecipe {
  /* Reusable fields */
  protected static final LoadableField<Ingredient, MeltingRecipe> INPUT = IngredientLoadable.DISALLOW_EMPTY.requiredField("ingredient", MeltingRecipe::getInput);
  protected static final LoadableField<FluidStack, MeltingRecipe> OUTPUT = FluidStackLoadable.REQUIRED_STACK_NBT.requiredField("result", MeltingRecipe::getOutput);
  protected static final LoadableField<Integer, MeltingRecipe> TEMPERATURE = IntLoadable.FROM_ZERO.requiredField("temperature", MeltingRecipe::getTemperature);
  protected static final LoadableField<Integer, MeltingRecipe> TIME = IntLoadable.FROM_ONE.requiredField("time", MeltingRecipe::getTime);
  protected static final LoadableField<List<FluidStack>, MeltingRecipe> BYPRODUCTS = FluidStackLoadable.REQUIRED_STACK_NBT.list(0).defaultField("byproducts", List.of(), r -> r.byproducts);
  /** Loader instance */
  public static final RecordLoadable<MeltingRecipe> LOADER = RecordLoadable.create(ContextKey.ID.requiredField(), LoadableRecipeSerializer.RECIPE_GROUP, INPUT, OUTPUT, TEMPERATURE, TIME, BYPRODUCTS, MeltingRecipe::new);

  @Getter
  private final ResourceLocation id;
  @Getter
  protected final String group;
  @Getter
  protected final Ingredient input;
  @Getter
  protected final FluidStack output;
  @Getter
  protected final int temperature;
  /** Number of "steps" needed to melt this, by default lava increases steps by 1 every 4 ticks (5 a second) */
  @Getter
  protected final int time;
  protected final List<FluidStack> byproducts;
  private List<List<FluidStack>> outputWithByproducts;

  public MeltingRecipe(ResourceLocation id, String group, Ingredient input, FluidStack output, int temperature, int time, List<FluidStack> byproducts) {
    this(id, group, input, output, temperature, time, byproducts, true);
  }

  /** Constructor that allows canceling the lookup addition, for generated recipes in JEI */
  public MeltingRecipe(ResourceLocation id, String group, Ingredient input, FluidStack output, int temperature, int time, List<FluidStack> byproducts, boolean addLookup) {
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
    for (FluidStack fluidStack : byproducts) {
      handler.fill(fluidStack.copy(), FluidAction.EXECUTE);
    }
  }

  /** Gets the recipe output for foundry display in JEI */
  public List<List<FluidStack>> getOutputWithByproducts() {
    if (outputWithByproducts == null) {
      outputWithByproducts = Stream.concat(Stream.of(output).map(output -> {
        // boost for foundry rate, this method is used for the foundry only
        OreRateType rate = getOreType();
        if (rate != null) {
          return new FluidStack(output, Config.COMMON.foundryOreRate.applyOreBoost(rate, output.getAmount()));
        }
        return output;
      }), byproducts.stream()).map(Collections::singletonList).collect(Collectors.toList());
    }
    return outputWithByproducts;
  }

  /** Interface for use in the serializer */
  @FunctionalInterface
  public interface IFactory<T extends MeltingRecipe> {
    /** Creates a new instance of this recipe */
    T create(ResourceLocation id, String group, Ingredient input, FluidStack output, int temperature, int time, List<FluidStack> byproducts);
  }

  @Deprecated(forRemoval = true)
  protected abstract static class AbstractSerializer<T extends MeltingRecipe> implements LoggingRecipeSerializer<T> {
    /** Creates a new recipe instance from Json */
    protected abstract T createFromJson(ResourceLocation id, String group, Ingredient input, FluidStack output, int temperature, int time, List<FluidStack> byproducts, JsonObject json);

    /** Creates a new recipe instance from the network */
    protected abstract T createFromNetwork(ResourceLocation id, String group, Ingredient input, FluidStack output, int temperature, int time, List<FluidStack> byproducts, FriendlyByteBuf buffer);

    @Override
    public T fromJson(ResourceLocation id, JsonObject json) {
      String group = GsonHelper.getAsString(json, "group", "");
      Ingredient input = Ingredient.fromJson(json.get("ingredient"));
      FluidStack output = RecipeHelper.deserializeFluidStack(GsonHelper.getAsJsonObject(json, "result"));

      // temperature calculates
      int temperature = GsonHelper.getAsInt(json, "temperature");
      int time = GsonHelper.getAsInt(json, "time");
      // validate values
      if (temperature < 0) throw new JsonSyntaxException("Melting temperature must be greater than zero");
      if (time <= 0) throw new JsonSyntaxException("Melting time must be greater than zero");
      List<FluidStack> byproducts = Collections.emptyList();
      if (json.has("byproducts")) {
        byproducts = JsonHelper.parseList(json, "byproducts", RecipeHelper::deserializeFluidStack);
      }

      return createFromJson(id, group, input, output, temperature, time, byproducts, json);
    }

    @Nullable
    @Override
    public T fromNetworkSafe(ResourceLocation id, FriendlyByteBuf buffer) {
      String group = buffer.readUtf(Short.MAX_VALUE);
      Ingredient input = Ingredient.fromNetwork(buffer);
      FluidStack output = FluidStack.readFromPacket(buffer);
      int temperature = buffer.readInt();
      int time = buffer.readVarInt();
      ImmutableList.Builder<FluidStack> builder = ImmutableList.builder();
      int byproductCount = buffer.readVarInt();
      for (int i = 0; i < byproductCount; i++) {
        builder.add(FluidStack.readFromPacket(buffer));
      }
      return createFromNetwork(id, group, input, output, temperature, time, builder.build(), buffer);
    }

    @Override
    public void toNetworkSafe(FriendlyByteBuf buffer, T recipe) {
      buffer.writeUtf(recipe.group);
      recipe.input.toNetwork(buffer);
      recipe.output.writeToPacket(buffer);
      buffer.writeInt(recipe.temperature);
      buffer.writeVarInt(recipe.time);
      buffer.writeVarInt(recipe.byproducts.size());
      for (FluidStack fluidStack : recipe.byproducts) {
        fluidStack.writeToPacket(buffer);
      }
    }
  }
}
