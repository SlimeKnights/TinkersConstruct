package slimeknights.tconstruct.library.modifiers.hook.behavior;

import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Hook run when a tool is damaged to allow modifiers to change the damage amount.
 */
public interface ToolDamageModifierHook {
  /**
   * Called when the tool is damaged. Can be used to cancel, decrease, or increase the damage.
   * @param tool       Tool stack
   * @param modifier   Modifier running this hook
   * @param amount     Amount of damage to deal
   * @param holder     Entity holding the tool
   * @return  Replacement damage. Returning 0 cancels the damage and stops other modifiers from processing.
   */
  int onDamageTool(IToolStackView tool, ModifierEntry modifier, int amount, @Nullable LivingEntity holder);

  /** Merger that runs all nested modules, but stops if the amount ever reaches 0 */
  record Merger(Collection<ToolDamageModifierHook> modules) implements ToolDamageModifierHook {
    @Override
    public int onDamageTool(IToolStackView tool, ModifierEntry modifier, int amount, @Nullable LivingEntity holder) {
      for (ToolDamageModifierHook module : modules) {
        amount = module.onDamageTool(tool, modifier, amount, holder);
        if (amount <= 0) {
          break;
        }
      }
      return amount;
    }
  }
}
