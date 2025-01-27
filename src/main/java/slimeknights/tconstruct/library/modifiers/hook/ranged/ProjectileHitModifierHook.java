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

/** Hook fired when an arrow hits an entity */
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
   */
  default boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
    return false;
  }

  /**
   * Called when a projectile hits a block.
   * TODO 1.21: bring back canceling behavior once we no longer have to deal with inconsistent APIs on Neo vs Forge?
   * @param modifiers       Modifiers from the tool firing this arrow
   * @param persistentData  Persistent data on the entity
   * @param modifier        Modifier triggering this hook
   * @param projectile      Projectile that hit the entity
   * @param hit             Hit result
   * @param attacker        Living entity who fired the projectile, null if non-living or not fired
   */
  default void onProjectileHitBlock(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, BlockHitResult hit, @Nullable LivingEntity attacker) {}

  /** Merger that runs all hooks and returns true if any did */
  record AllMerger(Collection<ProjectileHitModifierHook> modules) implements ProjectileHitModifierHook {
    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
      boolean ret = false;
      for (ProjectileHitModifierHook module : modules) {
        ret |= module.onProjectileHitEntity(modifiers, persistentData, modifier, projectile, hit, attacker, target);
      }
      return ret;
    }

    @Override
    public void onProjectileHitBlock(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, BlockHitResult hit, @Nullable LivingEntity attacker) {
      for (ProjectileHitModifierHook module : modules) {
        module.onProjectileHitBlock(modifiers, persistentData, modifier, projectile, hit, attacker);
      }
    }
  }
}
