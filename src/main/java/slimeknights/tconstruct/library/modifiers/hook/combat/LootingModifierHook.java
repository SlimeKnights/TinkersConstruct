package slimeknights.tconstruct.library.modifiers.hook.combat;

import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.tools.context.LootingContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

/** Modifier hooks for wearing leggings boosting loot. One as a module in case you want to only apply the bonus to leggings/not leggings */
public interface LootingModifierHook {
  /**
   * Gets the amount to boost the tool's luck by
   * @param tool       Tool instance
   * @param modifier   Modifier entry calling the hook
   * @param context    Context about the looting
   * @param looting    Looting value set from previous modifiers. May be negative, will be normalized post modifier calls.
   * @return New looting value, may be negative
   */
  int updateLooting(IToolStackView tool, ModifierEntry modifier, LootingContext context, int looting);


  /* Helpers */

  /**
   * Gets the looting value for the given tool
   * @param tool     Tool instance
   * @param context  Context about the looting
   * @param looting  Original looting value, typically from enchantments
   * @return  Looting value for the tool
   */
  static int getLooting(IToolStackView tool, LootingContext context, int looting) {
    if (!tool.isBroken()) {
      for (ModifierEntry entry : tool.getModifierList()) {
        looting = entry.getHook(TinkerHooks.WEAPON_LOOTING).updateLooting(tool, entry, context, looting);
      }
    }
    return looting;
  }


  /** Constructor for a merger that sums all children */
  record ComposeMerger(Collection<LootingModifierHook> modules) implements LootingModifierHook {
    @Override
    public int updateLooting(IToolStackView tool, ModifierEntry modifier, LootingContext context, int looting) {
      for (LootingModifierHook module : modules) {
        looting = module.updateLooting(tool, modifier, context, looting);
      }
      return looting;
    }
  }
}
