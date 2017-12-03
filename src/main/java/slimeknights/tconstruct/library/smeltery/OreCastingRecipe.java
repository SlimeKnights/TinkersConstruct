package slimeknights.tconstruct.library.smeltery;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import java.util.List;

import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.utils.RecipeUtil;

/**
 * A casting recipe that takes its output from an oredict entry.
 * Used for ingot casting etc.
 */
public class OreCastingRecipe extends CastingRecipe {

  /** @Deprecated use oreName instead */
  @Deprecated
  protected final List<ItemStack> outputs;
  protected final String oreName;

  /**
   * The ore list is retained internally, that means changes to the list affect the result
   * @Deprecated use {@link #OreCastingRecipe(String, RecipeMatch, Fluid, int)}
   */
  @Deprecated
  public OreCastingRecipe(List<ItemStack> ore, RecipeMatch cast, Fluid fluid, int amount) {
    this(ore, cast, new FluidStack(fluid, amount), calcCooldownTime(fluid, amount), false, false);
  }

  public OreCastingRecipe(String ore, RecipeMatch cast, Fluid fluid, int amount) {
    this(ore, cast, new FluidStack(fluid, amount), calcCooldownTime(fluid, amount), false, false);
  }

  public OreCastingRecipe(String ore, RecipeMatch cast, FluidStack fluid, int time, boolean consumesCast, boolean switchOutputs) {
    super(new ItemStack(Blocks.COBBLESTONE), cast, fluid, time, consumesCast, switchOutputs);
    this.outputs = null;
    this.oreName = ore;
  }

  /**
   * The ore list is retained internally, that means changes to the list affect the result
   * @Deprecated use {@link #OreCastingRecipe(String, RecipeMatch, FluidStack, int, boolean, boolean)}
   */
  @Deprecated
  public OreCastingRecipe(List<ItemStack> ore, RecipeMatch cast, FluidStack fluid, int time, boolean consumesCast, boolean switchOutputs) {
    super(new ItemStack(Blocks.COBBLESTONE), cast, fluid, time, consumesCast, switchOutputs);
    this.outputs = ore;
    this.oreName = null;
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
    if(oreName != null) {
      return RecipeUtil.getPreference(oreName);
    }
    // legacy logic kepy for backwards compatibility
    if(outputs.isEmpty()) {
      return ItemStack.EMPTY;
    }
    return outputs.get(0);
  }
}
