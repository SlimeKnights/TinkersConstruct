package slimeknights.tconstruct.misc;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface IItemHandlerModifiable extends IItemHandler {

  void setStackInSlot(int slot, @NotNull ItemStack stack);
}
