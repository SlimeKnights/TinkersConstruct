package slimeknights.tconstruct.tools.modifiers.armor;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.utils.TooltipFlag;

import java.util.List;

public class MagicProtectionModifier extends IncrementalModifier {
  public MagicProtectionModifier() {
    super(0xF4D362);
  }

  @Override
  public float getProtectionModifier(IModifierToolStack tool, int level, LivingEntity entity, DamageSource source, EquipmentSlotType slotType, float modifierValue) {
    if (!source.isDamageAbsolute() && !source.canHarmInCreative() && source.isMagicDamage()) {
      modifierValue += getScaledLevel(tool, level) * 2;
    }
    return modifierValue;
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, TooltipFlag tooltipFlag) {
    ProtectionModifier.addResistanceTooltip(this, tool, level, 2f, tooltip);
  }
}
