package slimeknights.tconstruct.tools.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

import slimeknights.tconstruct.library.traits.AbstractTrait;

public class TraitFreezing extends AbstractTrait {

  public TraitFreezing() {
    super("freezing", 0xffffff);
  }

  @Override
  public void onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, boolean isCritical) {
    int level = -1;
    PotionEffect potionEffect = target.getActivePotionEffect(MobEffects.SLOWNESS);
    if(potionEffect != null) {
      level = potionEffect.getAmplifier();
    }

    level = Math.min(4, level+1);

    target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 30, level));
  }
}
