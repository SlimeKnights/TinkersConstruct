package slimeknights.tconstruct.library.recipe.melting;

import com.google.common.collect.Streams;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer.OreRateType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.util.List;

/**
 * Extension of melting recipe to boost results of ores
 */
public class OreMeltingRecipe extends MeltingRecipe {
  @Getter
  private final OreRateType oreType;
  public OreMeltingRecipe(ResourceLocation id, String group, Ingredient input, FluidStack output, int temperature, int time, List<FluidStack> byproducts, OreRateType oreType) {
    super(id, group, input, output, temperature, time, byproducts);
    this.oreType = oreType;
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

  public static class Serializer extends MeltingRecipe.AbstractSerializer<OreMeltingRecipe> {
    @Override
    protected OreMeltingRecipe createFromJson(ResourceLocation id, String group, Ingredient input, FluidStack output, int temperature, int time, List<FluidStack> byproducts, JsonObject json) {
      OreRateType rate = OreRateType.parse(json, "rate");
      // multiply byproducts by the config amount, config is loaded, and this prevents running it twice (once on read from network)
      List<FluidStack> scaledByproducts;
      if (json.has("byproducts")) {
        List<OreRateType> list = JsonHelper.parseList(json, "byproducts", (e, n) -> {
          JsonObject byproduct = e.getAsJsonObject();
          if (byproduct.has("rate")) {
            return OreRateType.parse(e.getAsJsonObject(), "rate");
          }
          return rate;
        });
        if (list.size() != byproducts.size()) {
          throw new JsonSyntaxException("Wrong number of byproduct rates passed, must have one per byproduct");
        }
        scaledByproducts = Streams.zip(list.stream(), byproducts.stream(), Config.COMMON.foundryByproductRate::applyOreBoost).toList();
      } else {
        scaledByproducts = byproducts.stream().map(fluid -> Config.COMMON.foundryByproductRate.applyOreBoost(rate, fluid)).toList();
      }
      return new OreMeltingRecipe(id, group, input, output, temperature, time, scaledByproducts, rate);
    }

    @Override
    protected OreMeltingRecipe createFromNetwork(ResourceLocation id, String group, Ingredient input, FluidStack output, int temperature, int time, List<FluidStack> byproducts, FriendlyByteBuf buffer) {
      OreRateType rate = buffer.readEnum(OreRateType.class);
      return new OreMeltingRecipe(id, group, input, output, temperature, time, byproducts, rate);
    }

    @Override
    protected void toNetworkSafe(FriendlyByteBuf buffer, OreMeltingRecipe recipe) {
      super.toNetworkSafe(buffer, recipe);
      buffer.writeEnum(recipe.oreType);
    }
  }
}
