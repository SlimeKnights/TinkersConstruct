package slimeknights.tconstruct.library.modifiers.modules.combat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.data.loadable.LegacyLoadable;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.field.RecordField;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.damage.DamageSourcePredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.json.RandomLevelingValue;
import slimeknights.tconstruct.library.json.predicate.TinkerPredicate;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.DamageDealtModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MonsterMeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.BlockBreakModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.special.PlantHarvestModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.special.ShearsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.special.sling.SlingLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.BooleanPredicate;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModuleBuilder;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.tools.modules.armor.CounterModule;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNullElse;
import static slimeknights.tconstruct.TConstruct.RANDOM;

/** Module that applies a mob effect on various causes. */
public interface MobEffectModule extends ModifierModule, ConditionalModule<IToolStackView> {
  /** Shared effect field instance */
  RecordField<ModifierMobEffect, MobEffectModule> EFFECT_FIELD = ModifierMobEffect.LOADER.directField(MobEffectModule::effect);
  /** Shared chance field */
  RecordField<LevelingValue, MobEffectModule> CHANCE_FIELD = LevelingValue.LOADABLE.defaultField("chance", LevelingValue.ONE, MobEffectModule::chance);

  /** @deprecated use {@link Weapon#LOADER} or {@link ArmorCounter#LOADER}. */
  @Deprecated
  RecordLoadable<Legacy> LOADER = LegacyLoadable.message(RecordLoadable.create(
    EFFECT_FIELD, WeaponCommon.BEFORE_MELEE_FIELD,
    LevelingValue.LOADABLE.defaultField("chance", LevelingValue.eachLevel(0.25f), MobEffectModule::chance),
    IntLoadable.FROM_ZERO.defaultField("counter_durability_usage", 1, CounterCommon::durabilityUsage),
    ModifierCondition.TOOL_FIELD,
    Legacy::new), "Using deprecated modifier module 'tconstruct:mob_effect'. Use one or both of 'tconstruct:weapon_mob_effect' or 'tconstruct:counter_mob_effect'");

  /** Gets the effect for this module */
  ModifierMobEffect effect();

  /** Chance of the effect applying. */
  default LevelingValue chance() {
    return LevelingValue.ONE;
  }

  /** Checks if the modifier passes the chance to apply. */
  default boolean checkChance(float level) {
    float chance = this.chance().compute(level);
    return chance > 0 && (chance >= 1 || RANDOM.nextFloat() < chance);
  }

  /** Checks if the modifier passes the chance to apply. */
  default boolean checkChance(ModifierEntry entry) {
    return checkChance(entry.getEffectiveLevel());
  }

  @Override
  RecordLoadable<? extends MobEffectModule> getLoader();


  /** Creates a builder instance */
  static MobEffectModule.Builder builder(MobEffect effect) {
    return new Builder(effect);
  }

  /** Creates a builder instance */
  static MobEffectModule.Builder builder(Supplier<? extends MobEffect> effect) {
    return new Builder(effect.get());
  }

  /** Builder for this modifier in datagen */
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  @Accessors(fluent = true)
  @Setter
  class Builder extends ModuleBuilder.Stack<Builder> {
    // general fields
    /** Effect to apply. */
    private final MobEffect effect;
    /** Entity getting the effect. */
    private IJsonPredicate<LivingEntity> target = LivingEntityPredicate.ANY;
    /** Entity using the weapon. Unused for non-combat effects. */
    private IJsonPredicate<LivingEntity> holder = LivingEntityPredicate.ANY;
    /** Effect level, starting from 1. */
    private RandomLevelingValue level = RandomLevelingValue.flat(1);
    /** Effect time in ticks. */
    private RandomLevelingValue time = RandomLevelingValue.flat(0);
    /** Chance of applying the effect. */
    @Nullable
    private LevelingValue chance = null;

    // weapon
    /** Applies the effect before melee hit instead of after. */
    private boolean applyBeforeMelee = false;

    // armor
    /** Direct damage condition for the armor attack */
    @Nullable
    private BooleanPredicate directDamage = null;
    /** Damage source condition for applying on armor attack */
    private IJsonPredicate<DamageSource> damageSource = DamageSourcePredicate.ANY;
    // counter
    /** Amount of durability spent applying this modifier to counter-attacks. TODO 1.21: rename to {@code durabilityUsage} */
    private int counterDurabilityUsage = 1;
    /** If true, the counter module targets ourselves instead of the attacker. For non-counter modules, {@link #buildToolUsage()} will target yourself. */
    private boolean targetSelf = false;

    // tool usage
    /** Predicate for whether to apply the effect to AOE tool usages. */
    private BooleanPredicate isAoe = BooleanPredicate.FALSE;
    /** Predicate for whether to apply the effect to usages from projectiles, such as throwing or arrow hit */
    private BooleanPredicate isProjectile = BooleanPredicate.FALSE;


    /** Builds the effect */
    private ModifierMobEffect buildEffect() {
      return new ModifierMobEffect(effect, level, time, target);
    }

    /** Effect targets an entity hit with this weapon, melee or ranged */
    public Weapon buildWeapon() {
      return new Weapon(buildEffect(), requireNonNullElse(chance, LevelingValue.ONE), holder, applyBeforeMelee, condition);
    }

    /** Effect targets the entity attacking us */
    public ArmorCounter buildCounter() {
      return new ArmorCounter(buildEffect(), requireNonNullElse(chance, LevelingValue.eachLevel(0.15f)), holder, requireNonNullElse(directDamage, BooleanPredicate.TRUE), damageSource, counterDurabilityUsage, targetSelf, condition);
    }

    /** Effect targets entities attacked by us while wearing this as armor */
    public ArmorAttack buildArmorAttack() {
      return new ArmorAttack(buildEffect(), requireNonNullElse(chance, LevelingValue.ONE), holder, requireNonNullElse(directDamage, BooleanPredicate.ALWAYS), damageSource, condition);
    }

    /** Effect targets ourselves after using the tool */
    public ToolUsage buildToolUsage() {
      return new ToolUsage(buildEffect(), requireNonNullElse(chance, LevelingValue.ONE), isAoe, isProjectile, condition);
    }

    /** Builds the finished modifier */
    @Deprecated(forRemoval = true)
    public Legacy build() {
      return new Legacy(buildEffect(), applyBeforeMelee, requireNonNullElse(chance, LevelingValue.eachLevel(0.25f)), counterDurabilityUsage, condition);
    }
  }

  /** Represents a mob effect applied via a modifier. Meant to be nested inside a modifier module. */
  record ModifierMobEffect(MobEffect effect, RandomLevelingValue level, RandomLevelingValue time, IJsonPredicate<LivingEntity> target) {
    public static final RecordLoadable<ModifierMobEffect> LOADER = RecordLoadable.create(
      Loadables.MOB_EFFECT.requiredField("effect", ModifierMobEffect::effect),
      RandomLevelingValue.LOADABLE.requiredField("level", ModifierMobEffect::level),
      RandomLevelingValue.LOADABLE.requiredField("time", ModifierMobEffect::time),
      LivingEntityPredicate.LOADER.defaultField("target", ModifierMobEffect::target),
      ModifierMobEffect::new);

    /** Applies the effect for the given level */
    public void applyEffect(@Nullable LivingEntity target, float scaledLevel, @Nullable Entity cause) {
      if (target == null || !this.target.matches(target)) {
        return;
      }
      int level = Math.round(this.level.computeValue(scaledLevel)) - 1;
      if (level < 0) {
        return;
      }
      float duration = this.time.computeValue(scaledLevel);
      if (duration > 0) {
        target.addEffect(new MobEffectInstance(effect, (int)duration, level), cause);
      }
    }

    /** Applies the effect for the given modifier entry */
    public void applyEffect(@Nullable LivingEntity target, ModifierEntry entry, @Nullable Entity cause) {
      applyEffect(target, entry.getEffectiveLevel(), cause);
    }
  }


  /* Implementations */

  /** Common field between modules which distinguish holder from target */
  interface Combat extends MobEffectModule {
    RecordField<IJsonPredicate<LivingEntity>,Combat> HOLDER_FIELD = LivingEntityPredicate.LOADER.defaultField("holder", Combat::holder);

    /** Predicate on the entity holding this weapon or wearing this armor. */
    default IJsonPredicate<LivingEntity> holder() {
      return LivingEntityPredicate.ANY;
    }
  }

  /** Common logic between {@link Weapon} and {@link Legacy}. TODO 1.21: merge into {@link Weapon} */
  @Internal
  interface WeaponCommon extends Combat, MeleeHitModifierHook, MonsterMeleeHitModifierHook, ProjectileHitModifierHook {
    RecordField<Boolean,WeaponCommon> BEFORE_MELEE_FIELD = BooleanLoadable.INSTANCE.defaultField("apply_before_melee", false, false, WeaponCommon::applyBeforeMelee);

    /** If true, this applies before melee hits instead of after. */
    boolean applyBeforeMelee();

    @Override
    default void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage) {
      if (context.isFullyCharged() && condition().matches(tool, modifier) && checkChance(modifier)) {
        LivingEntity attacker = context.getAttacker();
        if (holder().matches(attacker)) {
          effect().applyEffect(context.getLivingTarget(), modifier, attacker);
        }
      }
    }

    @Override
    default float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
      if (applyBeforeMelee()) {
        onMonsterMeleeHit(tool, modifier, context, damage);
      }
      return knockback;
    }

    @Override
    default void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
      if (!applyBeforeMelee()) {
        onMonsterMeleeHit(tool, modifier, context, damageDealt);
      }
    }

    @Override
    default boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
      if (condition().modifierLevel().test(modifier.getLevel()) && TinkerPredicate.matches(holder(), attacker) && checkChance(modifier)) {
        effect().applyEffect(target, modifier, projectile.getEffectSource());
      }
      return false;
    }
  }

  /** Implementation for melee and ranged weapons, applying the effect to the target. */
  @Internal
  record Weapon(ModifierMobEffect effect, LevelingValue chance, IJsonPredicate<LivingEntity> holder, boolean applyBeforeMelee, ModifierCondition<IToolStackView> condition) implements WeaponCommon {
    private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<Weapon>defaultHooks(ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT, ModifierHooks.PROJECTILE_HIT);
    /** Loader for a weapon effect. */
    public static final RecordLoadable<Weapon> LOADER = RecordLoadable.create(EFFECT_FIELD, CHANCE_FIELD, HOLDER_FIELD, BEFORE_MELEE_FIELD, ModifierCondition.TOOL_FIELD, Weapon::new);

    /** @apiNote use {@link Builder#buildWeapon()} ()} */
    @Internal
    public Weapon {}

    @Override
    public List<ModuleHook<?>> getDefaultHooks() {
      return DEFAULT_HOOKS;
    }

    @Override
    public RecordLoadable<? extends Weapon> getLoader() {
      return LOADER;
    }
  }

  /** Common logic between {@link ArmorCounter} and {@link Legacy}. TODO 1.21: merge into {@link ArmorCounter}. */
  @Internal
  interface CounterCommon extends Combat, OnAttackedModifierHook {
    /** Reimplementation of chance field to change the default */
    RecordField<LevelingValue, CounterCommon> CHANCE_FIELD = LevelingValue.LOADABLE.defaultField("chance", LevelingValue.eachLevel(0.15f), false, CounterCommon::chance);

    /** Gets the amount of durability consumed by counter-attacks. */
    int durabilityUsage();

    /** If true, effect targets ourselves. If false, effect targets the attacker. */
    default boolean targetSelf() {
      return false;
    }

    /** Condition on the direct damage parameter. */
    default BooleanPredicate directDamage() {
      return BooleanPredicate.TRUE;
    }

    /** Condition on the direct damage source. */
    default IJsonPredicate<DamageSource> damageSource() {
      return DamageSourcePredicate.ANY;
    }

    @Override
    default void onAttacked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
      if (directDamage().test(isDirectDamage) && condition().matches(tool, modifier) && damageSource().matches(source)) {
        LivingEntity defender = context.getEntity();
        Entity sourceEntity = source.getEntity();
        float scaledLevel = CounterModule.getLevel(tool, modifier, slotType, defender);
        if (sourceEntity != defender && checkChance(scaledLevel)) {
          // target self if requested
          boolean applied = false;
          if (targetSelf()) {
            // repurpose holder to refer to attacker, as target is now self. makes the JSON a bit weird, but is more flexible
            if (TinkerPredicate.matches(holder(), sourceEntity)) {
              effect().applyEffect(defender, modifier, null);
              applied = true;
            }
          } else if (holder().matches(defender) && sourceEntity instanceof LivingEntity attacker) {
            effect().applyEffect(attacker, scaledLevel, defender);
            applied = true;
          }
          // consume durability
          int durabilityUsage = this.durabilityUsage();
          if (applied && durabilityUsage > 0) {
            ToolDamageUtil.damageAnimated(tool, durabilityUsage, defender, slotType, modifier.getId());
          }
        }
      }
    }
  }

  /** Implementation for counter-attacks from armor */
  record ArmorCounter(ModifierMobEffect effect, LevelingValue chance, IJsonPredicate<LivingEntity> holder, BooleanPredicate directDamage, IJsonPredicate<DamageSource> damageSource, int durabilityUsage, boolean targetSelf, ModifierCondition<IToolStackView> condition) implements CounterCommon {
    private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<ArmorCounter>defaultHooks(ModifierHooks.ON_ATTACKED);
    public static final RecordLoadable<ArmorCounter> LOADER = RecordLoadable.create(
      EFFECT_FIELD, CHANCE_FIELD, HOLDER_FIELD,
      BooleanPredicate.LOADABLE.defaultField("direct_damage", BooleanPredicate.TRUE, ArmorCounter::directDamage),
      DamageSourcePredicate.LOADER.defaultField("damage_source", ArmorCounter::damageSource),
      IntLoadable.FROM_ZERO.defaultField("durability_usage", 1, ArmorCounter::durabilityUsage),
      BooleanLoadable.INSTANCE.defaultField("target_self", false, false, ArmorCounter::targetSelf),
      ModifierCondition.TOOL_FIELD, ArmorCounter::new);

    /** @apiNote use {@link Builder#buildCounter()} */
    @Internal
    public ArmorCounter {}

    @Override
    public List<ModuleHook<?>> getDefaultHooks() {
      return DEFAULT_HOOKS;
    }

    @Override
    public RecordLoadable<ArmorCounter> getLoader() {
      return LOADER;
    }
  }

  /** Module for dealing damage with any weapon while wearing this as armor. */
  record ArmorAttack(ModifierMobEffect effect, LevelingValue chance, IJsonPredicate<LivingEntity> holder, BooleanPredicate directDamage, IJsonPredicate<DamageSource> damageSource, ModifierCondition<IToolStackView> condition) implements Combat, DamageDealtModifierHook {
    private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<ArmorAttack>defaultHooks(ModifierHooks.DAMAGE_DEALT);
    public static final RecordLoadable<ArmorAttack> LOADER = RecordLoadable.create(
      EFFECT_FIELD, CHANCE_FIELD, HOLDER_FIELD,
      BooleanPredicate.LOADABLE.defaultField("direct_damage", BooleanPredicate.ALWAYS, ArmorAttack::directDamage),
      DamageSourcePredicate.LOADER.defaultField("damage_source", ArmorAttack::damageSource),
      ModifierCondition.TOOL_FIELD, ArmorAttack::new);

    /** @apiNote use {@link Builder#buildArmorAttack()} */
    @Internal
    public ArmorAttack {}

    @Override
    public RecordLoadable<? extends MobEffectModule> getLoader() {
      return LOADER;
    }

    @Override
    public List<ModuleHook<?>> getDefaultHooks() {
      return DEFAULT_HOOKS;
    }

    @Override
    public void onDamageDealt(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, LivingEntity target, DamageSource source, float amount, boolean isDirectDamage) {
      if (this.directDamage.test(isDirectDamage) && condition.matches(tool, modifier) && this.damageSource.matches(source) && checkChance(modifier)) {
        LivingEntity holder = context.getEntity();
        if (holder != target && this.holder.matches(holder)) {
          effect.applyEffect(target, modifier, holder);
        }
      }
    }
  }

  /** Grants a mob effect when a tool is used to perform its standard tasks. */
  record ToolUsage(ModifierMobEffect effect, LevelingValue chance, BooleanPredicate isAoe, BooleanPredicate isProjectile, ModifierCondition<IToolStackView> condition) implements MobEffectModule, BlockBreakModifierHook, MeleeHitModifierHook, MonsterMeleeHitModifierHook.RedirectAfter, ProjectileLaunchModifierHook, ProjectileHitModifierHook, PlantHarvestModifierHook, ShearsModifierHook, SlingLaunchModifierHook {
    private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<ToolUsage>defaultHooks(ModifierHooks.BLOCK_BREAK, ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT, ModifierHooks.PROJECTILE_LAUNCH, ModifierHooks.PROJECTILE_SHOT, ModifierHooks.PROJECTILE_THROWN, ModifierHooks.PROJECTILE_HIT, ModifierHooks.PLANT_HARVEST, ModifierHooks.SHEAR_ENTITY, ModifierHooks.SLING_LAUNCH);
    public static final RecordLoadable<ToolUsage> LOADER = RecordLoadable.create(
      EFFECT_FIELD, CHANCE_FIELD,
      BooleanPredicate.LOADABLE.defaultField("is_aoe", BooleanPredicate.FALSE, ToolUsage::isAoe),
      BooleanPredicate.LOADABLE.defaultField("is_projectile", BooleanPredicate.FALSE, ToolUsage::isProjectile),
      ModifierCondition.TOOL_FIELD, ToolUsage::new);

    @Override
    public RecordLoadable<? extends MobEffectModule> getLoader() {
      return LOADER;
    }

    @Override
    public List<ModuleHook<?>> getDefaultHooks() {
      return DEFAULT_HOOKS;
    }

    @Override
    public void afterBlockBreak(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context) {
      if (isAoe.test(context.isAOE()) && isProjectile.test(context.isProjectile()) && condition.matches(tool, modifier) && checkChance(modifier)) {
        effect.applyEffect(context.getLiving(), modifier, null);
      }
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
      if (context.isFullyCharged() && isAoe.test(context.isExtraAttack()) && isProjectile.test(context.isProjectile()) && condition.matches(tool, modifier) && checkChance(modifier)) {
        effect.applyEffect(context.getAttacker(), modifier, null);
      }
    }

    @Override
    public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
      // yes, we are launching a projectile, but the intention of that condition is are we a projectile that just hit
      if (isAoe.test(!primary) && isProjectile.test(false) && condition.matches(tool, modifier) && checkChance(modifier)) {
        effect.applyEffect(shooter, modifier, null);
      }
    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target, boolean notBlocked) {
      if (isAoe.test(false) && isProjectile.test(true) && condition.modifierLevel().test(modifier.getLevel()) && checkChance(modifier)) {
        effect.applyEffect(attacker, modifier, null);
      }
      return false;
    }

    @Override
    public boolean onProjectileHitsBlock(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, BlockHitResult hit, @Nullable LivingEntity owner) {
      // calling a miss an AOE
      if (isAoe.test(true) && isProjectile.test(true) && condition.modifierLevel().test(modifier.getLevel()) && checkChance(modifier)) {
        effect.applyEffect(owner, modifier, null);
      }
      return false;
    }

    @Override
    public void afterHarvest(IToolStackView tool, ModifierEntry modifier, UseOnContext context, ServerLevel world, BlockState state, BlockPos pos) {
      if (isAoe.test(false) && isProjectile.test(false) && condition.matches(tool, modifier) && checkChance(modifier)) {
        effect.applyEffect(context.getPlayer(), modifier, null);
      }
    }

    @Override
    public void afterShearEntity(IToolStackView tool, ModifierEntry modifier, Player player, Entity entity, boolean isTarget) {
      if (isAoe.test(!isTarget) && isProjectile.test(false) && condition.matches(tool, modifier) && checkChance(modifier)) {
        effect.applyEffect(player, modifier, null);
      }
    }

    @Override
    public void afterSlingLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity holder, LivingEntity target, ModifierEntry slingSource, float force, float multiplier, Vec3 angle) {
      if (isAoe.test(false) && isProjectile.test(false) && condition.matches(tool, modifier) && checkChance(modifier)) {
        effect.applyEffect(holder, modifier, null);
      }
    }
  }

  /** Legacy implementation that does weapon, armor, and alike. Too many things happening. */
  @Deprecated
  record Legacy(ModifierMobEffect effect, boolean applyBeforeMelee, LevelingValue chance, int durabilityUsage, ModifierCondition<IToolStackView> condition) implements WeaponCommon, CounterCommon {
    private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<Legacy>defaultHooks(ModifierHooks.ON_ATTACKED, ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT, ModifierHooks.PROJECTILE_HIT);

    /** @apiNote use {@link Builder#build()} */
    @Internal
    public Legacy {}

    @Override
    public List<ModuleHook<?>> getDefaultHooks() {
      return DEFAULT_HOOKS;
    }

    @Override
    public RecordLoadable<Legacy> getLoader() {
      return MobEffectModule.LOADER;
    }

    @Override
    public void onAttacked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
      // legacy implementation conditioned on only being armor. For the new module we let you set the condition for that.
      if (tool.hasTag(TinkerTags.Items.ARMOR)) {
        CounterCommon.super.onAttacked(tool, modifier, context, slotType, source, amount, isDirectDamage);
      }
    }
  }
}
