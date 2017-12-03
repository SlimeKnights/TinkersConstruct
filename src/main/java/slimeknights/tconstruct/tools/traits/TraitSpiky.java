package slimeknights.tconstruct.tools.traits;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.shared.client.ParticleEffect;
import slimeknights.tconstruct.tools.TinkerTools;

public class TraitSpiky extends AbstractTrait {

  public TraitSpiky() {
    super("spiky", TextFormatting.DARK_GREEN);
    MinecraftForge.EVENT_BUS.register(this);
  }

  @SubscribeEvent
  public void onPlayerHurt(LivingHurtEvent livingHurtEvent) {
    if(livingHurtEvent.getEntityLiving() instanceof EntityPlayer && !livingHurtEvent.getEntityLiving().getEntityWorld().isRemote) {
      EntityPlayer entityPlayer = (EntityPlayer) livingHurtEvent.getEntityLiving();
      Entity attacker = livingHurtEvent.getSource().getTrueSource();

      if(attacker instanceof EntityLiving && !attacker.isDead && !playerIsBlockingWithSpikyTraitTool(entityPlayer)) {
        ItemStack tool = ToolHelper.playerIsHoldingItemWith(entityPlayer, this::isToolWithTrait);
        if(!tool.isEmpty() && !ToolHelper.isBroken(tool)) {
          dealSpikyDamage(false, tool, entityPlayer, attacker);
        }
      }
    }
  }

  private boolean playerIsBlockingWithSpikyTraitTool(EntityPlayer entityPlayer) {
    return entityPlayer.isActiveItemStackBlocking() && isToolWithTrait(entityPlayer.getActiveItemStack());
  }

  @Override
  public void onBlock(ItemStack tool, EntityPlayer player, LivingHurtEvent event) {
    Entity target = event.getSource().getTrueSource();
    dealSpikyDamage(true, tool, player, target);
  }

  private void dealSpikyDamage(boolean isBlocking, ItemStack tool, EntityPlayer player, Entity target) {
    if(target instanceof EntityLivingBase && target.isEntityAlive()) {
      float damage = ToolHelper.getActualDamage(tool, player);
      if(!isBlocking) {
        damage /= 2;
      }
      EntityDamageSource damageSource = new EntityDamageSource(DamageSource.CACTUS.damageType, player);
      damageSource.setDamageBypassesArmor();
      damageSource.setDamageIsAbsolute();

      int oldHurtResistantTime = target.hurtResistantTime;
      if(attackEntitySecondary(damageSource, damage, target, true, false)) {
        TinkerTools.proxy.spawnEffectParticle(ParticleEffect.Type.HEART_CACTUS, target, 1);
      }
      target.hurtResistantTime = oldHurtResistantTime; // reset to old time
    }
  }
}
