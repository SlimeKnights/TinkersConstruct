package slimeknights.tconstruct.library.tools.nbt;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.With;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.nbt.ModifierNBT;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ModifierListNBT {

  final static ModifierListNBT EMPTY = new ModifierListNBT(new ArrayList<>());

  @With(AccessLevel.PACKAGE)
  private final List<ModifierNBT> modifiers;

  public List<ModifierNBT> getCurrentModifiers() {
    return modifiers;
  }

  public ModifierListNBT addOrReplaceModifier(ModifierNBT modifierNBT) {
    List<ModifierNBT> modifiersList = modifiers;

    if (modifiersList.isEmpty()) {
      modifiersList = new ArrayList<>();
    }

    int index = getModifierIndex(modifierNBT.identifier);
    if (index != -1) {
      modifiersList.set(index, modifierNBT);
    }
    else {
      modifiersList.add(modifierNBT);
    }

    return withModifiers(modifiersList);
  }

  public boolean hasModifier(ModifierId identifier) {
    for (ModifierNBT modifier : modifiers) {
      if (modifier.identifier.toString().equals(identifier.toString())) {
        return true;
      }
    }

    return false;
  }

  public int getModifierIndex(ModifierId identifier) {
    for (ModifierNBT modifier : modifiers) {
      if (modifier.identifier.toString().equals(identifier.toString())) {
        return modifiers.indexOf(modifier);
      }
    }

    return -1;
  }

  public ModifierNBT getOrCreateModifier(ModifierId identifier) {
    return modifiers.stream().filter(modifierNBT -> modifierNBT.identifier == identifier).findFirst().orElse(new ModifierNBT(identifier, 0));
  }

  public static ModifierListNBT readFromNBT(@Nullable INBT nbt) {
    if (nbt == null || nbt.getId() != Constants.NBT.TAG_LIST) {
      return EMPTY;
    }

    ListNBT listNBT = (ListNBT) nbt;

    if (listNBT.getTagType() != Constants.NBT.TAG_COMPOUND) {
      return EMPTY;
    }

    List<ModifierNBT> modifiers = listNBT.stream()
      .map(ModifierNBT::readFromNBT)
      .collect(Collectors.toList());

    return new ModifierListNBT(modifiers);
  }

  public ListNBT serializeToNBT() {
    return this.modifiers.stream()
      .map(ModifierNBT::serializeToNBT)
      .collect(Collectors.toCollection(ListNBT::new));
  }
}
