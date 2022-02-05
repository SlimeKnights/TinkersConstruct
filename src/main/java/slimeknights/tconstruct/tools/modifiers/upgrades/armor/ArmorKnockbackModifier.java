package slimeknights.tconstruct.tools.modifiers.upgrades.armor;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.UUID;
import java.util.function.BiConsumer;

public class ArmorKnockbackModifier extends Modifier {
  private static final UUID[] UUIDS = {
    UUID.fromString("9fac6858-5ac3-11ec-bf63-0242ac130002"),
    UUID.fromString("9fac6aa6-5ac3-11ec-bf63-0242ac130002"),
    UUID.fromString("9fac6cf4-5ac3-11ec-bf63-0242ac130002"),
    UUID.fromString("9fac6e02-5ac3-11ec-bf63-0242ac130002")
  };

  @Override
  public void addAttributes(IToolStackView tool, int level, EquipmentSlot slot, BiConsumer<Attribute,AttributeModifier> consumer) {
    if (slot.getType() == Type.ARMOR) {
      consumer.accept(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(UUIDS[slot.getIndex()], "tconstruct.modifier.armor_knockback." + slot.getName(), level, Operation.ADDITION));
    }
  }
}
