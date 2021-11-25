package slimeknights.tconstruct.tools.modifiers.defense;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.utils.TooltipFlag;

import java.util.List;

public class MeleeProtectionModifier extends IncrementalModifier {
  public MeleeProtectionModifier() {
    super(0x2376DD);
  }

  @Override
  public float getProtectionModifier(IModifierToolStack tool, int level, EquipmentContext context, EquipmentSlotType slotType, DamageSource source, float modifierValue) {
    // by melee, we mean not projectiles, fire, magic, or explosions
    if (!source.isDamageAbsolute() && !source.canHarmInCreative() && !source.isFireDamage() && !source.isMagicDamage() && !source.isProjectile() && !source.isExplosion() && source != DamageSource.FALL) {
      modifierValue += getScaledLevel(tool, level) * 2;
    }
    return modifierValue;
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, TooltipFlag tooltipFlag) {
    ProtectionModifier.addResistanceTooltip(this, tool, level, 1.0f, tooltip);
  }
}
