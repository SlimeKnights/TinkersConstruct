package slimeknights.tconstruct.tools.modifiers.traits.melee;

import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class LevelDamageModifier extends Modifier {
  private final float damage;
  public LevelDamageModifier(int color, float damage) {
    super(color);
    this.damage = damage;
  }

  @Override
  public void addToolStats(ToolRebuildContext context, int level, ModifierStatsBuilder builder) {
    ToolStats.ATTACK_DAMAGE.add(builder, damage * level);
  }
}
