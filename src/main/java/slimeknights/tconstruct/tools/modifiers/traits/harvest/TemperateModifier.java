package slimeknights.tconstruct.tools.modifiers.traits.harvest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class TemperateModifier extends Modifier {
  private static final float BASELINE_TEMPERATURE = 0.75f;

  @Override
  public void onBreakSpeed(IToolStackView tool, int level, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
    if (isEffective) {
      BlockPos pos = event.getPos();
      // temperature ranges from 0 to 1.25. Division makes it 0 to 0.125 per level
      float boost = Math.abs(event.getPlayer().level.getBiome(pos).getTemperature(pos) - BASELINE_TEMPERATURE) * level / 10;
      event.setNewSpeed(event.getNewSpeed() * (1 + boost));
    }
  }
}
