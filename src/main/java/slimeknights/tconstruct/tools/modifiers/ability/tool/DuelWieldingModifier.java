package slimeknights.tconstruct.tools.modifiers.ability.tool;

import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class DuelWieldingModifier extends OffhandAttackModifier implements ToolStatsModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, TinkerHooks.TOOL_STATS);
  }

  @Override
  public void addToolStats(ToolRebuildContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
    // on two handed tools, take a larger hit to attack damage, smaller to attack speed
    if (context.hasTag(TinkerTags.Items.BROAD_TOOLS)) {
      ToolStats.ATTACK_DAMAGE.multiplyAll(builder, 0.7);
      ToolStats.ATTACK_SPEED.multiplyAll(builder, 0.9);
    } else {
      // on one handed tools, 80% on both
      ToolStats.ATTACK_DAMAGE.multiplyAll(builder, 0.8);
      ToolStats.ATTACK_SPEED.multiplyAll(builder, 0.8);
    }
  }

  @Override
  public boolean shouldDisplay(boolean advanced) {
    return true;
  }
}
