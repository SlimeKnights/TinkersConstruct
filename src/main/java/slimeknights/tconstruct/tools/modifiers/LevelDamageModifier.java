package slimeknights.tconstruct.tools.modifiers;

import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.tools.ToolStatsModifierBuilder;

public class LevelDamageModifier extends Modifier {
  private final float damage;
  public LevelDamageModifier(int color, float damage) {
    super(color);
    this.damage = damage;
  }

  @Override
  public void addToolStats(IModDataReadOnly persistentData, IModDataReadOnly volatileData, int level, ToolStatsModifierBuilder builder) {
    builder.addAttackDamage(damage * level);
  }
}
