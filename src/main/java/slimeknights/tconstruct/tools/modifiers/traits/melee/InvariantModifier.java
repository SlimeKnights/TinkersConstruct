package slimeknights.tconstruct.tools.modifiers.traits.melee;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.TooltipKey;

import javax.annotation.Nullable;
import java.util.List;

public class InvariantModifier extends Modifier {
  private static final float BASELINE_TEMPERATURE = 0.75f;
  private static final float MAX_TEMPERATURE = 1.25f;

  /** Gets the bonus for this modifier */
  private static float getBonus(LivingEntity living, int level) {
    // temperature ranges from 0 to 1.25. multiplication makes it go from 0 to 2.5
    BlockPos pos = living.blockPosition();
    return ((MAX_TEMPERATURE - Math.abs(BASELINE_TEMPERATURE - living.level.getBiome(pos).value().getTemperature(pos))) * level * 2f);
  }

  @Override
  public float getEntityDamage(IToolStackView tool, int level, ToolAttackContext context, float baseDamage, float damage) {
    return damage + getBonus(context.getAttacker(), level);
  }

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey key, TooltipFlag flag) {
    float bonus;
    if (player != null && key == TooltipKey.SHIFT) {
      bonus = getBonus(player, level);
    } else {
      bonus = level * 2.5f;
    }
    if (bonus > 0.01f) {
      addDamageTooltip(tool, bonus, tooltip);
    }
  }
}
