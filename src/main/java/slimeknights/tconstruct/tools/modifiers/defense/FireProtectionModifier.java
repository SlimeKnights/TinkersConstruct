package slimeknights.tconstruct.tools.modifiers.defense;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantments;
import slimeknights.tconstruct.library.modifiers.dynamic.EnchantmentModifier;
import slimeknights.tconstruct.library.modifiers.impl.IncrementalModifier;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.RestrictedCompoundTag;
import slimeknights.tconstruct.library.utils.TooltipKey;

import javax.annotation.Nullable;
import java.util.List;

public class FireProtectionModifier extends IncrementalModifier {
  @Override
  public void addRawData(IToolStackView tool, int level, RestrictedCompoundTag tag) {
    // only get the fire boost if you have a full level
    EnchantmentModifier.addEnchantmentData(tag, Enchantments.FIRE_PROTECTION, (int)Math.floor(getEffectiveLevel(tool, level)));
  }

  @Override
  public void beforeRemoved(IToolStackView tool, RestrictedCompoundTag tag) {
    EnchantmentModifier.removeEnchantmentData(tag, Enchantments.FIRE_PROTECTION);
  }

  @Override
  public float getProtectionModifier(IToolStackView tool, int level, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float modifierValue) {
    if (!source.isBypassMagic() && !source.isBypassInvul() && source.isFire()) {
      // we already got floored level * 2 boost from the vanilla enchantment, so cancel that out
      float scaledLevel = getEffectiveLevel(tool, level);
      modifierValue += scaledLevel * 2.5f - Math.floor(scaledLevel) * 2f;
    }
    return modifierValue;
  }

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    AbstractProtectionModifier.addResistanceTooltip(this, tool, level, 2.5f, tooltip);
  }
}
