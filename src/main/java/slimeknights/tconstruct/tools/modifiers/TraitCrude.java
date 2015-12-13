package slimeknights.tconstruct.tools.modifiers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import slimeknights.tconstruct.library.traits.AbstractTrait;

public class TraitCrude extends AbstractTrait {

  public TraitCrude() {
    super("crude", EnumChatFormatting.BLACK);
  }

  @Override
  public float onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
    boolean hasArmor = target.getTotalArmorValue() > 0;
    if(hasArmor) {
      // 10% damage boost against unarmed targets!
      return newDamage + damage * 0.1f;
    }
    return super.onHit(tool, player, target, damage, newDamage, isCritical);
  }
}
