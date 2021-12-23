package slimeknights.tconstruct.tools.modifiers.ability.armor;

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

public class StrengthModifier extends IncrementalModifier {
  private static final UUID[] UUIDS = {
    UUID.fromString("00732ace-5ac3-11ec-bf63-0242ac130002"),
    UUID.fromString("00732d26-5ac3-11ec-bf63-0242ac130002"),
    UUID.fromString("00733082-5ac3-11ec-bf63-0242ac130002"),
    UUID.fromString("00733190-5ac3-11ec-bf63-0242ac130002")
  };

  public StrengthModifier() {
    super(0xEAA727);
  }

  @Override
  public void addAttributes(IModifierToolStack tool, int level, EquipmentSlotType slot, BiConsumer<Attribute,AttributeModifier> consumer) {
    if (slot.getSlotType() == Group.ARMOR) {
      // +5% damage per level
      consumer.accept(Attributes.ATTACK_DAMAGE, new AttributeModifier(UUIDS[slot.getIndex()], "tconstruct.modifier.strength." + slot.getName(), 0.1f * getScaledLevel(tool, level), Operation.MULTIPLY_TOTAL));
    }
  }
}
