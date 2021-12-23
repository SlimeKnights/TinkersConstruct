package slimeknights.tconstruct.tools.modifiers.traits.melee;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.TooltipFlag;
import slimeknights.tconstruct.library.utils.TooltipKey;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.List;

public class InsatibleModifier extends Modifier {
  public InsatibleModifier() {
    super(0x9261cc);
  }

  /** Gets the current bonus for the entity */
  private static float getBonus(LivingEntity attacker, int level) {
    int effectLevel = TinkerModifiers.insatiableEffect.get().getLevel(attacker) + 1;
    return level * effectLevel / 4f;
  }

  @Override
  public float getEntityDamage(IModifierToolStack tool, int level, ToolAttackContext context, float baseDamage, float damage) {
    // gives +2 damage per level at max
    return damage + (getBonus(context.getAttacker(), level) / 4f * tool.getModifier(ToolStats.ATTACK_DAMAGE));
  }

  @Override
  public int afterEntityHit(IModifierToolStack tool, int level, ToolAttackContext context, float damageDealt) {
    // 8 hits gets you to max, levels faster at higher levels
    if (!context.isExtraAttack() && context.isFullyCharged()) {
      LivingEntity attacker = context.getAttacker();
      int effectLevel = Math.min(7, TinkerModifiers.insatiableEffect.get().getLevel(attacker) + 1);
      TinkerModifiers.insatiableEffect.get().apply(attacker, 5 * 20, effectLevel, true);
    }
    return 0;
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, @Nullable PlayerEntity player, List<ITextComponent> tooltip, TooltipKey key, TooltipFlag flag) {
    float bonus = level * 2;
    if (player != null && key == TooltipKey.SHIFT) {
      bonus = getBonus(player, level);
    }
    addDamageTooltip(tool, bonus, tooltip);
  }
}
