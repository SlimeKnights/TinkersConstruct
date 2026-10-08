package slimeknights.tconstruct.library.tools.nbt;

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.materials.IMaterialRegistry;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Similar to {@link slimeknights.tconstruct.library.tools.nbt.MaterialNBT}, but does not check materials against the registry.
 * Used for rendering so we can have client side only materials for UIs. Anything logic based should use the regular material NBT
 */
@EqualsAndHashCode
@ToString
public class MaterialIdNBT {
  /** Instance containing no materials, for errors with parsing NBT */
  public final static MaterialIdNBT EMPTY = new MaterialIdNBT(ImmutableList.of());

  /** List of materials contained in this NBT */
  @Getter
  private final List<MaterialVariantId> materials;

  /** Creates a new material NBT */
  public MaterialIdNBT(List<? extends MaterialVariantId> materials) {
    this.materials = List.copyOf(materials);
  }

  /** Gets the number of materials on this stack. Note this may not match the number of materials the tool desires. */
  public int size() {
    return materials.size();
  }

  /**
   * Gets the material at the given index
   * @param index  Index
   * @return  Material, or unknown if index is invalid
   * @see #getMaterial(ItemStack, int)
   */
  public MaterialVariantId getMaterial(int index) {
    if (index >= materials.size() || index < 0) {
      return MaterialId.UNKNOWN;
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
  public MaterialIdNBT replaceMaterial(int index, MaterialVariantId replacement) {
    if (index < 0) {
      throw new IndexOutOfBoundsException("Material index is out of bounds");
    }
    // start by copying all materials over
    int size = materials.size();
    ArrayList<MaterialVariantId> list = new ArrayList<>(Math.max(size, index + 1));
    for (int i = 0; i < size; i++) {
      if (i == index) {
        list.add(replacement);
      } else {
        list.add(this.materials.get(i));
      }
    }

    // if the index is bigger, copy in unknown materials until we reach it
    // handles the case where a tool has broken material NBT without crashing
    if (index >= size) {
      for (int i = size; i < index; i++) {
        list.add(MaterialId.UNKNOWN);
      }
      list.add(replacement);
    }

    return new MaterialIdNBT(list);
  }

  /** Resolves all redirects, replacing with material redirects */
  public MaterialIdNBT resolveRedirects() {
    boolean changed = false;
    ImmutableList.Builder<MaterialVariantId> builder = ImmutableList.builder();
    IMaterialRegistry registry = MaterialRegistry.getInstance();
    for (MaterialVariantId id : materials) {
      MaterialId original = id.getId();
      MaterialId resolved = registry.resolve(original);
      if (resolved != original) {
        changed = true;
      }
      builder.add(MaterialVariantId.create(resolved, id.getVariant()));
    }
    // return a new instance only if things changed
    if (changed) {
      return new MaterialIdNBT(builder.build());
    }
    return this;
  }

  /** Tries to parse the tag as a material variant ID, returning unknown if invalid. */
  private static MaterialVariantId tryParse(String tag) {
    MaterialVariantId material = MaterialVariantId.tryParse(tag);
    if (material != null) {
      return material;
    }
    return MaterialId.UNKNOWN;
  }

  /**
   * Parses the material list from NBT
   * @param nbt  NBT instance
   * @return  MaterialNBT instance
   */
  public static MaterialIdNBT readFromNBT(@Nullable Tag nbt) {
    if (nbt == null || nbt.getId() != Tag.TAG_LIST) {
      return EMPTY;
    }
    ListTag list = (ListTag) nbt;
    if (list.getElementType() != Tag.TAG_STRING) {
      return EMPTY;
    }
    List<MaterialVariantId> materials = new ArrayList<>(list.size());
    for (int i = 0; i < list.size(); i++) {
      materials.add(tryParse(list.getString(i)));
    }
    return new MaterialIdNBT(materials);
  }

  /**
   * Writes this material list to NBT
   * @return  List of materials
   */
  public ListTag serializeToNBT() {
    ListTag list = new ListTag();
    for (MaterialVariantId material : materials) {
      list.add(StringTag.valueOf(material.toString()));
    }
    return list;
  }

  /**
   * Parses the material list from a stack
   * @param stack  Tool stack instance
   * @return  MaterialNBT instance
   */
  public static MaterialIdNBT from(ItemStack stack) {
    CompoundTag nbt = stack.getTag();
    if (nbt != null) {
      return readFromNBT(nbt.getList(ToolStack.TAG_MATERIALS, Tag.TAG_STRING));
    }
    return EMPTY;
  }

  /** Helper to quickly fetch a single material ID from a stack. Use {@link #from(ItemStack)} and {@link #getMaterial(int)} instead if you need to parse multiple. */
  public static MaterialVariantId getMaterial(ItemStack stack, int index) {
    CompoundTag nbt = stack.getTag();
    if (nbt != null) {
      ListTag list = nbt.getList(ToolStack.TAG_MATERIALS, Tag.TAG_STRING);
      if (index < list.size()) {
        return tryParse(list.getString(index));
      }
    }
    return MaterialId.UNKNOWN;
  }

  /** Writes this material list to the given stack */
  public ItemStack updateStack(ItemStack stack) {
    stack.getOrCreateTag().put(ToolStack.TAG_MATERIALS, serializeToNBT());
    return stack;
  }

  /** Writes this material list to the given stack */
  @SuppressWarnings("UnusedReturnValue")
  public CompoundTag updateNBT(CompoundTag nbt) {
    nbt.put(ToolStack.TAG_MATERIALS, serializeToNBT());
    return nbt;
  }
}
