package slimeknights.tconstruct.world;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public interface WorldExtension {
  void queuePacket(Runnable runnable);
}
