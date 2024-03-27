package slimeknights.tconstruct.library.recipe.melting;

import com.google.common.collect.Streams;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.helper.LoadableRecipeSerializer;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.common.config.Config.OreRate;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.json.field.MergingListField;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer.OreRateType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.util.List;
import java.util.function.Function;

/**
 * Extension of melting recipe to boost results of ores
 */
public class OreMeltingRecipe extends MeltingRecipe {
  public static final RecordLoadable<OreMeltingRecipe> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(), LoadableRecipeSerializer.RECIPE_GROUP, INPUT, OUTPUT, TEMPERATURE, TIME, BYPRODUCTS,
    TinkerLoadables.ORE_RATE_TYPE.requiredField("rate", OreMeltingRecipe::getOreType),
    new MergingListField<>(TinkerLoadables.ORE_RATE_TYPE.defaultField("rate", OreRateType.DEFAULT, Function.identity()), "byproducts", r -> r.byproductTypes),
    // statically scale the byproduct rates during construction. Note this won't happen during datagen, as datagen does not use this constructor.
    (id, group, input, output, temperature, time, byproducts, oreType, byproductTypes) -> new OreMeltingRecipe(id, group, input, output, temperature, time, scaleByproducts(Config.COMMON.foundryByproductRate, byproducts, oreType, byproductTypes), oreType, List.of()));

  @Getter
  private final OreRateType oreType;
  private final List<OreRateType> byproductTypes;
  protected OreMeltingRecipe(ResourceLocation id, String group, Ingredient input, FluidStack output, int temperature, int time, List<FluidStack> byproducts, OreRateType oreType, List<OreRateType> byproductTypes) {
    super(id, group, input, output, temperature, time, byproducts);
    this.oreType = oreType;
    this.byproductTypes = byproductTypes;
  }

  @Override
  public FluidStack getOutput(IMeltingContainer inv) {
    FluidStack output = getOutput();
    return inv.getOreRate().applyOreBoost(oreType, output);
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerSmeltery.oreMeltingSerializer.get();
  }

  /** Scales the byproducts using the given rate */
  public static List<FluidStack> scaleByproducts(OreRate rate, List<FluidStack> byproducts, OreRateType rateType, List<OreRateType> byproductRates) {
    // empty means we are coming from network buffer, don't boost it again
    if (byproductRates.isEmpty()) {
      return byproducts;
    }
    // different size should never happen since we parse the same list
    if (byproductRates.size() != byproducts.size()) {
      throw new IllegalArgumentException("Wrong number of byproduct rates passed, must have one per byproduct");
    }
    return Streams.zip(byproductRates.stream(), byproducts.stream(), (type, fluid) -> rate.applyOreBoost(type.orElse(rateType), fluid)).toList();
  }
}
