package slimeknights.tconstruct.tools.modifiers.ability.armor;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.impl.IncrementalModifier;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.TooltipKey;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.List;

public class ProtectionModifier extends IncrementalModifier {
  /** Gets the protection value for the given level and modifier value */
  private double getProtectionValue(IToolStackView tool, int level) {
    float scaled = getScaledLevel(tool, level);
    if (scaled > 1) {
      return 0.5 + scaled;
    } else {
      return scaled * 1.5;
    }
  }

  @Override
  public float getProtectionModifier(IToolStackView tool, int level, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float modifierValue) {
    if (!source.isBypassMagic() && !source.isBypassInvul()) {
      modifierValue += getProtectionValue(tool, level);
    }
    return modifierValue;
  }

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    if (tool.hasTag(TinkerTags.Items.ARMOR)) {
      tooltip.add(applyStyle(new TextComponent(Util.PERCENT_BOOST_FORMAT.format(getProtectionValue(tool, level) / 25f))
                               .append(" ")
                               .append(new TranslatableComponent(getTranslationKey() + ".resistance"))));
    }
  }
}
