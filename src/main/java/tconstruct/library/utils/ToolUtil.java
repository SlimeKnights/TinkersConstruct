package tconstruct.library.utils;

import net.minecraft.item.ItemStack;

import tconstruct.library.tinkering.IToolPart;
import tconstruct.library.tinkering.Material;

public final class ToolUtil {

  private ToolUtil() {
  }

  public static Material getMaterialFromStack(ItemStack stack) {
    if (stack == null || stack.getItem() == null) {
      return Material.UNKNOWN;
    }
    if (!(stack.getItem() instanceof IToolPart)) {
      return Material.UNKNOWN;
    }

    return ((IToolPart) stack.getItem()).getMaterial(stack);
  }
}
