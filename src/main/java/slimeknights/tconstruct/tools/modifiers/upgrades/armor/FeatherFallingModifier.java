package slimeknights.tconstruct.tools.modifiers.upgrades.armor;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.tconstruct.library.modifiers.impl.IncrementalModifier;
import slimeknights.tconstruct.library.modifiers.modules.armor.ProtectionModule;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.TooltipKey;

import javax.annotation.Nullable;
import java.util.List;

/** @deprecated use {@link ProtectionModule} */
@Deprecated
public class FeatherFallingModifier extends IncrementalModifier {
  @Override
  public float getProtectionModifier(IToolStackView tool, int level, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float modifierValue) {
    if (source.isFall()) {
      modifierValue += getEffectiveLevel(tool, level) * 3.75f;
    }
    return modifierValue;
  }

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    ProtectionModule.addResistanceTooltip(tool, this, getEffectiveLevel(tool, level) * 3.75f, player, tooltip);
  }
}
