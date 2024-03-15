package slimeknights.tconstruct.smeltery.block.entity.multiblock;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

/**
 * Result of attempting to form a multiblock, for error message and the position causing the error
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MultiblockResult {
  /** Successful result, used when no error */
  public static final MultiblockResult SUCCESS = new MultiblockResult(true, null, Component.empty());

  /** If true, this result was successful */
  private final boolean success;
  /** Position to highlight for errors */
  @Nullable
  private final BlockPos pos;
  /** Message to display for errors */
  private final Component message;

  /**
   * Creates an error for the given arguments
   * @param pos  Position that caused the issue, may be null if no position
   * @return  Multiblock result
   */
  public static MultiblockResult error(@Nullable BlockPos pos, Component error) {
    return new MultiblockResult(false, pos, error);
  }

  /**
   * Creates an error for the given arguments
   * @param pos     Position that caused the issue, may be null if no position
   * @param key     Translation key
   * @param params  Error parameters
   * @return  Multiblock result
   */
  public static MultiblockResult error(@Nullable BlockPos pos, String key, Object... params) {
    return error(pos, Component.translatable(key, params));
  }
}
