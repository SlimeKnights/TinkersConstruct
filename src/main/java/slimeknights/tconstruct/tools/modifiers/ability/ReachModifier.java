package slimeknights.tconstruct.tools.modifiers.ability;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraftforge.common.ForgeMod;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.UUID;
import java.util.function.BiConsumer;

public class ReachModifier extends Modifier {
  // array of UUIDs to apply reach to each slot
  private static final UUID[] REACH_UUIDS = {
    UUID.fromString("5812a5d6-fa77-11eb-9a03-0242ac130003"),
    UUID.fromString("5812a838-fa77-11eb-9a03-0242ac130003"),
    UUID.fromString("5812a93c-fa77-11eb-9a03-0242ac130003"),
    UUID.fromString("5812abda-fa77-11eb-9a03-0242ac130003"),
    UUID.fromString("803c6bd2-fa77-11eb-9a03-0242ac130003")
  };

  @Override
  public void addAttributes(IToolStackView tool, int level, EquipmentSlot slot, BiConsumer<Attribute,AttributeModifier> consumer) {
    if (slot != EquipmentSlot.OFFHAND) {
      consumer.accept(ForgeMod.REACH_DISTANCE.get(), new AttributeModifier(REACH_UUIDS[slot.getFilterFlag()], "tconstruct.modifier.reach", level, Operation.ADDITION));
    }
  }
}
