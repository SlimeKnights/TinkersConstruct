package slimeknights.tconstruct.tools.modifiers;

import net.minecraft.world.World;

import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ProjectileModifierTrait;

public class ModFins extends ProjectileModifierTrait {

  public ModFins() {
    super("fins", 0xabcdef);

    addAspects(ModifierAspect.projectileOnly);
  }

  @Override
  public void onMovement(EntityProjectileBase projectile, World world, double slowdown) {
    if(projectile.isInWater()) {
      double speedup = 1f/slowdown;
      projectile.motionX *= speedup;
      projectile.motionY *= speedup;
      projectile.motionZ *= speedup;

      // apply regular slowdown, but a bit less :>
      double regularSlowdown = 1d - projectile.getSlowdown()*0.8d;
      projectile.motionX *= regularSlowdown;
      projectile.motionY *= regularSlowdown;
      projectile.motionZ *= regularSlowdown;
    }
  }
}
