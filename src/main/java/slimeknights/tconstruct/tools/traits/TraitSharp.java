package slimeknights.tconstruct.tools.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.potion.TinkerPotion;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.shared.client.ParticleEffect;
import slimeknights.tconstruct.tools.TinkerTools;

public class TraitSharp extends AbstractTrait {

  public static TinkerPotion DOT = new DoT();

  public TraitSharp() {
    super("sharp", 0xffffff);
  }

  @Override
  public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
    if(wasHit && target.isEntityAlive()) {
      target.setLastAttackedEntity(player);
      DOT.apply(target, 121);
    }
  }

  protected static void dealDamage(EntityLivingBase target, int level) {
    EntityLivingBase lastAttacker = target.getLastAttackedEntity();
    DamageSource source;
    if(lastAttacker != null) {
      source = new EntityDamageSource("bleed", lastAttacker);
    }
    else {
      source = new DamageSource("bleed");
    }

    int hurtResistantTime = target.hurtResistantTime;
    attackEntitySecondary(source, (level + 1f) / 3f, target, true, true);
    TinkerTools.proxy.spawnEffectParticle(ParticleEffect.Type.HEART_BLOOD, target, 1);
    target.hurtResistantTime = hurtResistantTime;
  }

  public static class DoT extends TinkerPotion {

    public DoT() {
      super(Util.getResource("dot"), true, true);
    }

    @Override
    public boolean isReady(int tick, int level) {
      // every half second
      return tick > 0 && tick % 15 == 0;
    }

    @Override
    public void performEffect(@Nonnull EntityLivingBase target, int level) {
      dealDamage(target, level);
    }
  }

}
