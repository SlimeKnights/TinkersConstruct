package slimeknights.tconstruct.tools.modifiers.traits;

import net.minecraft.util.Direction;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class DwarvenModifier extends Modifier {
  /** Baseline height where boost is 1 */
  private static final int SEA_LEVEL = 64;
  /** Max percentage bonus per level when y = 0 */
  private static final float BOOST_AT_0 = 0.1f;

  public DwarvenModifier() {
    super(0xF98648);
  }

  @Override
  public void onBreakSpeed(IModifierToolStack tool, int level, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
    if (!isEffective) {
      return;
    }
    // essentially just the line slope formula from (0, level + 1) to (SEA_LEVEL, 1), with a scal
    float factor = Math.max(1f, (SEA_LEVEL - event.getPos().getY()) * level * (BOOST_AT_0 / SEA_LEVEL) + 1);
    if (factor > 1f) {
      event.setNewSpeed(event.getNewSpeed() * factor);
    }
  }
}
