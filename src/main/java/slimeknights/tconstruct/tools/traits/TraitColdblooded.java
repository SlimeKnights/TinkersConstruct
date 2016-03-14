package slimeknights.tconstruct.tools.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import slimeknights.tconstruct.library.traits.AbstractTrait;

public class TraitColdblooded extends AbstractTrait {

  public TraitColdblooded() {
    super("coldblooded", 0xff0000);
  }

  @Override
  public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
    if(target.getMaxHealth() == target.getHealth()) {
      newDamage += damage / 2f;
    }
    return super.damage(tool, player, target, damage, newDamage, isCritical);
  }
}
