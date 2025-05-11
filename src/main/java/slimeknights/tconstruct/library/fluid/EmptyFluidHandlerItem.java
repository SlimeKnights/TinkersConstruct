package slimeknights.tconstruct.library.fluid;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;

/** Empty fluid handler item instance, usable like {@link EmptyFluidHandler#INSTANCE} */
@RequiredArgsConstructor
public class EmptyFluidHandlerItem extends EmptyFluidHandler implements IFluidHandlerItem {
  public static final EmptyFluidHandlerItem INSTANCE = new EmptyFluidHandlerItem(ItemStack.EMPTY);

  /** Container reference */
  @Getter
  private final ItemStack container;
}
