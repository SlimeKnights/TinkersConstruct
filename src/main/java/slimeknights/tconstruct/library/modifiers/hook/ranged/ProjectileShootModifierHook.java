package slimeknights.tconstruct.library.modifiers.hook.ranged;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;
import java.util.Collection;

/** Hook for modifiers which wish to modify a projectile before its shot. */
public interface ProjectileShootModifierHook {
  /**
   * Hook to modify arrow properties after an arrow is fired. Called serverside only, so randomness is safe.
   * @param tool            Bow instance
   * @param modifier        Modifier being used
   * @param shooter         Entity firing the arrow. May be null if fired from a dispenser
   * @param ammo            Ammo stack used to fire this projectile. May be empty if the projectile is unusual, e.g. fluid projectiles.
   * @param projectile      Projectile to modify
   * @param arrow           Arrow to modify as most modifiers wish to change that, will be null for non-arrow projectiles
   * @param persistentData  Persistent data instance stored on the arrow to write arbitrary data. Note the modifier list was already written
   * @param primary         If true, this is the primary projectile. Multishot may launch multiple
   */
  void onProjectileShoot(IToolStackView tool, ModifierEntry modifier, @Nullable LivingEntity shooter, ItemStack ammo, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary);

  /** Logic to merge multiple hooks into one */
  record AllMerger(Collection<ProjectileShootModifierHook> modules) implements ProjectileShootModifierHook {
    @Override
    public void onProjectileShoot(IToolStackView tool, ModifierEntry modifier, @Nullable LivingEntity shooter, ItemStack ammo, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
      for (ProjectileShootModifierHook module : modules) {
        module.onProjectileShoot(tool, modifier, shooter, ammo, projectile, arrow, persistentData, primary);
      }
    }
  }
}
