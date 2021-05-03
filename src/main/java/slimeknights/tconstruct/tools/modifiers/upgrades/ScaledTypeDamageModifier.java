package slimeknights.tconstruct.tools.modifiers.upgrades;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import java.util.List;

/** Shared logic for all modifiers that boost damage against a creature type */
public class ScaledTypeDamageModifier extends IncrementalModifier {
  private final CreatureAttribute type;
  public ScaledTypeDamageModifier(int color, CreatureAttribute type) {
    super(color);
    this.type = type;
  }

  @Override
  public float applyLivingDamage(IModifierToolStack tool, int level, LivingEntity attackerLiving, LivingEntity targetLiving, float baseDamage, float damage, boolean isCritical, boolean fullyCharged) {
    if (targetLiving.getCreatureAttribute() == type) {
      damage += getScaledLevel(tool, level) * 2.5f;
    }
    return damage;
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, boolean isAdvanced, boolean detailed) {
    tooltip.add(applyStyle(new StringTextComponent("+" + Util.df.format(getScaledLevel(tool, level) * 2.5f))
                             .appendString(" ")
                             .append(new TranslationTextComponent(getTranslationKey() + ".damage"))));
  }
}
