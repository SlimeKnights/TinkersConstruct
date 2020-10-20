package slimeknights.tconstruct.library.modifiers.nbt;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.With;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraftforge.common.util.Constants;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierId;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ModifierNBT {

  public final static ModifierNBT EMPTY = new ModifierNBT(new ModifierId(TConstruct.modID, "none"), 1);

  public static final String TAG_IDENTIFIER = "identifier";
  public static final String TAG_LEVEL = "level";

  public final ModifierId identifier;
  @With
  public final int level;

  public static ModifierNBT readFromNBT(@Nullable INBT inbt) {
    if (inbt == null || inbt.getId() != Constants.NBT.TAG_COMPOUND) {
      return EMPTY;
    }

    CompoundNBT nbt = (CompoundNBT) inbt;

    ModifierId identifier = new ModifierId(getStringFromTagOrDefault(nbt, TAG_IDENTIFIER, EMPTY.identifier.toString()));
    int level = getIntFromTagOrDefault(nbt, TAG_LEVEL, EMPTY.level);

    return new ModifierNBT(identifier, level);
  }

  private static String getStringFromTagOrDefault(CompoundNBT nbt, String key, String defaultValue) {
    if (nbt.contains(key, Constants.NBT.TAG_STRING)) {
      return nbt.getString(key);
    }

    return defaultValue;
  }

  private static int getIntFromTagOrDefault(CompoundNBT nbt, String key, int defaultValue) {
    return getFromTagOrDefault(nbt, key, defaultValue, CompoundNBT::getInt);
  }

  private static <T> T getFromTagOrDefault(CompoundNBT nbt, String key, T defaultValue, BiFunction<CompoundNBT, String, T> valueGetter) {
    if (nbt.contains(key, Constants.NBT.TAG_ANY_NUMERIC)) {
      return valueGetter.apply(nbt, key);
    }
    return defaultValue;
  }

  public CompoundNBT serializeToNBT() {
    CompoundNBT nbt = new CompoundNBT();

    nbt.putString(TAG_IDENTIFIER, this.identifier.toString());
    nbt.putInt(TAG_LEVEL, this.level);

    return nbt;
  }
}
