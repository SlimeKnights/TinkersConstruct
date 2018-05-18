package slimeknights.tconstruct.tools.traits;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.shared.client.ParticleEffect;
import slimeknights.tconstruct.tools.TinkerTools;

public class TraitSpiky extends AbstractTrait {

  public TraitSpiky() {
    super("spiky", TextFormatting.DARK_GREEN);
    MinecraftForge.EVENT_BUS.register(this);
  }

  @Override
  public void onPlayerHurt(ItemStack tool, EntityPlayer player, EntityLivingBase attacker, LivingHurtEvent event) {
    dealSpikyDamage(false, tool, player, attacker);
  }

  @Override
  public void onBlock(ItemStack tool, EntityPlayer player, LivingHurtEvent event) {
    Entity target = event.getSource().getTrueSource();
    dealSpikyDamage(true, tool, player, target);
  }

  private void dealSpikyDamage(boolean isBlocking, ItemStack tool, EntityPlayer player, Entity target) {
    if(target instanceof EntityLivingBase && target.isEntityAlive() && target != player) {
      float damage = ToolHelper.getActualDamage(tool, player);
      if(!isBlocking) {
        damage /= 2;
      }
      EntityDamageSource damageSource = new EntityDamageSource(DamageSource.CACTUS.damageType, player);
      damageSource.setDamageBypassesArmor();
      damageSource.setDamageIsAbsolute();
      damageSource.setIsThornsDamage();

      int oldHurtResistantTime = target.hurtResistantTime;
      if(attackEntitySecondary(damageSource, damage, target, true, false)) {
        TinkerTools.proxy.spawnEffectParticle(ParticleEffect.Type.HEART_CACTUS, target, 1);
      }
      target.hurtResistantTime = oldHurtResistantTime; // reset to old time
    }
  }
}
