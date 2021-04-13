package slimeknights.tconstruct.tools.modifiers.traits;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class TemperateModifier extends Modifier {
  private static final float BASELINE_TEMPERATURE = 0.75f;

  public TemperateModifier() {
    super(0xff9e7f);
  }

  @Override
  public void onBreakSpeed(IModifierToolStack tool, int level, PlayerEntity player) {
    BlockPos pos = player.getBlockPos();
    // temperature ranges from 0 to 1.25. Division makes it 0 to 0.25 per level
    float boost = Math.abs(player.world.getBiome(pos).getTemperature(pos) - BASELINE_TEMPERATURE) * level / 5;
    player.forwardSpeed = player.forwardSpeed * (1 + boost);
  }
}
