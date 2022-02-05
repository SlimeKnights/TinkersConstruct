package slimeknights.tconstruct.tools.modifiers.defense;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.UUID;
import java.util.function.BiConsumer;

public class RevitalizingModifier extends IncrementalModifier {
  private static final UUID[] uuids = {
    UUID.fromString("8e4285ac-fcd1-11eb-9a03-0242ac130003"),
    UUID.fromString("462e7884-3228-11ec-8d3d-0242ac130003"),
    UUID.fromString("462e7bae-3228-11ec-8d3d-0242ac130003"),
    UUID.fromString("462e7ca8-3228-11ec-8d3d-0242ac130003")
  };
  public RevitalizingModifier() {
    super(0x8cf4e2);
  }

  @Override
  public void addAttributes(IToolStackView tool, int level, EquipmentSlot slot, BiConsumer<Attribute,AttributeModifier> consumer) {
    if (slot.getType() == Type.ARMOR) {
      consumer.accept(Attributes.MAX_HEALTH, new AttributeModifier(uuids[slot.getIndex()], "tconstruct.modifier.revitalizing", Math.floor(getScaledLevel(tool, level) * 2), Operation.ADDITION));
    }
  }
}
