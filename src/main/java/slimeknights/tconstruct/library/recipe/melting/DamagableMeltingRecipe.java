package slimeknights.tconstruct.library.recipe.melting;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

/** Melting recipe that scale output based on input damage */
public class DamagableMeltingRecipe extends MeltingRecipe {
  public DamagableMeltingRecipe(Identifier id, String group, Ingredient input, FluidVolume output, int temperature, int time) {
    super(id, group, input, output, temperature, time);
  }

  @Override
  public FluidVolume getOutput(IMeltingInventory inv) {
    FluidVolume output = getFOutput();
    ItemStack input = inv.getStack();
    int maxDamage = input.getMaxDamage();
    if (maxDamage <= 0) {
      return output.copy();
    }
    // scale output based on damage value, its possible 1mb is a lot for some high durability things, but whatever
    return FluidVolume.create(output.getRawFluid(), Math.max(output.getAmount() * (maxDamage - input.getDamage()) / maxDamage, 1));
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerSmeltery.damagableMeltingSerializer;
  }

//  @Override
  public FluidVolume getFOutput() {
    throw new RuntimeException("CRAB!"); // FIXME: PORT
//    return super.getOutput();
  }
}
