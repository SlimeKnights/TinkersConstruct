package slimeknights.tconstruct.tools.modules.ranged;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Modifier to restrict a projectile angle, used also by an event for knockback angle.
 * TODO 1.21: move to {@link slimeknights.tconstruct.tools.modules.ranged.common}
 */
public enum RestrictAngleModule implements ModifierModule, ProjectileLaunchModifierHook.NoShooter {
  INSTANCE;

  private static final ResourceLocation TOTAL_LEVEL = TConstruct.getResource("restrict_angle_level");
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<RestrictAngleModule>defaultHooks(ModifierHooks.PROJECTILE_LAUNCH, ModifierHooks.PROJECTILE_SHOT, ModifierHooks.PROJECTILE_THROWN);
  public static final SingletonLoader<RestrictAngleModule> LOADER = new SingletonLoader<>(INSTANCE);

  @Override
  public SingletonLoader<RestrictAngleModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void onProjectileShoot(IToolStackView tool, ModifierEntry modifier, @Nullable LivingEntity shooter, ItemStack ammo, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
    // store the level in persistent data, so we can have this module add from multiple sources
    int level = persistentData.getInt(TOTAL_LEVEL) + modifier.intEffectiveLevel();
    persistentData.putInt(TOTAL_LEVEL, level);
    RestrictAngleModule.clampDirection(projectile.getDeltaMovement(), level, projectile);
  }


  /* Helpers */

  /** Shared angle logic */
  @SuppressWarnings("SuspiciousNameCombination") // mojang uses the angle between X and Z, but parchment named atan2 as the angle between Y and X, makes IDEA mad as it thinks parameters should swap
  public static Vec3 clampDirection(Vec3 direction, int level, @Nullable Projectile projectile) {
    double oldAngle = Mth.atan2(direction.x, direction.z);
    int possibleDirections = Math.max(4, (int)Math.pow(2, 6 - level)); // don't let directions fall below 4, else you start seeing directional biases
    double radianIncrements = 2 * Math.PI / possibleDirections;
    double newAngle = Math.round(oldAngle / radianIncrements) * radianIncrements;
    direction = direction.yRot((float)(newAngle - oldAngle));
    if (projectile != null) {
      projectile.setDeltaMovement(direction);
      projectile.setYRot((float)(newAngle * 180f / Math.PI));
    }
    return direction;
  }

  /** Called during the living knockback event to apply our effect */
  public static void onKnockback(LivingKnockBackEvent event, int level) {
    // start at 4 directions at level 1, then 32, 16, 8, and 4 by level 4, don't go below 4 directions
    Vec3 direction = clampDirection(new Vec3(event.getRatioX(), 0, event.getRatioZ()), level, null);
    event.setRatioX(direction.x);
    event.setRatioZ(direction.z);
  }
}
