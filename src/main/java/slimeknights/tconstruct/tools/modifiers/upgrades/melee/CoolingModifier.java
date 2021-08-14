package slimeknights.tconstruct.tools.modifiers.upgrades.melee;

import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.List;

public class CoolingModifier extends IncrementalModifier {
  public CoolingModifier() {
    super(0x649832);
  }

  @Override
  public float getEntityDamage(IModifierToolStack tool, int level, ToolAttackContext context, float baseDamage, float damage) {
    if (context.getTarget().isImmuneToFire()) {
      damage += getScaledLevel(tool, level) * 2f * tool.getModifier(ToolStats.ATTACK_DAMAGE);
    }
    return damage;
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, boolean isAdvanced, boolean detailed) {
    addDamageTooltip(tool, level, 2f, tooltip);
  }
}
