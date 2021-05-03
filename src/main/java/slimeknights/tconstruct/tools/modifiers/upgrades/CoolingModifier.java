package slimeknights.tconstruct.tools.modifiers.upgrades;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import java.util.List;

public class CoolingModifier extends IncrementalModifier {
  public CoolingModifier() {
    super(0x91C5B7);
  }

  @Override
  public float applyLivingDamage(IModifierToolStack tool, int level, LivingEntity attacker, LivingEntity target, float baseDamage, float damage, boolean isCritical, boolean fullyCharged) {
    if (target.isImmuneToFire()) {
      damage += getScaledLevel(tool, level) * 1.5f;
    }
    return damage;
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, boolean isAdvanced, boolean detailed) {
    tooltip.add(applyStyle(new StringTextComponent("+" + Util.df.format(getScaledLevel(tool, level) * 1.5f))
                             .appendString(" ")
                             .append(new TranslationTextComponent(getTranslationKey() + ".damage"))));
  }
}
