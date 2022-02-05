package slimeknights.tconstruct.tools.modifiers.upgrades.armor;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import slimeknights.tconstruct.library.modifiers.impl.IncrementalModifier;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.UUID;
import java.util.function.BiConsumer;

public class ArmorPowerModifier extends IncrementalModifier {
  private static final UUID[] UUIDS = {
    UUID.fromString("56de3a2c-3226-11ec-8d3d-0242ac130003"),
    UUID.fromString("56de3c52-3226-11ec-8d3d-0242ac130003"),
    UUID.fromString("56de3e28-3226-11ec-8d3d-0242ac130003"),
    UUID.fromString("56de3ef0-3226-11ec-8d3d-0242ac130003")
  };

  @Override
  public void addAttributes(IToolStackView tool, int level, EquipmentSlot slot, BiConsumer<Attribute,AttributeModifier> consumer) {
    if (slot.getType() == Type.ARMOR) {
      // +5% damage per level
      consumer.accept(Attributes.ATTACK_DAMAGE, new AttributeModifier(UUIDS[slot.getIndex()], "tconstruct.modifier.armor_power." + slot.getName(), 0.05f * getScaledLevel(tool, level), Operation.MULTIPLY_TOTAL));
    }
  }
}
