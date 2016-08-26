package slimeknights.tconstruct.tools.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.traits.IProjectileTrait;

public class TraitHovering extends AbstractTrait implements IProjectileTrait {

  public TraitHovering() {
    super("hovering", 0xffffff);
  }

  @Override
  public void onLaunch(EntityProjectileBase projectileBase, World world, @Nullable EntityLivingBase shooter) {
    projectileBase.motionX /= 2d;
    projectileBase.motionY /= 2d;
    projectileBase.motionZ /= 2d;
  }

  @Override
  public void onProjectileUpdate(EntityProjectileBase projectile, World world, ItemStack toolStack) {
    if(projectile.inGround) {
      return;
    }

    double slowdownCompensation = 0.99f/(1d - projectile.getSlowdown());
    projectile.motionX *= slowdownCompensation;
    projectile.motionY *= slowdownCompensation;
    projectile.motionZ *= slowdownCompensation;

    projectile.motionY += projectile.getGravity()*95d/100d;

    if(world.isRemote && random.nextInt(2) == 0) {
      float vx = (random.nextFloat()-0.5f)/15f;
      float vy = (random.nextFloat())/15f;
      float vz = (random.nextFloat()-0.5f)/15f;
      world.spawnParticle(EnumParticleTypes.FLAME,
                          projectile.posX,
                          projectile.posY,
                          projectile.posZ,
                          vx, vy, vz);
    }
  }
}
