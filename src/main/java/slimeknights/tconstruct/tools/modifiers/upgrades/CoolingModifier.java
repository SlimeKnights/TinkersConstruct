package slimeknights.tconstruct.tools.modifiers.upgrades;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import java.util.List;

public class CoolingModifier extends IncrementalModifier {
  public CoolingModifier() {
    super(0x649832);
  }

  @Override
  public float applyLivingDamage(IModifierToolStack tool, int level, LivingEntity attacker, LivingEntity target, float baseDamage, float damage, boolean isCritical, boolean fullyCharged, boolean isExtraAttack) {
    if (target.isImmuneToFire()) {
      damage += getScaledLevel(tool, level) * 2f;
    }
    return damage;
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, boolean isAdvanced, boolean detailed) {
    ScaledTypeDamageModifier.addDamageTooltip(this, tool, level, 2f, tooltip);
  }
}
