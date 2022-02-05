package slimeknights.tconstruct.tools.modifiers.upgrades.melee;

import lombok.RequiredArgsConstructor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.tconstruct.library.modifiers.impl.IncrementalModifier;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.TooltipKey;

import javax.annotation.Nullable;
import java.util.List;

/** Shared logic for all modifiers that boost damage against a creature type */
@RequiredArgsConstructor
public class ScaledTypeDamageModifier extends IncrementalModifier {
  private final MobType type;

  /**
   * Method to check if this modifier is effective on the given entity
   * @param target  Entity
   * @return  True if effective
   */
  protected boolean isEffective(LivingEntity target) {
    return target.getMobType() == type;
  }

  @Override
  public float getEntityDamage(IToolStackView tool, int level, ToolAttackContext context, float baseDamage, float damage) {
    LivingEntity target = context.getLivingTarget();
    if (target != null && isEffective(target)) {
      damage += getScaledLevel(tool, level) * 2.5f * tool.getMultiplier(ToolStats.ATTACK_DAMAGE);
    }
    return damage;
  }

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    addDamageTooltip(tool, level, 2.5f, tooltip);
  }
}
