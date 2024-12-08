package slimeknights.tconstruct.library.tools.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * All the materials contained within the tool. Determines a portion of the modifiers, along with the stats.
 */
@EqualsAndHashCode
@ToString
public class MaterialNBT implements Iterable<MaterialVariant> {
  /** Instance containing no materials, for errors with parsing NBT */
  public final static MaterialNBT EMPTY = new MaterialNBT(ImmutableList.of());

  /** List of materials contained in this NBT */
  @Getter
  private final List<MaterialVariant> list;

  /** Creates a new material NBT */
  public MaterialNBT(List<MaterialVariant> list) {
    this.list = ImmutableList.copyOf(list);
  }

  /** Creates a new material NBT */
  @VisibleForTesting
  public static MaterialNBT of(IMaterial... materials) {
    return new MaterialNBT(Arrays.stream(materials).map(MaterialVariant::of).toList());
  }

  /** Creates a builder for this NBT */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Gets the material at the given index
   * @param index  Index
   * @return  Material, or unknown if index is invalid
   */
  public MaterialVariant get(int index) {
    if (index >= list.size() || index < 0) {
      return MaterialVariant.UNKNOWN;
    }
    return list.get(index);
  }

  /** Gets the number of materials in this list */
  public int size() {
    return list.size();
  }

  /**
   * Creates a copy of this material list with the material at the given index substituted
   * @param index        Index to replace. Can be greater than the material list size
   * @param replacement  New material for that index
   * @return  Copy of NBt with the new material
   * @throws IndexOutOfBoundsException  If the index is invalid
   */
  public MaterialNBT replaceMaterial(int index, MaterialVariantId replacement) {
    if (index < 0) {
      throw new IndexOutOfBoundsException("Material index is out of bounds");
    }
    // start by copying all materials over
    int size = list.size();
    ArrayList<MaterialVariant> list = new ArrayList<>(Math.max(size, index + 1));
    for (int i = 0; i < size; i++) {
      if (i == index) {
        list.add(MaterialVariant.of(replacement));
      } else {
        list.add(this.list.get(i));
      }
    }

    // if the index is bigger, copy in unknown materials until we reach it
    // handles the case where a tool has broken material NBT without crashing
    if (index >= size) {
      for (int i = size; i < index; i++) {
        list.add(MaterialVariant.of(IMaterial.UNKNOWN, ""));
      }
      list.add(MaterialVariant.of(replacement));
    }

    return new MaterialNBT(list);
  }

  /**
   * Parses the material list from NBT
   * @param nbt  NBT instance
   * @return  MaterialNBT instance
   */
  public static MaterialNBT readFromNBT(@Nullable Tag nbt) {
    if (nbt == null || nbt.getId() != Tag.TAG_LIST) {
      return EMPTY;
    }
    ListTag listNBT = (ListTag) nbt;
    if (listNBT.getElementType() != Tag.TAG_STRING) {
      return EMPTY;
    }

    List<MaterialVariant> materials = listNBT.stream()
                                             .map(tag -> MaterialVariantId.tryParse(tag.getAsString()))
                                             .filter(Objects::nonNull)
                                             .map(MaterialVariant::of)
                                             .collect(Collectors.toList());

    return new MaterialNBT(materials);
  }

  /**
   * Writes this material list to NBT
   * @return  List of materials
   */
  public ListTag serializeToNBT() {
    return list.stream()
               .map(lazy -> StringTag.valueOf(lazy.getVariant().toString()))
               .collect(Collectors.toCollection(ListTag::new));
  }


  /* Iterator */

  @Nonnull
  @Override
  public Iterator<MaterialVariant> iterator() {
    return list.iterator();
  }

  @Override
  public void forEach(Consumer<? super MaterialVariant> action) {
    list.forEach(action);
  }

  @Override
  public Spliterator<MaterialVariant> spliterator() {
    return list.spliterator();
  }

  /** Builder for material NBT */
  public static class Builder {
    private final ImmutableList.Builder<MaterialVariant> builder = ImmutableList.builder();

    /** Adds a material to the builder */
    public Builder add(MaterialVariant variant) {
      builder.add(variant);
      return this;
    }

    /** Adds a material to the builder */
    public Builder add(MaterialVariantId variantId) {
      return add(MaterialVariant.of(variantId));
    }

    /** Adds a material to the builder */
    public Builder add(IMaterial material) {
      return add(MaterialVariant.of(material));
    }

    /** Builds the final list */
    public MaterialNBT build() {
      List<MaterialVariant> materials = builder.build();
      if (materials.isEmpty()) {
        return MaterialNBT.EMPTY;
      }
      return new MaterialNBT(materials);
    }
  }
}
