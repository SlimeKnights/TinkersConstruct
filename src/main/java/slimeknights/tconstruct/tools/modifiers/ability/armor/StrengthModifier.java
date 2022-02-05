package slimeknights.tconstruct.tools.modifiers.ability.armor;

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

public class StrengthModifier extends IncrementalModifier {
  private static final UUID[] UUIDS = {
    UUID.fromString("00732ace-5ac3-11ec-bf63-0242ac130002"),
    UUID.fromString("00732d26-5ac3-11ec-bf63-0242ac130002"),
    UUID.fromString("00733082-5ac3-11ec-bf63-0242ac130002"),
    UUID.fromString("00733190-5ac3-11ec-bf63-0242ac130002")
  };

  @Override
  public void addAttributes(IToolStackView tool, int level, EquipmentSlot slot, BiConsumer<Attribute,AttributeModifier> consumer) {
    if (slot.getType() == Type.ARMOR) {
      // +5% damage per level
      consumer.accept(Attributes.ATTACK_DAMAGE, new AttributeModifier(UUIDS[slot.getIndex()], "tconstruct.modifier.strength." + slot.getName(), 0.1f * getScaledLevel(tool, level), Operation.MULTIPLY_TOTAL));
    }
  }
}
