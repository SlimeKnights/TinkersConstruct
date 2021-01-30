package slimeknights.tconstruct.library.tools.nbt;

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraftforge.common.util.Constants;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * All the materials contained within the tool. Determines a portion of the modifiers, along with the stats.
 */
@EqualsAndHashCode
@ToString
public class MaterialNBT {
  /** Instance containing no materials, for errors with parsing NBT */
  final static MaterialNBT EMPTY = new MaterialNBT(ImmutableList.of());

  /** List of materials contained in this NBT */
  @Getter
  private final List<IMaterial> materials;

  /** Creates a new material NBT */
  public MaterialNBT(List<IMaterial> materials) {
    this.materials = ImmutableList.copyOf(materials);
  }

  /**
   * Gets the material at the given index
   * @param index  Index
   * @return  Material, or unknown if index is invalid
   */
  public IMaterial getMaterial(int index) {
    if (index >= materials.size() || index < 0) {
      return IMaterial.UNKNOWN;
    }
    return materials.get(index);
  }

  /**
   * Creates a copy of this material list with the material at the given index substituted
   * @param index        Index to replace. Can be greater than the material list size
   * @param replacement  New material for that index
   * @return  Copy of NBt with the new material
   * @throws IndexOutOfBoundsException  If the index is invalid
   */
  public MaterialNBT replaceMaterial(int index, IMaterial replacement) {
    if (index < 0) {
      throw new IndexOutOfBoundsException("Material index is out of bounds");
    }
    // start by copying all materials over
    int size = materials.size();
    ArrayList<IMaterial> list = new ArrayList<>(Math.max(size, index + 1));
    for (int i = 0; i < size; i++) {
      if (i == index) {
        list.add(replacement);
      } else {
        list.add(materials.get(i));
      }
    }

    // if the index is bigger, copy in unknown materials until we reach it
    // handles the case where a tool has broken material NBT without crashing
    if (index >= size) {
      for (int i = size; i < index; i++) {
        list.add(IMaterial.UNKNOWN);
      }
      list.add(replacement);
    }

    return new MaterialNBT(list);
  }

  /**
   * Parses the material list from NBT
   * @param nbt  NBT instance
   * @return  MaterialNBT instance
   */
  public static MaterialNBT readFromNBT(@Nullable INBT nbt) {
    if (nbt == null || nbt.getId() != Constants.NBT.TAG_LIST) {
      return EMPTY;
    }
    ListNBT listNBT = (ListNBT) nbt;
    if (listNBT.getTagType() != Constants.NBT.TAG_STRING) {
      return EMPTY;
    }

    List<IMaterial> materials = listNBT.stream()
      .map(INBT::getString)
      .map(MaterialId::new)
      .map(MaterialRegistry::getMaterial)
      .collect(Collectors.toList());

    return new MaterialNBT(materials);
  }

  /**
   * Writes this material list to NBT
   * @return  List of materials
   */
  public ListNBT serializeToNBT() {
    return materials.stream()
      .map(IMaterial::getIdentifier)
      .map(MaterialId::toString)
      .map(StringNBT::valueOf)
      .collect(Collectors.toCollection(ListNBT::new));
  }
}
