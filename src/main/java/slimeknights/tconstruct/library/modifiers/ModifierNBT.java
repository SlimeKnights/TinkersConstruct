package slimeknights.tconstruct.library.modifiers;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import slimeknights.tconstruct.TConstruct;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ModifierNBT {

  public final static ModifierNBT EMPTY = new ModifierNBT(new ResourceLocation(TConstruct.modID, "none"), 0xffffff, 1, "");

  public static final String TAG_IDENTIFIER = "identifier";
  public static final String TAG_COLOR = "color";
  public static final String TAG_LEVEL = "level";
  public static final String TAG_EXTRA_INFORMATION = "extra_information";

  public ResourceLocation identifier;
  public int color;
  public int level;
  public String extraInformation;

  public ModifierNBT() {
    this.identifier = new ResourceLocation(TConstruct.modID, "none");
    this.color = 0xffffff;
    this.level = 0;
    this.extraInformation = "";
  }

  public static ModifierNBT readFromNBT(@Nullable INBT inbt) {
    ModifierNBT data = new ModifierNBT();

    if (inbt != null && inbt.getId() == Constants.NBT.TAG_COMPOUND) {
      data.readNBT(inbt);
    }

    return data;
  }

  public ModifierNBT readNBT(@Nullable INBT inbt) {
    if (inbt == null || inbt.getId() != Constants.NBT.TAG_COMPOUND) {
      return EMPTY;
    }

    CompoundNBT nbt = (CompoundNBT) inbt;

    ResourceLocation identifier = new ResourceLocation(getStringFromTagOrDefault(nbt, TAG_IDENTIFIER, EMPTY.identifier.toString()));
    int color = getIntFromTagOrDefault(nbt, TAG_COLOR, EMPTY.color);
    int level = getIntFromTagOrDefault(nbt, TAG_LEVEL, EMPTY.level);
    String extraInformation = getStringFromTagOrDefault(nbt, TAG_EXTRA_INFORMATION, EMPTY.extraInformation);

    return new ModifierNBT(identifier, color, level, extraInformation);
  }

  @Nullable
  public static <T extends ModifierNBT> T readFromNBT(@Nullable INBT tag, Class<T> clazz) {
    try {
      T data = clazz.newInstance();
      data.readNBT(tag);
      return data;
    }
    catch (ReflectiveOperationException e) {
      TConstruct.log.error(e);
      return null;
    }
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
    nbt.putInt(TAG_COLOR, this.color);
    nbt.putInt(TAG_LEVEL, this.level);
    if (!this.extraInformation.isEmpty()) { nbt.putString(TAG_EXTRA_INFORMATION, this.extraInformation); }

    return nbt;
  }
}
