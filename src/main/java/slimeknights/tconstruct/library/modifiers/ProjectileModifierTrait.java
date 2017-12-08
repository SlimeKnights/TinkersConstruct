package slimeknights.tconstruct.library.modifiers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.traits.IProjectileTrait;


public abstract class ProjectileModifierTrait extends ModifierTrait implements IProjectileTrait {

  public ProjectileModifierTrait(String identifier, int color) {
    super(identifier, color);
  }

  public ProjectileModifierTrait(String identifier, int color, int maxLevel, int countPerLevel) {
    super(identifier, color, maxLevel, countPerLevel);
  }

  @Override
  public void onLaunch(EntityProjectileBase projectileBase, World world, @Nullable EntityLivingBase shooter) {

  }

  @Override
  public void onProjectileUpdate(EntityProjectileBase projectile, World world, ItemStack toolStack) {

  }

  @Override
  public void onMovement(EntityProjectileBase projectile, World world, double slowdown) {

  }

  @Override
  public void afterHit(EntityProjectileBase projectile, World world, ItemStack ammoStack, EntityLivingBase attacker, Entity target, double impactSpeed) {

  }
}
