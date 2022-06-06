package slimeknights.tconstruct.library.fluid.transfer;

import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;

/** @deprecated use {@link slimeknights.mantle.fluid.transfer.FillFluidWithNBTTransfer} */
@Deprecated
public class FillFluidWithNBTTransfer extends slimeknights.mantle.fluid.transfer.FillFluidWithNBTTransfer implements IFluidContainerTransfer {
  public FillFluidWithNBTTransfer(Ingredient input, ItemOutput filled, FluidIngredient fluid) {
    super(input, filled, fluid);
  }
}
