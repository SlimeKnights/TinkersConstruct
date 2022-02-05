package slimeknights.tconstruct.tools.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EquipmentSlot;

import javax.annotation.Nullable;
import java.util.Locale;

/** Enum to aid in armor registraton */
@RequiredArgsConstructor
@Getter
public enum ArmorSlotType implements StringRepresentable {
  BOOTS(EquipmentSlot.FEET),
  LEGGINGS(EquipmentSlot.LEGS),
  CHESTPLATE(EquipmentSlot.CHEST),
  HELMET(EquipmentSlot.HEAD);

  private final EquipmentSlot equipmentSlot;
  private final String serializedName = toString().toLowerCase(Locale.ROOT);
  private final int index = ordinal();

  /** Gets an equipment slot for the given armor slot */
  @Nullable
  public static ArmorSlotType fromEquipment(EquipmentSlot slotType) {
    return switch (slotType) {
      case FEET -> BOOTS;
      case LEGS -> LEGGINGS;
      case CHEST -> CHESTPLATE;
      case HEAD -> HELMET;
      default -> null;
    };
  }
}
