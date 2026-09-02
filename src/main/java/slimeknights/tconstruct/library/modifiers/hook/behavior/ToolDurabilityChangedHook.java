package slimeknights.tconstruct.library.modifiers.hook.behavior;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Hook run after tool durability changes to allow responding to the change.
 * Due to where this hook is run, calling any methods from {@link slimeknights.tconstruct.library.tools.helper.ToolDamageUtil} is likely to cause recursion.
 */
public interface ToolDurabilityChangedHook {
  /**
   * Called after a tool is damaged by any cause. Provides information about the damage but does not allow changing it.
   * @param tool       Tool stack
   * @param modifier   Modifier running this hook
   * @param amount     Amount of damage the tool took
   * @param holder     Entity holding the tool
   * @param stack      ItemStack instance to find the tool on the player
   * @see ToolDamageModifierHook
   */
  default void afterDamageTool(IToolStackView tool, ModifierEntry modifier, int amount, @Nullable LivingEntity holder, ItemStack stack) {}

  /**
   * Called when the tool is repaired by any cause. Provides information on the repair but does not allow chaging it.
   * @param tool       Tool stack
   * @param modifier   Modifier running this hook
   * @param amount     Amount of damage the tool took
   * @see RepairFactorModifierHook
   * @see MaterialRepairModifierHook
   */
  default void afterRepairTool(IToolStackView tool, ModifierEntry modifier, int amount) {}


  /** Merger that runs all nested modules, but stops if the amount ever reaches 0 */
  record Merger(Collection<ToolDurabilityChangedHook> modules) implements ToolDurabilityChangedHook {
    @Override
    public void afterDamageTool(IToolStackView tool, ModifierEntry modifier, int amount, @Nullable LivingEntity holder, ItemStack stack) {
      for (ToolDurabilityChangedHook module : modules) {
        module.afterDamageTool(tool, modifier, amount, holder, stack);
      }
    }

    @Override
    public void afterRepairTool(IToolStackView tool, ModifierEntry modifier, int amount) {
      for (ToolDurabilityChangedHook module : modules) {
        module.afterRepairTool(tool, modifier, amount);
      }
    }
  }
}
