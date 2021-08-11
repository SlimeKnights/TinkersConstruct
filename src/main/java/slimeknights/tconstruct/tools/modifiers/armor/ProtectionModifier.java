package slimeknights.tconstruct.tools.modifiers.armor;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.utils.TooltipFlag;
import slimeknights.tconstruct.library.utils.Util;

import java.util.List;

public class ProtectionModifier extends IncrementalModifier {
  public ProtectionModifier() {
    super(0x2376DD);
  }

  @Override
  public float getProtectionModifier(IModifierToolStack tool, int level, LivingEntity entity, DamageSource source, EquipmentSlotType slotType, float modifierValue) {
    if (!source.isDamageAbsolute() && !source.canHarmInCreative()) {
      modifierValue += getScaledLevel(tool, level);
    }
    return modifierValue;
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, TooltipFlag tooltipFlag) {
    addResistanceTooltip(this, tool, level, 1.0f, tooltip);
  }

  /**
   * Adds the resistance type tooltip to the armor
   * @param modifier    Modifier instance
   * @param tool        Tool getting the tooltip
   * @param level       Modifier level
   * @param multiplier  Amount per level
   * @param tooltip     Tooltip to show
   */
  public static void addResistanceTooltip(IncrementalModifier modifier, IModifierToolStack tool, int level, float multiplier, List<ITextComponent> tooltip) {
    if (tool.hasTag(TinkerTags.Items.ARMOR)) {
      tooltip.add(modifier.applyStyle(new StringTextComponent(Util.PERCENT_BOOST_FORMAT.format(modifier.getScaledLevel(tool, level) * multiplier / 25f))
                               .appendString(" ")
                               .appendSibling(new TranslationTextComponent(modifier.getTranslationKey() + ".resistance"))));
    }
  }
}
