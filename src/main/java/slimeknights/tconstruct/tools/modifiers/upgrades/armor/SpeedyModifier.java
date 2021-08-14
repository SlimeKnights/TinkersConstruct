package slimeknights.tconstruct.tools.modifiers.upgrades.armor;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import java.util.UUID;
import java.util.function.BiConsumer;

public class SpeedyModifier extends IncrementalModifier {
  private static final UUID uuid = UUID.fromString("cdcece66-fcd0-11eb-9a03-0242ac130003");
  public SpeedyModifier() {
    super(0xD8B281);
  }

  @Override
  public void addAttributes(IModifierToolStack tool, int level, EquipmentSlotType slot, BiConsumer<Attribute,AttributeModifier> consumer) {
    consumer.accept(Attributes.MOVEMENT_SPEED, new AttributeModifier(uuid, "tconstruct.modifier.speedy", 0.1 * getScaledLevel(tool, level), Operation.MULTIPLY_TOTAL));
  }
}
