package slimeknights.tconstruct.smeltery.tileentity.multiblock;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

/**
 * Result of attempting to form a multiblock, for error message and the position causing the error
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MultiblockResult {
  /** Successful result, used when no error */
  public static final MultiblockResult SUCCESS = new MultiblockResult(true, null, StringTextComponent.EMPTY);

  /** If true, this result was successful */
  private final boolean success;
  /** Position to highlight for errors */
  @Nullable
  private final BlockPos pos;
  /** Message to display for errors */
  private final ITextComponent message;

  /**
   * Creates an error for the given arguments
   * @param pos  Position that caused the issue, may be null if no position
   * @return  Multiblock result
   */
  public static MultiblockResult error(@Nullable BlockPos pos, ITextComponent error) {
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
    return error(pos, new TranslationTextComponent(key, params));
  }
}
