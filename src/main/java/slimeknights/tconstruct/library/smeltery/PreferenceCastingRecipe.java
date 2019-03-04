package slimeknights.tconstruct.library.smeltery;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.utils.RecipeUtil;

/**
 * A casting recipe that takes its output from an oredict entry using the preference system to determine the output
 * Used for ingot casting etc.
 */
public class PreferenceCastingRecipe extends CastingRecipe {

  protected final String oreName;

  public PreferenceCastingRecipe(String ore, RecipeMatch cast, Fluid fluid, int amount) {
    this(ore, cast, new FluidStack(fluid, amount), calcCooldownTime(fluid, amount), false, false);
  }

  public PreferenceCastingRecipe(String ore, RecipeMatch cast, FluidStack fluid, int time, boolean consumesCast, boolean switchOutputs) {
    super(new ItemStack(Blocks.COBBLESTONE), cast, fluid, time, consumesCast, switchOutputs);
    this.oreName = ore;
  }

  @Override
  public boolean matches(ItemStack cast, Fluid fluid) {
    // always return false if there is no output
    return !getResult().isEmpty() && super.matches(cast, fluid);
  }

  @Override
  public ItemStack getResult(ItemStack cast, Fluid fluid) {
    return getResult().copy();
  }

  @Override
  public ItemStack getResult() {
    return RecipeUtil.getPreference(oreName);
  }
}
