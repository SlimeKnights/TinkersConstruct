package slimeknights.tconstruct.tools.modifiers.traits.melee;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.TooltipKey;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.List;

public class SearingModifier extends Modifier {
  private static final float BASELINE_TEMPERATURE = 0.75f;

  @Override
  public void addVolatileData(ToolRebuildContext context, int level, ModDataNBT volatileData) {
    TinkerModifiers.tank.get().addCapacity(volatileData, FluidValues.BRICK);
  }

  /** Applies the temperature boost */
  private static float temperatureBoost(LivingEntity living, int level) {
    // produces 0 at 0.75t. Caps at level * 2.5 at 2.0t, or at level * -2.5 at -0.5t
    BlockPos attackerPos = living.blockPosition();
    // TODO: temperature update
    return (living.level.getBiome(attackerPos).getTemperature(attackerPos) - BASELINE_TEMPERATURE) * (level * 2);
  }

  @Override
  public float getEntityDamage(IToolStackView tool, int level, ToolAttackContext context, float baseDamage, float damage) {
    return damage + temperatureBoost(context.getAttacker(), level) * tool.getMultiplier(ToolStats.ATTACK_DAMAGE);
  }

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    float bonus;
    if (player != null && tooltipKey == TooltipKey.SHIFT) {
      bonus = temperatureBoost(player, level);
    } else {
      bonus = level * 2.5f;
    }
    addDamageTooltip(tool, bonus, tooltip);
  }
}
