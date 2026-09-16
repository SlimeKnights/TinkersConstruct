package slimeknights.tconstruct.library.modifiers.hook.build;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.Collection;

/**
 * Hook for when a tool is crafted.
 * @see ValidateModifierHook
 */
public interface CraftCountModifierHook {
  /**
   * Called when a tool is crafted to let you change the amount crafted. This hook is also called on part swap and related places to prevent dupes, so applying on craft sideeffects is discouraged.
   * Note this hook is only called on crafting in the tinker station or anvil. Tools crafted by other means (such as casting) will not call it, nor will tool modifications.
   * Hook is really only intended to be used by ammo, such as arrows.
   * @param tool    Tool instance
   * @param entry   Modifier running the hook
   * @param amount  Amount crafted
   * @return  New amount crafted. Will be floored if a non-whole number. If 0, nothing will be crafted.
   */
  float modifyCraftCount(IToolStackView tool, ModifierEntry entry, float amount);

  /** Merger running each hook */
  record ComposeMerger(Collection<CraftCountModifierHook> modules) implements CraftCountModifierHook {
    @Override
    public float modifyCraftCount(IToolStackView tool, ModifierEntry entry, float amount) {
      for (CraftCountModifierHook module : modules) {
        amount = module.modifyCraftCount(tool, entry, amount);
        if (amount <= 0) {
          return 0;
        }
      }
      return amount;
    }
  }


  /* Helpers */

  /** Gets the max stack size for the given tool, calling the modifier hook */
  static int maxStackSize(IToolStackView tool, float count) {
    // don't bother running if the tool can't go above count 1. Prevents the trait from being abused on non-ammo
    int itemMax = tool.getItem().getMaxStackSize();
    if (itemMax == 1) return 1;
    for (ModifierEntry entry : tool.getModifiers()) {
      count = entry.getHook(ModifierHooks.CRAFT_COUNT).modifyCraftCount(tool, entry, count);
      if (count <= 0) {
        return 0;
      }
    }
    return (int) Math.min(count, itemMax);
  }

  /** Creates a stack with the max size from the given materials and focus, running the material stack size hook as needed. */
  static ItemStack createDisplayStack(MaterialIdNBT materials, ItemLike focus, int count) {
    ItemStack stack = materials.updateStack(new ItemStack(focus));
    if (stack.getMaxStackSize() > 1) {
      ToolStack tool = ToolStack.from(stack);
      tool.rebuildStats();
      stack.setCount(maxStackSize(tool, count));
    }
    return stack;
  }

}
