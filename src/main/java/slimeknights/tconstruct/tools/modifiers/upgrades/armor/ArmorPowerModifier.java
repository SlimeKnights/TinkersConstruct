package slimeknights.tconstruct.tools.modifiers.upgrades.armor;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.EquipmentSlotType.Group;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import java.util.UUID;
import java.util.function.BiConsumer;

public class ArmorPowerModifier extends IncrementalModifier {
  private static final UUID[] UUIDS = {
    UUID.fromString("56de3a2c-3226-11ec-8d3d-0242ac130003"),
    UUID.fromString("56de3c52-3226-11ec-8d3d-0242ac130003"),
    UUID.fromString("56de3e28-3226-11ec-8d3d-0242ac130003"),
    UUID.fromString("56de3ef0-3226-11ec-8d3d-0242ac130003")
  };

  public ArmorPowerModifier() {
    super(0xEAE5DE);
  }

  @Override
  public void addAttributes(IModifierToolStack tool, int level, EquipmentSlotType slot, BiConsumer<Attribute,AttributeModifier> consumer) {
    if (slot.getSlotType() == Group.ARMOR) {
      // +5% damage per level
      consumer.accept(Attributes.ATTACK_DAMAGE, new AttributeModifier(UUIDS[slot.getIndex()], "tconstruct.modifier.armor_power." + slot.getName(), 0.05f * getScaledLevel(tool, level), Operation.MULTIPLY_TOTAL));
    }
  }
}
