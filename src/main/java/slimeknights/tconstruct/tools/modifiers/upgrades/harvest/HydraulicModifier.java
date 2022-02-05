package slimeknights.tconstruct.tools.modifiers.upgrades.harvest;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.impl.IncrementalModifier;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.TooltipKey;

import javax.annotation.Nullable;
import java.util.List;

public class HydraulicModifier extends IncrementalModifier {
  @Override
  public int getPriority() {
    return 125; // run before trait boosts such as dwarven
  }

  /** Gets the bonus for the living entity */
  private static float getBonus(LivingEntity living) {
    float bonus = 0;
    // highest bonus in water
    if (living.isEyeInFluid(FluidTags.WATER)) {
      bonus = 8;
    } else if (living.getCommandSenderWorld().isRainingAt(living.blockPosition())) {
      // partial bonus in the rain
      bonus = 4;
    }
    return bonus;
  }

  @Override
  public void onBreakSpeed(IToolStackView tool, int level, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
    if (!isEffective) {
      return;
    }
    Player player = event.getPlayer();
    float bonus = getBonus(player);
    if (bonus > 0) {
      // if not enchanted with aqua affinity, multiply by 5 to cancel out the effects of water
      if (!ModifierUtil.hasAquaAffinity(player) && player.isEyeInFluid(FluidTags.WATER)) {
        bonus *= 5;
      }
      bonus *= getScaledLevel(tool, level) * tool.getMultiplier(ToolStats.MINING_SPEED) * miningSpeedModifier;
      event.setNewSpeed(event.getNewSpeed() + bonus);
    }
  }

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey key, TooltipFlag flag) {
    float bonus = 8;
    if (player != null && key == TooltipKey.SHIFT) {
      bonus = getBonus(player);
    }
    addStatTooltip(tool, ToolStats.MINING_SPEED, TinkerTags.Items.HARVEST, bonus * getScaledLevel(tool, level), tooltip);
  }
}
