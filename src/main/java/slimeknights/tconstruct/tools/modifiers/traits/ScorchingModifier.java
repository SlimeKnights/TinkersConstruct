package slimeknights.tconstruct.tools.modifiers.traits;

import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class ScorchingModifier extends Modifier {
  public ScorchingModifier() {
    super(0x5B4C43);
  }

  @Override
  public void addVolatileData(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, int level, ModDataNBT volatileData) {
    TinkerModifiers.tank.get().addCapacity(volatileData, MaterialValues.INGOT * 2);
  }

  @Override
  public float getEntityDamage(IModifierToolStack tool, int level, ToolAttackContext context, float baseDamage, float damage) {
    if (context.getTarget().isBurning()) {
      damage += 2f * level;
    }
    return damage;
  }
}
