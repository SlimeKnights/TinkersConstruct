package slimeknights.tconstruct.tools.modifiers.traits.melee;

import lombok.RequiredArgsConstructor;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

@RequiredArgsConstructor
public class LevelDamageModifier extends Modifier {
  private final float damage;

  @Override
  public void addToolStats(ToolRebuildContext context, int level, ModifierStatsBuilder builder) {
    ToolStats.ATTACK_DAMAGE.add(builder, damage * level);
  }
}
