package slimeknights.tconstruct.tools.modifiers.upgrades.harvest;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.TooltipFlag;
import slimeknights.tconstruct.library.utils.TooltipKey;

import javax.annotation.Nullable;
import java.util.List;

public class HydraulicModifier extends IncrementalModifier {
  public HydraulicModifier() {
    super(0x7CB3A4);
  }

  @Override
  public int getPriority() {
    return 125; // run before trait boosts such as dwarven
  }

  /** Gets the bonus for the living entity */
  private static float getBonus(LivingEntity living) {
    float bonus = 0;
    // highest bonus in water
    if (living.areEyesInFluid(FluidTags.WATER)) {
      bonus = 8;
    } else if (living.getEntityWorld().isRainingAt(living.getPosition())) {
      // partial bonus in the rain
      bonus = 4;
    }
    return bonus;
  }

  @Override
  public void onBreakSpeed(IModifierToolStack tool, int level, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
    if (!isEffective) {
      return;
    }
    PlayerEntity player = event.getPlayer();
    float bonus = getBonus(player);
    if (bonus > 0) {
      // if not enchanted with aqua affinity, multiply by 5 to cancel out the effects of water
      if (!ModifierUtil.hasAquaAffinity(player) && player.areEyesInFluid(FluidTags.WATER)) {
        bonus *= 5;
      }
      bonus *= getScaledLevel(tool, level) * tool.getModifier(ToolStats.MINING_SPEED) * miningSpeedModifier;
      event.setNewSpeed(event.getNewSpeed() + bonus);
    }
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, @Nullable PlayerEntity player, List<ITextComponent> tooltip, TooltipKey key, TooltipFlag flag) {
    float bonus = 8;
    if (player != null && key == TooltipKey.SHIFT) {
      bonus = getBonus(player);
    }
    addStatTooltip(tool, ToolStats.MINING_SPEED, TinkerTags.Items.HARVEST, bonus * getScaledLevel(tool, level), tooltip);
  }
}
