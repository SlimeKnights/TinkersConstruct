package slimeknights.tconstruct.tools.modifiers.traits;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.utils.Util;

import java.util.List;

/** Deals a percentage boost in damage when on fire */
public class ConductingModifier extends Modifier {
  private static final ITextComponent ATTACK_DAMAGE = TConstruct.makeTranslation("modifier", "conducting.attack_damage");
  private static final int MAX_BONUS_TICKS = 15 * 20; // time from lava
  private static final float PERCENT_PER_LEVEL = 0.15f; // 15% bonus when in lava essentially
  public ConductingModifier() {
    super(0xDBCC0B);
  }

  @Override
  public int getPriority() {
    return 90;
  }

  @Override
  public float getEntityDamage(IModifierToolStack tool, int level, ToolAttackContext context, float baseDamage, float damage) {
    LivingEntity attacker = context.getAttacker();
    int fire = attacker.getFireTimer();
    if (fire > 0) {
      float bonus = PERCENT_PER_LEVEL * level;
      // if less than 15 seconds of fire, smaller boost
      if (fire < MAX_BONUS_TICKS) {
        bonus *= (float)(fire) / MAX_BONUS_TICKS;
      }
      // half boost if not on fire
      if (attacker.isPotionActive(Effects.FIRE_RESISTANCE)) {
        bonus /= 2;
      }
      // finally, apply percentage
      if (bonus > 0) {
        damage *= 1 + bonus;
      }
    }
    return damage;
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, boolean isAdvanced, boolean detailed) {
    if (tool.hasTag(TinkerTags.Items.MELEE)) {
      tooltip.add(applyStyle(new StringTextComponent(Util.PERCENT_BOOST_FORMAT.format(PERCENT_PER_LEVEL * level)).appendString(" ").appendSibling(ATTACK_DAMAGE)));
    }
  }
}
