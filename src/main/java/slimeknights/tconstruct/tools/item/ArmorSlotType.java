package slimeknights.tconstruct.tools.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.IStringSerializable;

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
}
