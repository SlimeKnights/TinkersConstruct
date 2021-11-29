package slimeknights.tconstruct.tools.modifiers.defense;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.ForgeMod;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.utils.TooltipFlag;

import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

public class TurtleShellModifier extends IncrementalModifier {
  private static final UUID[] UUIDS = {
    UUID.fromString("62a1c224-50e5-11ec-bf63-0242ac130002"),
    UUID.fromString("62a1c4a4-50e5-11ec-bf63-0242ac130002"),
    UUID.fromString("62a1c5e4-50e5-11ec-bf63-0242ac130002"),
    UUID.fromString("62a1c6e8-50e5-11ec-bf63-0242ac130002")
  };

  public TurtleShellModifier() {
    super(0x47BF4A);
  }

  @Override
  public void addAttributes(IModifierToolStack tool, int level, EquipmentSlotType slot, BiConsumer<Attribute,AttributeModifier> consumer) {
    consumer.accept(ForgeMod.SWIM_SPEED.get(), new AttributeModifier(UUIDS[slot.getIndex()], "tconstruct.modifier.armor_power." + slot.getName(), 0.05f * getScaledLevel(tool, level), Operation.MULTIPLY_TOTAL));
  }

  @Override
  public float getProtectionModifier(IModifierToolStack tool, int level, EquipmentContext context, EquipmentSlotType slotType, DamageSource source, float modifierValue) {
    if (!source.isDamageAbsolute() && !source.canHarmInCreative()) {
      LivingEntity entity = context.getEntity();
      // helmet/chest boost if eyes in water, legs/boots boost if feet in water
      if ((slotType == EquipmentSlotType.HEAD || slotType == EquipmentSlotType.CHEST) ? entity.eyesInWater : entity.isInWater()) {
        modifierValue += getScaledLevel(tool, level) * 2;
      }
    }
    return modifierValue;
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, TooltipFlag tooltipFlag) {
    ProtectionModifier.addResistanceTooltip(this, tool, level, 2f, tooltip);
  }
}
