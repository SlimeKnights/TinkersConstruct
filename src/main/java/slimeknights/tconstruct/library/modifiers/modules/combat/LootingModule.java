package slimeknights.tconstruct.library.modifiers.modules.combat;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.damage.DamageSourcePredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.json.predicate.TinkerPredicate;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.ArmorLootingModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.LootingModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.IntLevelModule;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModuleCondition;
import slimeknights.tconstruct.library.modifiers.modules.combat.LootingModule.Loader.Result;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.LootingContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.JsonUtils;

import java.util.List;
import java.util.Set;

/**
 * Module for increasing the looting level, used for tools, on pants, and from bows
 * Currently, does not support incremental.
 */
public interface LootingModule extends ModifierModule, IntLevelModule {
  /** Condition on the entity attacking */
  IJsonPredicate<LivingEntity> holder();
  /** Condition on the target */
  IJsonPredicate<LivingEntity> target();
  /** Condition on the damage source used */
  IJsonPredicate<DamageSource> damageSource();
  /** Condition on the tool and modifier instance */
  ModifierModuleCondition condition();

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

  /** Shared loader logic between both looting modules */
  @RequiredArgsConstructor
  abstract class Loader<T extends LootingModule> implements IGenericLoader<T> {
    /**
     * Record to allow adding additional elements to the base loader without breaking callers (and to simplify the parameters)
     * The constructor of this record is considered API status internal and should not be called.
     */
    record Result(int level, IJsonPredicate<LivingEntity> holder, IJsonPredicate<LivingEntity> target, IJsonPredicate<DamageSource> damageSource, ModifierModuleCondition conditions) {}

    /** Creates a module instance for JSON */
    protected abstract T deserialize(Result result, JsonObject json);
    /** Creates a module instance for the buffer */
    protected abstract T fromNetwork(Result result, FriendlyByteBuf buffer);

    @Override
    public T deserialize(JsonObject json) {
      return deserialize(new Result(
        JsonUtils.getIntMin(json, "level", 1),
        LivingEntityPredicate.LOADER.getAndDeserialize(json, "holder"),
        LivingEntityPredicate.LOADER.getAndDeserialize(json, "target"),
        DamageSourcePredicate.LOADER.getAndDeserialize(json, "damage_source"),
        ModifierModuleCondition.deserializeFrom(json)
      ), json);
    }

    @Override
    public void serialize(T object, JsonObject json) {
      object.condition().serializeInto(json);
      json.addProperty("level", object.level());
      json.add("holder", LivingEntityPredicate.LOADER.serialize(object.holder()));
      json.add("target", LivingEntityPredicate.LOADER.serialize(object.target()));
      json.add("damage_source", DamageSourcePredicate.LOADER.serialize(object.damageSource()));
    }

    @Override
    public T fromNetwork(FriendlyByteBuf buffer) {
      return fromNetwork(new Result(
        buffer.readVarInt(),
        LivingEntityPredicate.LOADER.fromNetwork(buffer),
        LivingEntityPredicate.LOADER.fromNetwork(buffer),
        DamageSourcePredicate.LOADER.fromNetwork(buffer),
        ModifierModuleCondition.fromNetwork(buffer)
      ), buffer);
    }

    @Override
    public void toNetwork(T object, FriendlyByteBuf buffer) {
      buffer.writeVarInt(object.level());
      LivingEntityPredicate.LOADER.toNetwork(object.holder(), buffer);
      LivingEntityPredicate.LOADER.toNetwork(object.target(), buffer);
      DamageSourcePredicate.LOADER.toNetwork(object.damageSource(), buffer);
      object.condition().toNetwork(buffer);
    }
  }

  /** Implementation for weapon looting */
  record Weapon(int level, IJsonPredicate<LivingEntity> holder, IJsonPredicate<LivingEntity> target, IJsonPredicate<DamageSource> damageSource, ModifierModuleCondition condition) implements LootingModule, LootingModifierHook {
    private static final List<ModifierHook<?>> DEFAULT_HOOKS = ModifierModule.<Weapon>defaultHooks(TinkerHooks.WEAPON_LOOTING);
    public static final IGenericLoader<Weapon> LOADER = new Loader<>() {
      @Override
      protected Weapon deserialize(Result result, JsonObject json) {
        return new Weapon(result);
      }

      @Override
      protected Weapon fromNetwork(Result result, FriendlyByteBuf buffer) {
        return new Weapon(result);
      }
    };

    private Weapon(Result result) {
      this(result.level, result.holder, result.target, result.damageSource, result.conditions);
    }

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
    public static final IGenericLoader<Armor> LOADER = new Loader<>() {
      @Override
      protected Armor deserialize(Result result, JsonObject json) {
        return new Armor(result, JsonUtils.deserializeEnumSet(json, "slots", EquipmentSlot.class));
      }

      @Override
      public void serialize(Armor object, JsonObject json) {
        super.serialize(object, json);
        json.add("slots", JsonUtils.serializeEnumCollection(object.slots));
      }

      @Override
      protected Armor fromNetwork(Result result, FriendlyByteBuf buffer) {
        return new Armor(result, JsonUtils.readEnumSet(buffer, EquipmentSlot.class));
      }

      @Override
      public void toNetwork(Armor object, FriendlyByteBuf buffer) {
        super.toNetwork(object, buffer);
        JsonUtils.writeEnumCollection(buffer, object.slots);
      }
    };

    private Armor(Result result, Set<EquipmentSlot> slots) {
      this(result.level, result.holder, result.target, result.damageSource, result.conditions, slots);
    }

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

