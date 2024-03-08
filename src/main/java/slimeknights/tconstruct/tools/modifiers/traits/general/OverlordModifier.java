package slimeknights.tconstruct.tools.modifiers.traits.general;

import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modifiers.slotless.OverslimeModifier;

public class OverlordModifier extends Modifier implements ToolStatsModifierHook, VolatileDataModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, TinkerHooks.TOOL_STATS, TinkerHooks.VOLATILE_DATA);
  }

  @Override
  public int getPriority() {
    return 80; // after overcast
  }

  /** Gets the durability boost per level */
  private int getBoost(StatsNBT baseStats, int level, float perLevel) {
    return (int)(baseStats.get(ToolStats.DURABILITY) * perLevel * level);
  }

  @Override
  public void addVolatileData(ToolRebuildContext context, ModifierEntry modifier, ModDataNBT volatileData) {
    OverslimeModifier overslime = TinkerModifiers.overslime.get();
    overslime.setFriend(volatileData);
    // gains +15% of the durability per level, note that base stats does not consider the durability modifier
    overslime.addCapacity(volatileData, getBoost(context.getBaseStats(), modifier.getLevel(), 0.10f * context.getDefinition().getData().getMultiplier(ToolStats.DURABILITY)));
  }

  @Override
  public void addToolStats(ToolRebuildContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
    // at most subtract 90% durability, note this runs before the tool durability modifier
    ToolStats.DURABILITY.add(builder, -getBoost(context.getBaseStats(), Math.min(modifier.getLevel(), 6), 0.15f));
  }
}
