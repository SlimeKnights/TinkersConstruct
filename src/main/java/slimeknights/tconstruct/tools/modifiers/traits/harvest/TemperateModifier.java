package slimeknights.tconstruct.tools.modifiers.traits.harvest;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.utils.TooltipFlag;
import slimeknights.tconstruct.library.utils.TooltipKey;

import javax.annotation.Nullable;
import java.util.List;

public class TemperateModifier extends Modifier {
  private static final float BASELINE_TEMPERATURE = 0.75f;
  private static final ITextComponent MINING_SPEED = TConstruct.makeTranslation("modifier", "temperate.mining_speed");

  public TemperateModifier() {
    super(0x9C5643);
  }

  /** Gets the bonus from this modifier for the given location */
  private static float getBonus(PlayerEntity player, BlockPos pos, int level) {
    // temperature ranges from 0 to 1.25. Division makes it 0 to 0.125 per level
    return Math.abs(player.world.getBiome(pos).getTemperature(pos) - BASELINE_TEMPERATURE) * level / 10;
  }

  @Override
  public void onBreakSpeed(IModifierToolStack tool, int level, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
    if (isEffective) {
      event.setNewSpeed(event.getNewSpeed() * (1 + getBonus(event.getPlayer(), event.getPos(), level)));
    }
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, @Nullable PlayerEntity player, List<ITextComponent> tooltip, TooltipKey key, TooltipFlag tooltipFlag) {
    float bonus;
    if (player != null && key == TooltipKey.SHIFT) {
      bonus = getBonus(player, player.getPosition(), level);
    } else {
      bonus = level * 0.125f;
    }
    if (bonus >= 0.01f) {
      addPercentTooltip(MINING_SPEED, bonus, tooltip);
    }
  }
}
