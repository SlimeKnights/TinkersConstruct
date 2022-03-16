package slimeknights.tconstruct.tools.modifiers.defense;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.utils.TooltipFlag;
import slimeknights.tconstruct.library.utils.Util;

import java.util.List;

public class ProtectionModifier extends IncrementalModifier {
  public ProtectionModifier() {
    super(0xA8A8A8);
  }

  /** Gets the protection value for the given level and modifier value */
  private double getProtectionValue(IModifierToolStack tool, int level) {
    float scaled = getScaledLevel(tool, level);
    if (scaled > 1) {
      return 0.5 + scaled;
    } else {
      return scaled * 1.5;
    }
  }

  @Override
  public float getProtectionModifier(IModifierToolStack tool, int level, EquipmentContext context, EquipmentSlotType slotType, DamageSource source, float modifierValue) {
    if (!source.isDamageAbsolute() && !source.canHarmInCreative()) {
      modifierValue += getScaledLevel(tool, level);
    }
    return modifierValue;
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, TooltipFlag tooltipFlag) {
    if (tool.hasTag(TinkerTags.Items.ARMOR)) {
      tooltip.add(applyStyle(new StringTextComponent(Util.PERCENT_BOOST_FORMAT.format(getProtectionValue(tool, level) / 25f))
                               .appendString(" ")
                               .appendSibling(new TranslationTextComponent(getTranslationKey() + ".resistance"))));
    }
  }
}
