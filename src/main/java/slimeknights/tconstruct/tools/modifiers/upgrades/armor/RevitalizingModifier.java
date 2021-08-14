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

public class RevitalizingModifier extends IncrementalModifier {
  private static final UUID uuid = UUID.fromString("8e4285ac-fcd1-11eb-9a03-0242ac130003");
  public RevitalizingModifier() {
    super(0xCFF1F1);
  }

  @Override
  public void addAttributes(IModifierToolStack tool, int level, EquipmentSlotType slot, BiConsumer<Attribute,AttributeModifier> consumer) {
    consumer.accept(Attributes.MAX_HEALTH, new AttributeModifier(uuid, "tconstruct.modifier.revitalizing", Math.floor(getScaledLevel(tool, level) * 2), Operation.ADDITION));
  }
}
