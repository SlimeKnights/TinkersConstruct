package slimeknights.tconstruct.tools.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import slimeknights.tconstruct.library.client.particle.Particles;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.traits.AbstractProjectileTrait;
import slimeknights.tconstruct.tools.ranged.TinkerRangedWeapons;

public class TraitEndspeed extends AbstractProjectileTrait {

  public TraitEndspeed() {
    super("endspeed", 0xffffff);
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
    while(!projectile.inGround && projectile.ticksInAir > 2 && sqrDistanceTraveled < 40) {
      double x = projectile.posX;
      double y = projectile.posY;
      double z = projectile.posZ;

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
