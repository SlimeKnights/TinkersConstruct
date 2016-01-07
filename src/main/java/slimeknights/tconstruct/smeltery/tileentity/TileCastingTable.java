package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;

public class TileCastingTable extends TileCasting {

  @Override
  protected CastingRecipe findRecipe(ItemStack cast, Fluid fluid) {
    return TinkerRegistry.getTableCasting(cast, fluid);
  }
}
