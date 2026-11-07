package slimeknights.tconstruct.smeltery.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

/** {@link ChannelBlockEntity} with a different BlockEntityType so that it can use an alternative BlockEntityRenderer that does not try render contained fluid */
public class OpaqueChannelBlockEntity extends ChannelBlockEntity {

  public OpaqueChannelBlockEntity(BlockPos pos, BlockState state) {
    super(TinkerSmeltery.opaqueChannel.get(), pos, state);
  }
}
