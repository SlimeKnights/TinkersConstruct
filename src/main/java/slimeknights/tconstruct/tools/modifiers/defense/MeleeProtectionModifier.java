package slimeknights.tconstruct.tools.modifiers.defense;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.tconstruct.library.modifiers.impl.IncrementalModifier;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.TooltipKey;

import javax.annotation.Nullable;
import java.util.List;

public class MeleeProtectionModifier extends IncrementalModifier {
  @Override
  public float getProtectionModifier(IToolStackView tool, int level, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float modifierValue) {
    // by melee, we mean not projectiles, fire, magic, or explosions
    if (!source.isBypassMagic() && !source.isBypassInvul() && !source.isFire() && !source.isMagic() && !source.isProjectile() && !source.isExplosion()) {
      modifierValue += getScaledLevel(tool, level) * 2;
    }
    return modifierValue;
  }

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    AbstractProtectionModifier.addResistanceTooltip(this, tool, level, 2.0f, tooltip);
  }
}
