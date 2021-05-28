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
    addDamageTooltip(this, tool, level, 2.5f, tooltip);
  }

  /**
   * Adds a tooltip showing the bonus damage and the type of damage dded
   * @param self         Modifier instance
   * @param tool         Tool instance
   * @param level        Current level
   * @param levelAmount  Bonus per level
   * @param tooltip      Tooltip
   */
  public static void addDamageTooltip(IncrementalModifier self, IModifierToolStack tool, int level, float levelAmount, List<ITextComponent> tooltip) {
    tooltip.add(self.applyStyle(new StringTextComponent("+" + Util.df.format(self.getScaledLevel(tool, level) * levelAmount))
                                  .appendString(" ")
                                  .append(new TranslationTextComponent(self.getTranslationKey() + ".damage"))));
  }
}
