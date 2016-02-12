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
    if(target.isEntityAlive() && wasHit) {
      causeDamage(player, target);
    }
  }

  protected void causeDamage(EntityLivingBase player, EntityLivingBase target) {
    float damage = 0.5f + Math.max(-0.5f, (float) random.nextGaussian() * 0.75f);
    if(damage > 0) {
      EntityDamageSource damageSource = new EntityDamageSource(DamageSource.cactus.damageType, player);
      damageSource.setDamageBypassesArmor();
      damageSource.setDamageIsAbsolute();

      this.attackEntitySecondary(damageSource, damage, target, true, false);
    }
  }
}
