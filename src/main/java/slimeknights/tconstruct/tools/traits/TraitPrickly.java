package slimeknights.tconstruct.tools.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumChatFormatting;

import slimeknights.tconstruct.library.traits.AbstractTrait;

public class TraitPrickly extends AbstractTrait {

  public TraitPrickly() {
    super("prickly", EnumChatFormatting.DARK_GREEN);
  }

  @Override
  public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
    if(target.isEntityAlive()) {
      causeDamage(player, target);
    }
  }

  static void causeDamage(EntityLivingBase player, EntityLivingBase target) {
    float damage = 0.5f + Math.max(-0.5f, (float) random.nextGaussian() / 2f);
    if(damage > 0) {
      EntityDamageSource damageSource = new EntityDamageSource(DamageSource.cactus.damageType, player);
      damageSource.setDamageBypassesArmor();
      damageSource.setDamageIsAbsolute();

      // reset hurt resistance time from being hit before
      target.hurtResistantTime = 0;
      target.attackEntityFrom(damageSource, damage);
    }
  }
}
