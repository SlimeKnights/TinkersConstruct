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

public class SpeedyModifier extends IncrementalModifier {
  private static final UUID[] uuids = {
    UUID.fromString("cdcece66-fcd0-11eb-9a03-0242ac130003"),
    UUID.fromString("1601fd0c-3228-11ec-8d3d-0242ac130003"),
    UUID.fromString("1601ff32-3228-11ec-8d3d-0242ac130003"),
    UUID.fromString("16020022-3228-11ec-8d3d-0242ac130003")
  };

  @Override
  public void addAttributes(IToolStackView tool, int level, EquipmentSlot slot, BiConsumer<Attribute,AttributeModifier> consumer) {
    if (slot.getType() == Type.ARMOR) {
      consumer.accept(Attributes.MOVEMENT_SPEED, new AttributeModifier(uuids[slot.getIndex()], "tconstruct.modifier.speedy." + slot.getName(), 0.1 * getScaledLevel(tool, level), Operation.MULTIPLY_TOTAL));
    }
  }
}
