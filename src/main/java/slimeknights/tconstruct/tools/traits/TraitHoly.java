package slimeknights.tconstruct.tools.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.item.ItemStack;

import slimeknights.tconstruct.library.traits.AbstractTrait;

public class TraitHoly extends AbstractTrait {

  public TraitHoly() {
    super("holy", 0xffffff);
  }

  @Override
  public float onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
    if(target.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
      return newDamage + 3f;
    }

    return super.onHit(tool, player, target, damage, newDamage, isCritical);
  }
}
