package slimeknights.tconstruct.tools.modifiers.upgrades.armor;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.EquipmentSlotType.Group;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import java.util.UUID;
import java.util.function.BiConsumer;

public class ArmorKnockbackModifier extends Modifier {
  private static final UUID[] UUIDS = {
    UUID.fromString("9fac6858-5ac3-11ec-bf63-0242ac130002"),
    UUID.fromString("9fac6aa6-5ac3-11ec-bf63-0242ac130002"),
    UUID.fromString("9fac6cf4-5ac3-11ec-bf63-0242ac130002"),
    UUID.fromString("9fac6e02-5ac3-11ec-bf63-0242ac130002")
  };

  public ArmorKnockbackModifier() {
    super(0xBC9862);
  }

  @Override
  public void addAttributes(IModifierToolStack tool, int level, EquipmentSlotType slot, BiConsumer<Attribute,AttributeModifier> consumer) {
    if (slot.getSlotType() == Group.ARMOR) {
      consumer.accept(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(UUIDS[slot.getIndex()], "tconstruct.modifier.armor_knockback." + slot.getName(), level, Operation.ADDITION));
    }
  }
}
