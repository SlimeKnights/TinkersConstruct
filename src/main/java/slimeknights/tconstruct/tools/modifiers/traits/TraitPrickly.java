package slimeknights.tconstruct.tools.modifiers.traits;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import slimeknights.tconstruct.library.traits.AbstractTrait;

public class TraitPrickly extends AbstractTrait {

  private static final DamageSource meanCactus = (new DamageSource(DamageSource.cactus.damageType)).setDamageBypassesArmor().setDamageIsAbsolute();

  public TraitPrickly() {
    super("prickly", EnumChatFormatting.DARK_GREEN);
  }

  @Override
  public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
    if(target.isEntityAlive()) {
      causeDamage(player, target);
    }
  }

  @Override
  public void onBlock(ItemStack tool, EntityPlayer player, LivingHurtEvent event) {
    Entity source = event.source.getEntity();
    if(source instanceof EntityLivingBase && source.isEntityAlive()) {
      causeDamage(player, (EntityLivingBase) source);
    }
  }

  private void causeDamage(EntityLivingBase player, EntityLivingBase target) {
    float damage = 0.5f + (float) random.nextGaussian() / 2f;
    EntityDamageSource damageSource = new EntityDamageSource(DamageSource.cactus.damageType, player);
    damageSource.setDamageBypassesArmor();
    damageSource.setDamageIsAbsolute();

    target.attackEntityFrom(damageSource, damage);
  }
}
