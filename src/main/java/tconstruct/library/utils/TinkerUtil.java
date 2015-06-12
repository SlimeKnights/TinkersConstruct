package tconstruct.library.utils;

import net.minecraft.item.ItemStack;

import tconstruct.library.tinkering.Material;
import tconstruct.library.tools.IToolPart;

public final class TinkerUtil {

  private TinkerUtil() {
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
