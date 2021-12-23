package slimeknights.tconstruct.library.tools.nbt;

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.Constants.NBT;
import slimeknights.tconstruct.library.materials.IMaterialRegistry;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Similar to {@link slimeknights.tconstruct.library.tools.nbt.MaterialNBT}, but does not check materials against the registry.
 * Used for rendering so we can have client side only materials for UIs. Anything logic based should use the regular material NBT
 */
@EqualsAndHashCode
@ToString
public class MaterialIdNBT {
  /** Instance containing no materials, for errors with parsing NBT */
  final static MaterialIdNBT EMPTY = new MaterialIdNBT(ImmutableList.of());

  /** List of materials contained in this NBT */
  @Getter
  private final List<MaterialId> materials;

  /** Creates a new material NBT */
  public MaterialIdNBT(List<MaterialId> materials) {
    this.materials = ImmutableList.copyOf(materials);
  }

  /**
   * Gets the material at the given index
   * @param index  Index
   * @return  Material, or unknown if index is invalid
   */
  public MaterialId getMaterial(int index) {
    if (index >= materials.size() || index < 0) {
      return IMaterial.UNKNOWN_ID;
    }
    return materials.get(index);
  }

  /** Resolves all redirects, replacing with material redirects */
  public MaterialIdNBT resolveRedirects() {
    boolean changed = false;
    ImmutableList.Builder<MaterialId> builder = ImmutableList.builder();
    IMaterialRegistry registry = MaterialRegistry.getInstance();
    for (MaterialId id : materials) {
      MaterialId resolved = registry.resolve(id);
      if (resolved != id) {
        changed = true;
      }
      builder.add(resolved);
    }
    // return a new instance only if things changed
    if (changed) {
      return new MaterialIdNBT(builder.build());
    }
    return this;
  }

  /**
   * Parses the material list from NBT
   * @param nbt  NBT instance
   * @return  MaterialNBT instance
   */
  public static MaterialIdNBT readFromNBT(@Nullable INBT nbt) {
    if (nbt == null || nbt.getId() != Constants.NBT.TAG_LIST) {
      return EMPTY;
    }
    ListNBT listNBT = (ListNBT) nbt;
    if (listNBT.getTagType() != Constants.NBT.TAG_STRING) {
      return EMPTY;
    }

    List<MaterialId> materials = listNBT.stream()
      .map(INBT::getString)
      .map(MaterialId::tryCreate)
      .filter(Objects::nonNull)
      .collect(Collectors.toList());
    return new MaterialIdNBT(materials);
  }

  /**
   * Writes this material list to NBT
   * @return  List of materials
   */
  public ListNBT serializeToNBT() {
    return materials.stream()
                    .map(MaterialId::toString)
                    .map(StringNBT::valueOf)
                    .collect(Collectors.toCollection(ListNBT::new));
  }

  /**
   * Parses the material list from a stack
   * @param stack  Tool stack instance
   * @return  MaterialNBT instance
   */
  public static MaterialIdNBT from(ItemStack stack) {
    CompoundNBT nbt = stack.getTag();
    if (nbt != null) {
      return readFromNBT(nbt.getList(ToolStack.TAG_MATERIALS, NBT.TAG_STRING));
    }
    return EMPTY;
  }

  /** Writes this material list to the given stack */
  public ItemStack updateStack(ItemStack stack) {
    stack.getOrCreateTag().put(ToolStack.TAG_MATERIALS, serializeToNBT());
    return stack;
  }

  /** Writes this material list to the given stack */
  public CompoundNBT updateNBT(CompoundNBT nbt) {
    nbt.put(ToolStack.TAG_MATERIALS, serializeToNBT());
    return nbt;
  }
}
