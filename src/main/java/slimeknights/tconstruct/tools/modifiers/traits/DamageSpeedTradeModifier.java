package slimeknights.tconstruct.tools.modifiers.traits;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

/**
 * Shared logic for jagged and stonebound. Trait boosts attack damage as it lowers mining speed.
 */
public class DamageSpeedTradeModifier extends Modifier {
  private static final String KEY_MINING_BOOST = Util.makeTranslationKey("modifier", "damage_speed_trade.suffix");
  private final float multiplier;

  /**
   * Creates a new instance of
   * @param color       Modifier text color
   * @param multiplier  Multiplier. Positive boosts damage, negative boosts mining speed
   */
  public DamageSpeedTradeModifier(int color, float multiplier) {
    super(color);
    this.multiplier = multiplier;
  }

  @Override
  public ITextComponent getDisplayName(IModifierToolStack tool, int level) {
    double boost = Math.abs(Math.sqrt(tool.getDamage() * level) * multiplier);
    ITextComponent name = super.getDisplayName(level);
    if (boost > 0) {
      name = name.deepCopy().append(new TranslationTextComponent(KEY_MINING_BOOST, Util.dfPercent.format(boost)));
    }
    return name;
  }

  @Override
  public float applyLivingDamage(IModifierToolStack tool, int level, LivingEntity attacker, LivingEntity target, float baseDamage, float damage, boolean isCritical, boolean fullyCharged) {
    return (int)(damage * (1 + Math.sqrt(tool.getDamage() * level) * multiplier));
  }

  @Override
  public void onBreakSpeed(IModifierToolStack tool, int level, BreakSpeed event) {
    event.setNewSpeed((float)(event.getNewSpeed() * (1 - (Math.sqrt(tool.getDamage() * level) * multiplier))));
  }
}
