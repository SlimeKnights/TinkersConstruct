package slimeknights.tconstruct.tools.modules.combat;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.common.TinkerDamageTypes;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.special.sling.SlingLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;
import java.util.List;

/** Module to damage the player when shooting projectiles */
public record DamageOnShootModule(float damage, ResourceKey<DamageType> damageType, ModifierCondition<IToolStackView> condition) implements ModifierModule, ProjectileLaunchModifierHook, SlingLaunchModifierHook, ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<DamageOnShootModule>defaultHooks(ModifierHooks.PROJECTILE_LAUNCH, ModifierHooks.PROJECTILE_SHOT, ModifierHooks.PROJECTILE_THROWN, ModifierHooks.SLING_LAUNCH);
  public static final RecordLoadable<DamageOnShootModule> LOADER = RecordLoadable.create(
    FloatLoadable.FROM_ZERO.requiredField("damage", DamageOnShootModule::damage),
    Loadables.DAMAGE_TYPE_KEY.requiredField("damage_type", DamageOnShootModule::damageType),
    ModifierCondition.TOOL_FIELD,
    DamageOnShootModule::new);

  @Override
  public RecordLoadable<DamageOnShootModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
    if (condition.matches(tool, modifier)) {
      shooter.hurt(TinkerDamageTypes.source(shooter.level().registryAccess(), damageType), damage * modifier.getEffectiveLevel());
    }
  }

  @Override
  public void afterSlingLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity holder, LivingEntity target, ModifierEntry slingSource, float force, float multiplier, Vec3 angle) {
    if (condition.matches(tool, modifier)) {
      holder.hurt(TinkerDamageTypes.source(holder.level().registryAccess(), damageType), damage * modifier.getEffectiveLevel());
    }
  }
}
