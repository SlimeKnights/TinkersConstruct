package slimeknights.tconstruct.library.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import javax.annotation.Nullable;

/** Helpers related to Tag */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TagUtil {
  /* Helper functions */

  /**
   * Writes a block position to Tag
   * @param pos  Position to write
   * @return  Position in Tag
   * @deprecated  Use {@link net.minecraft.nbt.NbtUtils#writeBlockPos(BlockPos)}, deprecated due to case difference
   */
  @Deprecated
  public static CompoundTag writePos(BlockPos pos) {
    CompoundTag tag = new CompoundTag();
    tag.putInt("x", pos.getX());
    tag.putInt("y", pos.getY());
    tag.putInt("z", pos.getZ());
    return tag;
  }

  /**
   * Reads a block position from Tag
   * @param tag  Tag
   * @return  Block position, or null if invalid
   * @deprecated  Use {@link net.minecraft.nbt.NbtUtils#readBlockPos(CompoundTag)}, deprecated due to case difference
   */
  @Deprecated
  @Nullable
  public static BlockPos readPos(CompoundTag tag) {
    if (tag.contains("x", Tag.TAG_ANY_NUMERIC) && tag.contains("y", Tag.TAG_ANY_NUMERIC) && tag.contains("z", Tag.TAG_ANY_NUMERIC)) {
      return new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
    }
    return null;
  }

  /**
   * Reads a block position from Tag
   * @param parent  Parent tag
   * @param key     Position key
   * @return  Block position, or null if invalid or missing
   */
  @Nullable
  public static BlockPos readPos(CompoundTag parent, String key) {
    if (parent.contains(key, Tag.TAG_COMPOUND)) {
      return readPos(parent.getCompound(key));
    }
    return null;
  }

  /**
   * Checks if the given tag is a numeric type
   * @param tag  Tag to check
   * @return  True if the type matches
   */
  public static boolean isNumeric(Tag tag) {
    byte type = tag.getId();
    return type == Tag.TAG_BYTE || type == Tag.TAG_SHORT || type == Tag.TAG_INT || type == Tag.TAG_LONG || type == Tag.TAG_FLOAT || type == Tag.TAG_DOUBLE;
  }
}
