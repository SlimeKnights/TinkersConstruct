package slimeknights.tconstruct.library.recipe.melting;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.util.List;

/** Melting recipe that scale output based on input damage */
public class DamageableMeltingRecipe extends MeltingRecipe {
  public DamageableMeltingRecipe(ResourceLocation id, String group, Ingredient input, FluidStack output, int temperature, int time, List<FluidStack> byproducts) {
    super(id, group, input, output, temperature, time, byproducts);
  }

  /** Scales a fluid stack based on the damage */
  private static FluidStack scaleOutput(FluidStack fluid, int damage, int maxDamage) {
    return new FluidStack(fluid, Math.max(fluid.getAmount() * (maxDamage - damage) / maxDamage, 1));
  }

  @Override
  public FluidStack getOutput(IMeltingInventory inv) {
    FluidStack output = getOutput();
    ItemStack input = inv.getStack();
    int maxDamage = input.getMaxDamage();
    if (maxDamage <= 0) {
      return output.copy();
    }
    // scale output based on damage value, its possible 1mb is a lot for some high durability things, but whatever
    return scaleOutput(output, input.getDamage(), maxDamage);
  }

  @Override
  public void handleByproducts(IMeltingInventory inv, IFluidHandler handler) {
    ItemStack input = inv.getStack();
    int maxDamage = input.getMaxDamage();
    if (maxDamage <= 0) {
      super.handleByproducts(inv, handler);
    } else {
      // fill byproducts until we run out of space or byproducts
      int itemDamage = input.getDamage();
      for (FluidStack fluidStack : byproducts) {
        handler.fill(scaleOutput(fluidStack, itemDamage, maxDamage), FluidAction.EXECUTE);
      }
    }
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerSmeltery.damagableMeltingSerializer.get();
  }
}
