package slimeknights.tconstruct.library.fluid.transfer;

import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;

/** @deprecated use {@link slimeknights.mantle.fluid.transfer.FillFluidContainerTransfer} */
@Deprecated
public class FillFluidContainerTransfer extends slimeknights.mantle.fluid.transfer.FillFluidContainerTransfer implements IFluidContainerTransfer {
  public FillFluidContainerTransfer(Ingredient input, ItemOutput filled, FluidIngredient fluid) {
    super(input, filled, fluid);
  }
}
