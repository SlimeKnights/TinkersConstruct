package slimeknights.tconstruct.library.recipe.melting;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.util.List;
import java.util.stream.StreamSupport;

/** Melting recipe that scale output based on input damage */
public class DamageableMeltingRecipe extends MeltingRecipe {
  private static final int[] EMPTY_SIZE = {};

  /** Sizes of each unit in the recipe. Index 0 is the main output, 1 and onwards is secondary outputs */
  private final int unitSize;
  /** Sizes of byproducts */
  private final int[] byproductSizes;
  public DamageableMeltingRecipe(ResourceLocation id, String group, Ingredient input, FluidStack output, int temperature, int time, List<FluidStack> byproducts, int unitSize, int... byproductSizes) {
    super(id, group, input, output, temperature, time, byproducts);
    this.unitSize = unitSize;
    this.byproductSizes = byproductSizes;
  }

  /** Scales a fluid stack based on the damage */
  private static FluidStack scaleOutput(FluidStack fluid, int damage, int maxDamage, int unitSize) {
    int amount = fluid.getAmount() * (maxDamage - damage) / maxDamage;
    // mimimum output is one unit
    if (amount <= unitSize) {
      amount = Math.max(unitSize, 1);
    } else if (unitSize > 1) {
      // round down to the nearest unit
      int remainder = amount % unitSize;
      if (remainder > 0) {
        amount -= remainder;
      }
    }
    return new FluidStack(fluid, amount);
  }

  @Override
  public FluidStack getOutput(IMeltingContainer inv) {
    FluidStack output = getOutput();
    ItemStack input = inv.getStack();
    int maxDamage = input.getMaxDamage();
    if (maxDamage <= 0) {
      return output.copy();
    }
    // scale output based on damage value, its possible 1mb is a lot for some high durability things, but whatever
    return scaleOutput(output, input.getDamageValue(), maxDamage, unitSize);
  }

  @Override
  public void handleByproducts(IMeltingContainer inv, IFluidHandler handler) {
    ItemStack input = inv.getStack();
    int maxDamage = input.getMaxDamage();
    if (maxDamage <= 0) {
      super.handleByproducts(inv, handler);
    } else {
      // fill byproducts until we run out of space or byproducts
      int itemDamage = input.getDamageValue();
      for (int i = 0; i < byproducts.size(); i++) {
        FluidStack fluidStack = byproducts.get(i);
        handler.fill(scaleOutput(fluidStack, itemDamage, maxDamage, i < byproductSizes.length ? byproductSizes[i] : unitSize), FluidAction.EXECUTE);
      }
    }
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerSmeltery.damagableMeltingSerializer.get();
  }

  public static class Serializer extends MeltingRecipe.AbstractSerializer<DamageableMeltingRecipe> {

    @Override
    protected DamageableMeltingRecipe createFromJson(ResourceLocation id, String group, Ingredient input, FluidStack output, int temperature, int time, List<FluidStack> byproducts, JsonObject json) {
      int unitSize = GsonHelper.getAsInt(GsonHelper.getAsJsonObject(json, "result"), "unit_size", 1);
      int[] byproductSizes = EMPTY_SIZE;
      if (json.has("byproducts")) {
        byproductSizes = StreamSupport.stream(GsonHelper.getAsJsonArray(json, "byproducts").spliterator(), false)
                                 .mapToInt(element -> GsonHelper.getAsInt(element.getAsJsonObject(), "unit_size", 1))
                                 .toArray();
      }
      return new DamageableMeltingRecipe(id, group, input, output, temperature, time, byproducts, unitSize, byproductSizes);
    }

    @Override
    protected DamageableMeltingRecipe createFromNetwork(ResourceLocation id, String group, Ingredient input, FluidStack output, int temperature, int time, List<FluidStack> byproducts, FriendlyByteBuf buffer) {
      int unitSize = buffer.readVarInt();
      int[] byproductSizes = buffer.readVarIntArray();
      return new DamageableMeltingRecipe(id, group, input, output, temperature, time, byproducts, unitSize, byproductSizes);
    }

    @Override
    protected void toNetworkSafe(FriendlyByteBuf buffer, DamageableMeltingRecipe recipe) {
      super.toNetworkSafe(buffer, recipe);
      buffer.writeVarInt(recipe.unitSize);
      buffer.writeVarIntArray(recipe.byproductSizes);
    }
  }
}
