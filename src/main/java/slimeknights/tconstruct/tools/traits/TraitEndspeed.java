package slimeknights.tconstruct.tools.traits;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.traits.AbstractProjectileTrait;

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
    while(!projectile.inGround && sqrDistanceTraveled < 40) {
      double x = projectile.posX;
      double y = projectile.posY;
      double z = projectile.posZ;

      projectile.updateInAir();

      x -= projectile.posX;
      y -= projectile.posY;
      z -= projectile.posZ;

      sqrDistanceTraveled += 0.5d;
      //double travelled = x*x + y*y + z*z;
      //sqrDistanceTraveled += travelled;
      //if(travelled < 0.001) {
//        break;
//      }
    }
  }

  @Override
  public void onMovement(EntityProjectileBase projectile, World world, double slowdown) {
    if(world.isRemote) {
      world.spawnParticle(EnumParticleTypes.END_ROD,
                          projectile.posX,
                          projectile.posY,
                          projectile.posZ,
                          0d, 0d, 0d);
    }

    projectile.motionX *= 1d/slowdown;
    projectile.motionY *= 1d/slowdown;
    projectile.motionZ *= 1d/slowdown;

    //projectile.motionY -= projectile.getGravity()/20d;
  }
}
