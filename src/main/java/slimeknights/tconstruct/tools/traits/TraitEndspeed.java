package slimeknights.tconstruct.tools.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;

import slimeknights.tconstruct.library.client.particle.Particles;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.events.TinkerToolEvent;
import slimeknights.tconstruct.library.traits.AbstractProjectileTrait;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.tools.ranged.TinkerRangedWeapons;

public class TraitEndspeed extends AbstractProjectileTrait {

  public TraitEndspeed() {
    super("endspeed", 0xffffff);

    MinecraftForge.EVENT_BUS.register(this);
  }



  @SubscribeEvent
  public void onBowShooting(TinkerToolEvent.OnBowShoot event) {
    if(TinkerUtil.hasTrait(TagUtil.getTagSafe(event.ammo), this.getModifierIdentifier())) {
      event.setBaseInaccuracy(event.getBaseInaccuracy()*2f/3f);
    }
  }

  @Override
  public void onLaunch(EntityProjectileBase projectileBase, World world, @Nullable EntityLivingBase shooter) {
    projectileBase.motionX /= 10d;
    projectileBase.motionY /= 10d;
    projectileBase.motionZ /= 10d;

    projectileBase.setNoGravity(true);
  }

  @Override
  public void onProjectileUpdate(EntityProjectileBase projectile, World world, ItemStack toolStack) {
    double sqrDistanceTraveled = 0d;
    double lastParticle = 0d;
    int ticks = projectile.ticksInAir;
    while(!projectile.inGround && projectile.ticksInAir > 1 && sqrDistanceTraveled < 40) {
      double x = projectile.posX;
      double y = projectile.posY;
      double z = projectile.posZ;

      projectile.ticksInAir = ticks;
      projectile.updateInAir();

      x -= projectile.posX;
      y -= projectile.posY;
      z -= projectile.posZ;

      double travelled = x*x + y*y + z*z;
      sqrDistanceTraveled += travelled;
      if(travelled < 0.001) {
        break;
      }

      lastParticle += travelled;
      if(lastParticle > 0.3d) {
        TinkerRangedWeapons.proxy.spawnParticle(Particles.ENDSPEED,
                                                world,
                                                projectile.posX,
                                                projectile.posY,
                                                projectile.posZ);
        lastParticle = 0;
      }
    }
    projectile.ticksInAir = ticks;
  }

  @Override
  public void onMovement(EntityProjectileBase projectile, World world, double slowdown) {
    // revert slowdown so we don't get stuck midair
    projectile.motionX *= 1d / slowdown;
    projectile.motionY *= 1d / slowdown;
    projectile.motionZ *= 1d / slowdown;

    projectile.motionY -= projectile.getGravity()/250d;
  }
}
