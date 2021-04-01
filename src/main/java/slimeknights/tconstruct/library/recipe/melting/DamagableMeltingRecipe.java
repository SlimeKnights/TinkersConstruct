package slimeknights.tconstruct.library.recipe.melting;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

/** Melting recipe that scale output based on input damage */
public class DamagableMeltingRecipe extends MeltingRecipe {
  public DamagableMeltingRecipe(ResourceLocation id, String group, Ingredient input, FluidStack output, int temperature, int time) {
    super(id, group, input, output, temperature, time);
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
    return new FluidStack(output, Math.max(output.getAmount() * (maxDamage - input.getDamage()) / maxDamage, 1));
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerSmeltery.damagableMeltingSerializer.get();
  }
}
