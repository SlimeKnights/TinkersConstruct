package slimeknights.tconstruct.tools.modules.ranged.common;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;
import java.util.List;

/** Module applying projectile knockback in the opposite direction of punch */
public record ReversePunchModule(LevelingValue amount) implements ModifierModule, ProjectileHitModifierHook {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<ReversePunchModule>defaultHooks(ModifierHooks.PROJECTILE_HIT, ModifierHooks.PROJECTILE_HIT_CLIENT);
  public static final RecordLoadable<ReversePunchModule> LOADER = RecordLoadable.create(LevelingValue.LOADABLE.directField(ReversePunchModule::amount), ReversePunchModule::new);

  @Override
  public RecordLoadable<ReversePunchModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
    if (target != null) {
      // apply requested amount
      float amount = this.amount.compute(modifier.getEffectiveLevel());
      // reverse out the base knockback living entities receive when attacked, only applies to projectiles with owners or arrows though
      if (projectile.getOwner() != null || projectile instanceof AbstractArrow) {
        amount += 0.8f;
      }
      // apply knockback attribute
      amount *= Math.max(0, 1 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));

      // knockback logic based on arrows
      Vec3 motion = projectile.getDeltaMovement().multiply(1, 0, 1).normalize().scale(amount);
      if (motion.lengthSqr() > 0) {
        target.push(-motion.x, 0.1f, -motion.z);
      }
    }
    return false;
  }
}
