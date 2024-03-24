package slimeknights.tconstruct.library.modifiers.modules.combat;

import com.google.common.collect.ImmutableSet;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.damage.DamageSourcePredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.json.predicate.TinkerPredicate;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.ArmorLootingModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.LootingModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.IntLevelModule;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModuleCondition;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModuleCondition.ConditionalModifierModule;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.LootingContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;
import java.util.Set;

/**
 * Module for increasing the looting level, used for tools, on pants, and from bows
 * Currently, does not support incremental.
 */
public interface LootingModule extends ModifierModule, IntLevelModule, ConditionalModifierModule {
  /* Common fields */
  LoadableField<IJsonPredicate<LivingEntity>,LootingModule> HOLDER = LivingEntityPredicate.LOADER.defaultField("holder", LootingModule::holder);
  LoadableField<IJsonPredicate<LivingEntity>,LootingModule> TARGET = LivingEntityPredicate.LOADER.defaultField("target", LootingModule::target);
  LoadableField<IJsonPredicate<DamageSource>,LootingModule> DAMAGE_SOURCE = DamageSourcePredicate.LOADER.defaultField("damage_source", LootingModule::damageSource);

  /** Condition on the entity attacking */
  IJsonPredicate<LivingEntity> holder();
  /** Condition on the target */
  IJsonPredicate<LivingEntity> target();
  /** Condition on the damage source used */
  IJsonPredicate<DamageSource> damageSource();

  /** Checks if the conditions match the given context */
  default boolean matchesConditions(IToolStackView tool, ModifierEntry modifier, LootingContext context) {
    return condition().matches(tool, modifier) && holder().matches(context.getHolder()) && TinkerPredicate.matches(target(), context.getLivingTarget()) && TinkerPredicate.matches(damageSource(), context.getDamageSource());
  }

  /** Creates a new builder instance */
  static Builder builder() {
    return new Builder();
  }

  /** Shared builder instance */
  @SuppressWarnings("unused") // API
  @Setter
  @Accessors(fluent = true)
  class Builder extends ModifierModuleCondition.Builder<Builder> {
    private int level = 1;
    private IJsonPredicate<LivingEntity> holder = LivingEntityPredicate.ANY;
    private IJsonPredicate<LivingEntity> target = LivingEntityPredicate.ANY;
    private IJsonPredicate<DamageSource> damageSource = DamageSourcePredicate.ANY;

    private Builder() {}

    /** Builds a module for weapon looting */
    public Weapon weapon() {
      return new Weapon(level, holder, target, damageSource, condition);
    }

    /**
     * Creates a new armor harvest module
     * @param slots  Slots to allow this to run
     * @return  Module instance
     */
    public Armor armor(EquipmentSlot... slots) {
      if (slots.length == 0) {
        throw new IllegalArgumentException("Must have at least 1 slot");
      }
      // immutable set preserves insertion order
      return new Armor(level, holder, target, damageSource, condition, ImmutableSet.copyOf(slots));
    }

    /** Creates a new armor harvest module with the default slots */
    public Armor armor() {
      return armor(EquipmentSlot.values());
    }
  }

  /** Implementation for weapon looting */
  record Weapon(int level, IJsonPredicate<LivingEntity> holder, IJsonPredicate<LivingEntity> target, IJsonPredicate<DamageSource> damageSource, ModifierModuleCondition condition) implements LootingModule, LootingModifierHook {
    private static final List<ModifierHook<?>> DEFAULT_HOOKS = ModifierModule.<Weapon>defaultHooks(TinkerHooks.WEAPON_LOOTING);
    public static final RecordLoadable<Weapon> LOADER = RecordLoadable.create(IntLevelModule.FIELD, HOLDER, TARGET, DAMAGE_SOURCE, ModifierModuleCondition.FIELD, Weapon::new);

    @Override
    public int updateLooting(IToolStackView tool, ModifierEntry modifier, LootingContext context, int looting) {
      if (matchesConditions(tool, modifier, context)) {
        looting += getLevel(tool, modifier);
      }
      return looting;
    }

    @Override
    public IGenericLoader<? extends ModifierModule> getLoader() {
      return LOADER;
    }

    @Override
    public List<ModifierHook<?>> getDefaultHooks() {
      return DEFAULT_HOOKS;
    }
  }

  /** Implementation for armor looting */
  record Armor(int level, IJsonPredicate<LivingEntity> holder, IJsonPredicate<LivingEntity> target, IJsonPredicate<DamageSource> damageSource, ModifierModuleCondition condition, Set<EquipmentSlot> slots) implements LootingModule, ArmorLootingModifierHook {
    private static final List<ModifierHook<?>> DEFAULT_HOOKS = ModifierModule.<Armor>defaultHooks(TinkerHooks.ARMOR_LOOTING);
    public static final RecordLoadable<Armor> LOADER = RecordLoadable.create(IntLevelModule.FIELD, HOLDER, TARGET, DAMAGE_SOURCE, ModifierModuleCondition.FIELD, TinkerLoadables.EQUIPMENT_SLOT_SET.requiredField("slots", Armor::slots), Armor::new);

    @Override
    public int updateArmorLooting(IToolStackView tool, ModifierEntry modifier, LootingContext context, EquipmentContext equipment, EquipmentSlot slot, int looting) {
      if (slots.contains(slot) && matchesConditions(tool, modifier, context)) {
        looting += getLevel(tool, modifier);
      }
      return looting;
    }

    @Override
    public IGenericLoader<? extends ModifierModule> getLoader() {
      return LOADER;
    }

    @Override
    public List<ModifierHook<?>> getDefaultHooks() {
      return DEFAULT_HOOKS;
    }
  }
}

