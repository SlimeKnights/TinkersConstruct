package slimeknights.tconstruct.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;
import slimeknights.mantle.client.SafeClientAccess;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.mantle.util.BlockEntityHelper;
import slimeknights.tconstruct.TConstruct;

import javax.annotation.Nullable;

/**
 * Helper to create a packet that updates a block entity on the client.
 * @param <T> class type receiving updates.
 */
public interface BlockEntityPacket<T> extends IThreadsafePacket {
  /** Gets the block position for this packet */
  BlockPos pos();

  /** Gets the class for the filter */
  Class<T> type();

  @Override
  default void handleThreadsafe(Context context) {
    BlockPos pos = pos();
    BlockEntity be = getBlockEntity(pos, this);
    if (be != null) {
      Class<T> type = type();
      if (type.isInstance(be)) {
        handleBlockEntity(context, type.cast(be));
      } else {
        TConstruct.LOG.error("Failed to handle packet {}: Block entity type mismatch at {}, expected {}, found {}", this, pos, type, be.getClass());
      }
    } else {
      TConstruct.LOG.error("Failed to handle packet {}: No block entity at {}", this, pos);
    }
  }

  /** Handles the block entity, assuming it's not null and the correct type */
  void handleBlockEntity(Context context, T be);


  /* Helpers */

  /**
   * Gets a block entity in a packet, ensuring the world is loaded before attempting.
   * @param world   World instance.
   * @param pos     Position
   * @param packet  Object to print for debug
   * @return Block entity instance. Null if: world null, position not loaded, or block entity does not exist.
   */
  @Nullable
  static BlockEntity getBlockEntity(@Nullable BlockGetter world, BlockPos pos, Object packet) {
    if (BlockEntityHelper.isBlockLoaded(world, pos)) {
      return world.getBlockEntity(pos);
    }
    TConstruct.LOG.error("Failed to handle packet {}: World is not loaded at {}", packet, pos);
    return null;
  }

  /**
   * Gets a block entity in a packet client side, ensuring the world is loaded before attempting. Only works on the client side.
   * @param pos     Position
   * @param packet  Object to print for debug
   * @return Block entity instance. Null if: not client, position not loaded, or block entity does not exist.
   */
  @Nullable
  static BlockEntity getBlockEntity(BlockPos pos, Object packet) {
    return getBlockEntity(SafeClientAccess.getLevel(), pos, packet);
  }
}
