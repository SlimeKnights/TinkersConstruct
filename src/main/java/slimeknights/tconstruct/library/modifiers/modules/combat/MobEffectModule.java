package slimeknights.tconstruct.library.modifiers.modules.combat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.json.RandomLevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModuleCondition;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModuleCondition.ConditionalModifierModule;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.NamespacedNBT;

import javax.annotation.Nullable;
import java.util.List;

import static slimeknights.tconstruct.TConstruct.RANDOM;

/**
 * Module that applies a mob effect on melee attack, projectile hit, and counterattack
 */
public record MobEffectModule(
  IJsonPredicate<LivingEntity> target,
  MobEffect effect,
  RandomLevelingValue level,
  RandomLevelingValue time,
  ModifierModuleCondition condition
) implements OnAttackedModifierHook, MeleeHitModifierHook, ProjectileLaunchModifierHook, ProjectileHitModifierHook, ModifierModule, ConditionalModifierModule {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.ON_ATTACKED, TinkerHooks.MELEE_HIT, TinkerHooks.PROJECTILE_LAUNCH, TinkerHooks.PROJECTILE_HIT);
  public static final RecordLoadable<MobEffectModule> LOADER = RecordLoadable.create(
    LivingEntityPredicate.LOADER.field("target", MobEffectModule::target),
    Loadables.MOB_EFFECT.field("effect", MobEffectModule::effect),
    RandomLevelingValue.LOADABLE.field("level", MobEffectModule::level),
    RandomLevelingValue.LOADABLE.field("time", MobEffectModule::time),
    ModifierModuleCondition.FIELD,
    MobEffectModule::new);

  /** Creates a builder instance */
  public static MobEffectModule.Builder builder(MobEffect effect) {
    return new Builder(effect);
  }

  /** Applies the effect for the given level */
  private void applyEffect(@Nullable LivingEntity target, float scaledLevel) {
    if (target == null || !this.target.matches(target)) {
      return;
    }
    int level = Math.round(this.level.computeValue(scaledLevel)) - 1;
    if (level < 0) {
      return;
    }
    float duration = this.time.computeValue(scaledLevel);
    if (duration > 0) {
      target.addEffect(new MobEffectInstance(effect, (int)duration, level));
    }
  }

  @Override
  public void onAttacked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
    Entity attacker = source.getEntity();
    if (isDirectDamage && attacker instanceof LivingEntity living) {
      // 15% chance of working per level
      float scaledLevel = modifier.getEffectiveLevel(tool);
      if (RANDOM.nextFloat() < (scaledLevel * 0.25f)) {
        applyEffect(living, scaledLevel);
        ToolDamageUtil.damageAnimated(tool, 1, context.getEntity(), slotType);
      }
    }
  }

  @Override
  public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
    applyEffect(context.getLivingTarget(), modifier.getEffectiveLevel(tool));
  }

  @Override
  public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, NamespacedNBT persistentData, boolean primary) {
    persistentData.putFloat(modifier.getId(), modifier.getEffectiveLevel(tool));
  }

  @Override
  public boolean onProjectileHitEntity(ModifierNBT modifiers, NamespacedNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
    applyEffect(target, persistentData.getFloat(modifier.getId()));
    return false;
  }

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }

  /** Builder for this modifier in datagen */
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  @Accessors(fluent = true)
  @Setter
  public static class Builder extends ModifierModuleCondition.Builder<Builder> {
    private final MobEffect effect;
    private IJsonPredicate<LivingEntity> target = LivingEntityPredicate.ANY;
    private RandomLevelingValue level = RandomLevelingValue.flat(1);
    private RandomLevelingValue time = RandomLevelingValue.flat(0);

    /** Builds the finished modifier */
    public MobEffectModule build() {
      return new MobEffectModule(target, effect, level, time, condition);
    }
  }
}
