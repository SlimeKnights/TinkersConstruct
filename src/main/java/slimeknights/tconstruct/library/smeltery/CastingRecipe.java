package slimeknights.tconstruct.library.smeltery;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;

public class CastingRecipe {

  public final RecipeMatch cast;
  public final FluidStack fluid;
  public final ItemStack output;


  public CastingRecipe(ItemStack output, RecipeMatch cast, Fluid fluid, int amount) {
    this(output, cast,  new FluidStack(fluid, amount));
  }

  public CastingRecipe(ItemStack output, Fluid fluid, int amount) {
    this(output, null,  new FluidStack(fluid, amount));
  }

  public CastingRecipe(ItemStack output, RecipeMatch cast, FluidStack fluid) {
    this.output = output;
    this.cast = cast;
    this.fluid = fluid;
  }

  public boolean matches(ItemStack cast, Fluid fluid) {
    if((cast == null && this.cast == null) || this.cast.matches(new ItemStack[]{cast}) != null) {
      return this.fluid.getFluid() == fluid;
    }
    return false;
  }

  public ItemStack getResult() {
    return output.copy();
  }
}
