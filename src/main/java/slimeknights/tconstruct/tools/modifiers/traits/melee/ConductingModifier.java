package slimeknights.tconstruct.tools.modifiers.traits.melee;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.utils.TooltipFlag;
import slimeknights.tconstruct.library.utils.TooltipKey;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
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

  /** Gets the bonus damage for the given entity and level */
  private static float getBonus(LivingEntity living, int level) {
    int fire = living.getFireTimer();
    if (fire > 0) {
      float bonus = PERCENT_PER_LEVEL * level;
      // if less than 15 seconds of fire, smaller boost
      if (fire < MAX_BONUS_TICKS) {
        bonus *= (float)(fire) / MAX_BONUS_TICKS;
      }
      // half boost if not on fire
      if (living.isPotionActive(Effects.FIRE_RESISTANCE)) {
        bonus /= 2;
      }
      return bonus;
    }
    return 0;
  }

  @Override
  public float getEntityDamage(IModifierToolStack tool, int level, ToolAttackContext context, float baseDamage, float damage) {
    float bonus = getBonus(context.getAttacker(), level);
    if (bonus > 0) {
      damage *= 1 + bonus;
    }
    return damage;
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, @Nullable PlayerEntity player, List<ITextComponent> tooltip, TooltipKey key, TooltipFlag flag) {
    if (tool.hasTag(TinkerTags.Items.MELEE_OR_UNARMED)) {
      float bonus = PERCENT_PER_LEVEL * level;
      // client only knows if the player is on fire or not, not the amount of fire, so just show full if on fire
      if (player != null && key == TooltipKey.SHIFT && player.getFireTimer() == 0) {
        bonus = 0;
      }
      tooltip.add(applyStyle(new StringTextComponent(Util.PERCENT_BOOST_FORMAT.format(bonus)).appendString(" ").appendSibling(ATTACK_DAMAGE)));
    }
  }
}
