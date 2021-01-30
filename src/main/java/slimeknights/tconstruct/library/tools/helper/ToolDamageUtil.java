package slimeknights.tconstruct.library.tools.helper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

/**
 * Handles tool breaking and repairing without relying on the tool being {@link ToolStack}. Can also be quicker than running through {@link ToolStack} for quick updates
 */
public class ToolDamageUtil {
  /**
   * Raw method to set a tool as broken. Bypasses {@link ToolStack} for the sake of things that may not be a full Tinker Tool
   * @param stack  Tool stack
   */
  public static void breakTool(ItemStack stack) {
    stack.getOrCreateTag().putBoolean(ToolStack.TAG_BROKEN, true);
  }

  /**
   * Checks if the given stack is broken
   * @param stack  Stack to check
   * @return  True if broken
   */
  public static boolean isBroken(ItemStack stack) {
    CompoundNBT nbt = stack.getTag();
    return nbt != null && nbt.getBoolean(ToolStack.TAG_BROKEN);
  }


  /**
   * Gets the current tool durability
   *
   * @param stack the tool stack to use
   * @return the currently durability of the tool stack
   */
  public static int getCurrentDurability(ItemStack stack) {
    if (isBroken(stack)) {
      return 0;
    }
    return stack.getMaxDamage() - stack.getDamage();
  }

  /**
   * Gets the current damage the tool has taken. Essentially the reverse of {@link #getCurrentDurability(ItemStack)}
   * TODO: consider replacing definition of {@link net.minecraft.item.Item#getDamage(ItemStack)} with this, will that cause vanilla to delete the stack?
   *
   * @param stack the tool stack to use
   * @return the currently durability of the tool stack
   */
  public static int getCurrentDamage(ItemStack stack) {
    if (isBroken(stack)) {
      return stack.getMaxDamage();
    }
    return stack.getDamage();
  }

  /**
   * Checks if the given stack needs to be repaired
   * @param stack  Stack to check
   * @return  True if it needs repair
   */
  public static boolean needsRepair(ItemStack stack) {
    return stack.getDamage() > 0 || isBroken(stack);
  }
}
