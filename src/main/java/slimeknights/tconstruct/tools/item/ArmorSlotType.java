package slimeknights.tconstruct.tools.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.IStringSerializable;

import javax.annotation.Nullable;
import java.util.Locale;

/** Enum to aid in armor registraton */
@RequiredArgsConstructor
@Getter
public enum ArmorSlotType implements IStringSerializable {
  BOOTS(EquipmentSlotType.FEET),
  LEGGINGS(EquipmentSlotType.LEGS),
  CHESTPLATE(EquipmentSlotType.CHEST),
  HELMET(EquipmentSlotType.HEAD);

  private final EquipmentSlotType equipmentSlot;
  private final String string = toString().toLowerCase(Locale.ROOT);
  private final int index = ordinal();

  /** Gets an equipment slot for the given armor slot */
  @Nullable
  public static ArmorSlotType fromEquipment(EquipmentSlotType slotType) {
    switch (slotType) {
      case FEET: return BOOTS;
      case LEGS: return LEGGINGS;
      case CHEST: return CHESTPLATE;
      case HEAD: return HELMET;
    }
    return null;
  }
}
