package slimeknights.tconstruct.library.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants.NBT;

import javax.annotation.Nullable;

/** Helpers related to NBT */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TagUtil {
  /* Helper functions */

  /**
   * Writes a block position to NBT
   * @param pos  Position to write
   * @return  Position in NBT
   * @deprecated  Use {@link net.minecraft.nbt.NBTUtil#writeBlockPos(BlockPos)}, deprecated due to case difference
   */
  @Deprecated
  public static CompoundNBT writePos(BlockPos pos) {
    CompoundNBT tag = new CompoundNBT();
    tag.putInt("x", pos.getX());
    tag.putInt("y", pos.getY());
    tag.putInt("z", pos.getZ());
    return tag;
  }

  /**
   * Reads a block position from NBT
   * @param tag  Tag
   * @return  Block position, or null if invalid
   * @deprecated  Use {@link net.minecraft.nbt.NBTUtil#writeBlockPos(BlockPos)}, deprecated due to case difference
   */
  @Deprecated
  @Nullable
  public static BlockPos readPos(CompoundNBT tag) {
    if (tag.contains("x", NBT.TAG_ANY_NUMERIC) && tag.contains("y", NBT.TAG_ANY_NUMERIC) && tag.contains("z", NBT.TAG_ANY_NUMERIC)) {
      return new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
    }
    return null;
  }

  /**
   * Reads a block position from NBT
   * @param parent  Parent tag
   * @param key     Position key
   * @return  Block position, or null if invalid or missing
   */
  @Nullable
  public static BlockPos readPos(CompoundNBT parent, String key) {
    if (parent.contains(key, NBT.TAG_COMPOUND)) {
      return readPos(parent.getCompound(key));
    }
    return null;
  }
}
