package tconstruct.library.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import tconstruct.library.tinkering.ITinkerable;
import tconstruct.library.tools.IToolPart;
import tconstruct.library.tinkering.Material;

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

  /**
   * Returns the child NBTTag with all the tinker data for the item or Null if it has none.
   */
  public static NBTTagCompound getTinkerTag(ItemStack stack) {
    if (stack == null || stack.getItem() == null) {
      return null;
    }
    if (!stack.hasTagCompound()) {
      return null;
    }
    if (!(stack.getItem() instanceof ITinkerable)) {
      return null;
    }

    ITinkerable tinkerable = (ITinkerable) stack.getItem();
    if (!stack.getTagCompound().hasKey(tinkerable.getTagName())) {
      return null;
    }

    return stack.getTagCompound().getCompoundTag(tinkerable.getTagName());
  }
}
