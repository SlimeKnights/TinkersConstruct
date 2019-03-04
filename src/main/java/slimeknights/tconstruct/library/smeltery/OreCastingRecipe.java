package slimeknights.tconstruct.library.smeltery;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

import slimeknights.mantle.util.RecipeMatch;

/**
 * A casting recipe that takes its output from an oredict entry.
 * Used for ingot casting etc.
 * @Deprecated Use {@link PreferenceCastingRecipe}
 */
@Deprecated
public class OreCastingRecipe extends CastingRecipe {

  protected final List<ItemStack> outputs;

  public OreCastingRecipe(List<ItemStack> ore, RecipeMatch cast, Fluid fluid, int amount) {
    this(ore, cast, new FluidStack(fluid, amount), calcCooldownTime(fluid, amount), false, false);
  }

  public OreCastingRecipe(String ore, RecipeMatch cast, Fluid fluid, int amount) {
    this(OreDictionary.getOres(ore), cast, new FluidStack(fluid, amount), calcCooldownTime(fluid, amount), false, false);
  }

  public OreCastingRecipe(String ore, RecipeMatch cast, FluidStack fluid, int time, boolean consumesCast, boolean switchOutputs) {
    this(OreDictionary.getOres(ore), cast, fluid, time, consumesCast, switchOutputs);
  }

  /** The ore list is retained internally, that means changes to the list affect the result */
  public OreCastingRecipe(List<ItemStack> ore, RecipeMatch cast, FluidStack fluid, int time, boolean consumesCast, boolean switchOutputs) {
    super(new ItemStack(Blocks.COBBLESTONE), cast, fluid, time, consumesCast, switchOutputs);
    this.outputs = ore;
  }

  @Override
  public boolean matches(ItemStack cast, Fluid fluid) {
    // always return false if there is no output
    return !outputs.isEmpty() && super.matches(cast, fluid);
  }

  @Override
  public ItemStack getResult(ItemStack cast, Fluid fluid) {
    return getResult().copy();
  }

  @Override
  public ItemStack getResult() {
    if(outputs.isEmpty()) {
      return ItemStack.EMPTY;
    }
    return outputs.get(0);
  }
}