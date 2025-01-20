package slimeknights.tconstruct.library.modifiers.hook.ranged;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent.ImpactResult;
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
   * @return Impact result. Options:
   * <ul>
   *   <li>{@code DEFAULT} means process the hit as normal.</li>
   *   <li>{@code STOP_AT_CURRENT} means process the hit as normal, but clear piercing level.</li>
   *   <li>{@code STOP_AT_CURRENT_NO_DAMAGE} means the hit will not be processed and the entity will be discarded. Further modifiers will not run.</li>
   *   <li>{@code SKIP_ENTITY} means the hit will not be processed for this entity, but the projectile may continue due to piercing. Further modifiers will not run.</li>
   * </ul>
   */
  default ImpactResult onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
    return ImpactResult.DEFAULT;
  }

  /**
   * Called when a projectile hits a block
   * @param modifiers       Modifiers from the tool firing this arrow
   * @param persistentData  Persistent data on the entity
   * @param modifier        Modifier triggering this hook
   * @param projectile      Projectile that hit the entity
   * @param hit             Hit result
   * @param attacker        Living entity who fired the projectile, null if non-living or not fired
   * @return true if the hit should be canceled
   */
  default boolean onProjectileHitBlock(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, BlockHitResult hit, @Nullable LivingEntity attacker) {
    return false;
  }

  /** Merger that runs all hooks and returns true if any did */
  record AllMerger(Collection<ProjectileHitModifierHook> modules) implements ProjectileHitModifierHook {
    @Override
    public ImpactResult onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
      ImpactResult ret = ImpactResult.DEFAULT;
      for (ProjectileHitModifierHook module : modules) {
        ImpactResult newResult = module.onProjectileHitEntity(modifiers, persistentData, modifier, projectile, hit, attacker, target);
        // if a hook says stop at current, continue running
        if (newResult != ImpactResult.DEFAULT) {
          ret = newResult;
        }
        // if a hook says skip or stop, we are done. Former means this entity is ignored while latter means
        if (newResult == ImpactResult.SKIP_ENTITY || newResult == ImpactResult.STOP_AT_CURRENT_NO_DAMAGE) {
          break;
        }
      }
      return ret;
    }

    @Override
    public boolean onProjectileHitBlock(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, BlockHitResult hit, @Nullable LivingEntity attacker) {
      boolean ret = false;
      for (ProjectileHitModifierHook module : modules) {
        ret |= module.onProjectileHitBlock(modifiers, persistentData, modifier, projectile, hit, attacker);
      }
      return ret;
    }
  }
}
