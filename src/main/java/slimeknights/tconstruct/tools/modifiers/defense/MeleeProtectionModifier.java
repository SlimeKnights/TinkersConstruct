package slimeknights.tconstruct.tools.modifiers.defense;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
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
  private static boolean doesApply(DamageSource source) {
    if (source.isBypassMagic() || source.isProjectile() || source.isBypassInvul()) {
      return false;
    }
    // if its caused by an entity, require it to simply not be thorns
    // meets most normal melee attacks, like zombies, but also means a melee fire or melee magic attack will work
    if (source.getEntity() != null) {
      return source instanceof EntityDamageSource entityDamage && !entityDamage.isThorns();
    } else {
      // for non-entity damage, require it to not be any other type
      // blocks dall damage, falling blocks, cactus, but not starving, drowning, freezing
      return (source.isFall() || !source.isBypassArmor()) && !source.isFire() && !source.isMagic() && !source.isExplosion();
    }
  }

  @Override
  public float getProtectionModifier(IToolStackView tool, int level, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float modifierValue) {
    if (doesApply(source)) {
      modifierValue += getScaledLevel(tool, level) * 2;
    }
    return modifierValue;
  }

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    AbstractProtectionModifier.addResistanceTooltip(this, tool, level, 2.0f, tooltip);
  }
}
