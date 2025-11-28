package slimeknights.tconstruct.library.modifiers.hook.ranged;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Hook fired when an arrow hits an entity
 * @see LauncherHitModifierHook
 */
public interface ProjectileHitModifierHook {
  /**
   * Called when a projectile hits an entity
   * @param modifiers       Modifiers from the tool firing this arrow
   * @param persistentData  Persistent data on the entity
   * @param modifier        Modifier triggering this hook
   * @param projectile      Projectile that hit the entity
   * @param hit             Hit result
   * @param attacker        Living entity who fired the projectile, null if non-living or not fired
   * @param target          Living target, will be null if not living
   * @return true if the hit should be canceled, preventing vanilla logic
   * @deprecated use {@link #onProjectileHitEntity(ModifierNBT, ModDataNBT, ModifierEntry, Projectile, EntityHitResult, LivingEntity, LivingEntity, boolean)} for shield blocking info. Overriding is okay.
   */
  @Deprecated
  default boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
    return false;
  }

  /**
   * Called when a projectile hits an entity
   * @param modifiers       Modifiers from the tool firing this arrow
   * @param persistentData  Persistent data on the entity
   * @param modifier        Modifier triggering this hook
   * @param projectile      Projectile that hit the entity
   * @param hit             Hit result
   * @param attacker        Living entity who fired the projectile, null if non-living or not fired
   * @param target          Living target, will be null if not living
   * @param notBlocked      If false, the projectile was blocked with a shield. Some modifiers may wish to run their effects regardless, just keep in mind the projectile is often reusable when blocked.
   * @return true if the hit should be canceled, preventing vanilla logic
   */
  default boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target, boolean notBlocked) {
    return notBlocked && onProjectileHitEntity(modifiers, persistentData, modifier, projectile, hit, attacker, target);
  }

  /**
   * Called when a projectile hits a block.
   * @param modifiers       Modifiers from the tool firing this arrow
   * @param persistentData  Persistent data on the entity
   * @param modifier        Modifier triggering this hook
   * @param projectile      Projectile that hit the entity
   * @param hit             Hit result
   * @param attacker        Living entity who fired the projectile, null if non-living or not fired
   * @deprecated Call
   */
  @Deprecated
  default void onProjectileHitBlock(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, BlockHitResult hit, @Nullable LivingEntity attacker) {}

  /**
   * Called when a projectile hits a block.
   * @param modifiers       Modifiers from the tool firing this arrow
   * @param persistentData  Persistent data on the entity
   * @param modifier        Modifier triggering this hook
   * @param projectile      Projectile that hit the entity
   * @param hit             Hit result
   * @param owner           Living entity who fired the projectile, null if non-living or not fired
   */
  default boolean onProjectileHitsBlock(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, BlockHitResult hit, @Nullable LivingEntity owner) {
    onProjectileHitBlock(modifiers, persistentData, modifier, projectile, hit, owner);
    return false;
  }

  /** Merger that runs all hooks and returns true if any did */
  record AllMerger(Collection<ProjectileHitModifierHook> modules) implements ProjectileHitModifierHook {
    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
      for (ProjectileHitModifierHook module : modules) {
        if (module.onProjectileHitEntity(modifiers, persistentData, modifier, projectile, hit, attacker, target)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public void onProjectileHitBlock(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, BlockHitResult hit, @Nullable LivingEntity attacker) {
      for (ProjectileHitModifierHook module : modules) {
        module.onProjectileHitBlock(modifiers, persistentData, modifier, projectile, hit, attacker);
      }
    }

    @Override
    public boolean onProjectileHitsBlock(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, BlockHitResult hit, @Nullable LivingEntity owner) {
      for (ProjectileHitModifierHook module : modules) {
        if (module.onProjectileHitsBlock(modifiers, persistentData, modifier, projectile, hit, owner)) {
          return true;
        }
      }
      return false;
    }
  }
}
