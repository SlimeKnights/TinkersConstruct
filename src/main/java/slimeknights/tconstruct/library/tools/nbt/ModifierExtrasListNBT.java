package slimeknights.tconstruct.library.tools.nbt;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.With;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import slimeknights.tconstruct.library.modifiers.ModifierId;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ModifierExtrasListNBT {

  public static final String TAG_IDENTIFIER = "identifier";

  final static ModifierExtrasListNBT EMPTY = new ModifierExtrasListNBT(new ArrayList<>());

  @With(AccessLevel.PACKAGE)
  private final List<CompoundNBT> extraData;

  public List<CompoundNBT> getCurrentExtraData() {
    return extraData;
  }

  public ModifierExtrasListNBT addOrReplaceExtraData(CompoundNBT extraDataNBT) {
    List<CompoundNBT> extraDataList = extraData;

    if (extraDataList.isEmpty()) {
      extraDataList = new ArrayList<>();
    }

    int index = getExtraDataIndex(new ModifierId(extraDataNBT.getString(TAG_IDENTIFIER)));

    if (index != -1) {
      extraDataList.set(index, extraDataNBT);
    }
    else {
      extraDataList.add(extraDataNBT);
    }

    return withExtraData(extraDataList);
  }

  public boolean hasExtraData(ModifierId identifier) {
    for (CompoundNBT extra : extraData) {
      if (extra.getString(TAG_IDENTIFIER).equals(identifier.toString())) {
        return true;
      }
    }

    return false;
  }

  public int getExtraDataIndex(ModifierId identifier) {
    for (CompoundNBT extra : extraData) {
      if (extra.getString(TAG_IDENTIFIER).equals(identifier.toString())) {
        return extraData.indexOf(extra);
      }
    }

    return -1;
  }

  public CompoundNBT getOrCreateExtraData(ModifierId identifier) {
    return extraData.stream().filter(compoundNBT -> compoundNBT.getString(TAG_IDENTIFIER).equals(identifier.toString())).findFirst().orElseGet(() -> createExtraDataCompound(identifier));
  }

  public CompoundNBT createExtraDataCompound(ModifierId modifierId) {
    CompoundNBT extraData = new CompoundNBT();
    extraData.putString(TAG_IDENTIFIER, modifierId.toString());
    return extraData;
  }

  public static ModifierExtrasListNBT readFromNBT(@Nullable INBT nbt) {
    if (nbt == null || nbt.getId() != Constants.NBT.TAG_LIST) {
      return EMPTY;
    }

    ListNBT listNBT = (ListNBT) nbt;

    if (listNBT.getTagType() != Constants.NBT.TAG_COMPOUND) {
      return EMPTY;
    }

    List<CompoundNBT> extraData = new ArrayList<>();

    for (int i = 0; i < listNBT.size(); ++i) {
      CompoundNBT compoundnbt = listNBT.getCompound(i);

      extraData.add(compoundnbt);
    }

    return new ModifierExtrasListNBT(extraData);
  }

  public ListNBT serializeToNBT() {
    return this.extraData.stream()
      .collect(Collectors.toCollection(ListNBT::new));
  }
}
