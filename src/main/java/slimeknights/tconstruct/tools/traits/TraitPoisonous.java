package slimeknights.tconstruct.tools.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

import slimeknights.tconstruct.library.traits.AbstractTrait;

public class TraitPoisonous extends AbstractTrait {

  public TraitPoisonous() {
    super("poisonous", 0xffffff);
  }

  @Override
  public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
    if(wasHit && target.isEntityAlive()) {
      target.addPotionEffect(new PotionEffect(MobEffects.POISON, 101));
    }
  }
}
