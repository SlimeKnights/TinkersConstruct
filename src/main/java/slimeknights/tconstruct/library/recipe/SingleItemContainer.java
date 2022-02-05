package slimeknights.tconstruct.library.recipe;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.recipe.container.ISingleStackContainer;

/** Simple class for an inventory containing just one item */
public class SingleItemContainer implements ISingleStackContainer {
  @Getter @Setter
  private ItemStack stack = ItemStack.EMPTY;
}
