package slimeknights.tconstruct.library.tools.nbt;

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraftforge.common.util.Constants;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * All the stats that every tool must have.
 * Some may not be used explicitly by all tools (e.g. weapons and harvest  level)
 */
@EqualsAndHashCode
@ToString
public class MaterialNBT {

  final static MaterialNBT EMPTY = new MaterialNBT(ImmutableList.of());

  private final List<IMaterial> materials;

  public MaterialNBT(List<IMaterial> materials) {
    this.materials = ImmutableList.copyOf(materials);
  }

  public List<IMaterial> getMaterials() {
    return materials;
  }

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

  public ListNBT serializeToNBT() {
    return materials.stream()
      .map(IMaterial::getIdentifier)
      .map(MaterialId::toString)
      .map(StringNBT::valueOf)
      .collect(Collectors.toCollection(ListNBT::new));
  }
}
