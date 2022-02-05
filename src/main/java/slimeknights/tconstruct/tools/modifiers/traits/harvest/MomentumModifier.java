package slimeknights.tconstruct.tools.modifiers.traits.harvest;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.TooltipKey;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.List;

public class MomentumModifier extends Modifier {
  private static final Component MINING_SPEED = TConstruct.makeTranslation("modifier", "momentum.mining_speed");

  @Override
  public int getPriority() {
    // run this last as we boost original speed, adds to existing boosts
    return 75;
  }

  /** Gets the bonus for the modifier */
  private static float getBonus(LivingEntity living, int level) {
    // 25% boost per level at max
    int effectLevel = TinkerModifiers.momentumEffect.get().getLevel(living) + 1;
    return level * effectLevel / 128f;
  }

  @Override
  public void onBreakSpeed(IToolStackView tool, int level, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
    if (isEffective) {
      event.setNewSpeed(event.getNewSpeed() * (1 + getBonus(event.getEntityLiving(), level)));
    }
  }

  @Override
  public void afterBlockBreak(IToolStackView tool, int level, ToolHarvestContext context) {
    if (context.canHarvest() && context.isEffective() && !context.isAOE()) {
      // 16 blocks gets you to max, levels faster at higher levels
      LivingEntity living = context.getLiving();
      int effectLevel = Math.min(31, TinkerModifiers.momentumEffect.get().getLevel(living) + 1);
      // funny formula from 1.12, guess it makes faster tools have a slightly shorter effect
      int duration = (int) ((10f / tool.getStats().get(ToolStats.MINING_SPEED)) * 1.5f * 20f);
      TinkerModifiers.momentumEffect.get().apply(living, duration, effectLevel, true);
    }
  }

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey key, TooltipFlag flag) {
    if (tool.hasTag(TinkerTags.Items.HARVEST)) {
      float bonus;
      if (player != null && key == TooltipKey.SHIFT) {
        bonus = getBonus(player, level);
      } else {
        bonus = level * 0.25f;
      }
      addPercentTooltip(MINING_SPEED, bonus, tooltip);
    }
  }
}
