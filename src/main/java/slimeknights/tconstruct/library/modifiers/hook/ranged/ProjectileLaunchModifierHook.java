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

/** Hook for modifiers which wish to modify an arrow before its fired. */
public interface ProjectileLaunchModifierHook extends ProjectileShootModifierHook {
  /**
   * Hook to modify arrow properties after an arrow is fired. Called serverside only, so randomness is safe.
   * @param tool            Bow instance
   * @param modifier        Modifier being used
   * @param shooter         Entity firing the arrow
   * @param projectile      Projectile to modify
   * @param arrow           Arrow to modify as most modifiers wish to change that, will be null for non-arrow projectiles
   * @param persistentData  Persistent data instance stored on the arrow to write arbitrary data. Note the modifier list was already written
   * @param primary         If true, this is the primary projectile. Multishot may launch multiple
   * @deprecated Call {@link #onProjectileLaunch(IToolStackView, ModifierEntry, LivingEntity, ItemStack, Projectile, AbstractArrow, ModDataNBT, boolean)}. Overriding is fine.
   */
  @Deprecated
  void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary);

  /**
   * Hook to modify arrow properties after an arrow is fired. Called serverside only, so randomness is safe.
   * @param tool            Bow instance
   * @param modifier        Modifier being used
   * @param shooter         Entity firing the arrow
   * @param ammo            Ammo stack used to fire this projectile. May be empty if the projectile is unusual, e.g. fluid projectiles.
   * @param projectile      Projectile to modify
   * @param arrow           Arrow to modify as most modifiers wish to change that, will be null for non-arrow projectiles
   * @param persistentData  Persistent data instance stored on the arrow to write arbitrary data. Note the modifier list was already written
   * @param primary         If true, this is the primary projectile. Multishot may launch multiple
   */
  default void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, ItemStack ammo, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
    onProjectileLaunch(tool, modifier, shooter, projectile, arrow, persistentData, primary);
  }

  @Override
  default void onProjectileShoot(IToolStackView tool, ModifierEntry modifier, @Nullable LivingEntity shooter, ItemStack ammo, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
    if (shooter != null) {
      onProjectileLaunch(tool, modifier, shooter, projectile, arrow, persistentData, primary);
    }
  }

  /** Logic to merge multiple hooks into one */
  record AllMerger(Collection<ProjectileLaunchModifierHook> modules) implements ProjectileLaunchModifierHook {
    @Override
    public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
      for (ProjectileLaunchModifierHook module : modules) {
        module.onProjectileLaunch(tool, modifier, shooter, projectile, arrow, persistentData, primary);
      }
    }

    @Override
    public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, ItemStack ammo, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
      for (ProjectileLaunchModifierHook module : modules) {
        module.onProjectileLaunch(tool, modifier, shooter, ammo, projectile, arrow, persistentData, primary);
      }
    }

    @Override
    public void onProjectileShoot(IToolStackView tool, ModifierEntry modifier, @Nullable LivingEntity shooter, ItemStack ammo, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
      for (ProjectileShootModifierHook module : modules) {
        module.onProjectileShoot(tool, modifier, shooter, ammo, projectile, arrow, persistentData, primary);
      }
    }
  }

  /**
   * Interface to ease migration to {@link ProjectileShootModifierHook} when shooter is unused or optional.
   * TODO 1.21: make the original projectile launch parameter nullable for simplicity.
   */
  interface NoShooter extends ProjectileLaunchModifierHook {
    @Override
    void onProjectileShoot(IToolStackView tool, ModifierEntry modifier, @Nullable LivingEntity shooter, ItemStack ammo, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary);

    @Override
    default void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
      onProjectileShoot(tool, modifier, shooter, ItemStack.EMPTY, projectile, arrow, persistentData, primary);
    }

    @Override
    default void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, ItemStack ammo, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
      onProjectileShoot(tool, modifier, shooter, ammo, projectile, arrow, persistentData, primary);
    }
  }
}
