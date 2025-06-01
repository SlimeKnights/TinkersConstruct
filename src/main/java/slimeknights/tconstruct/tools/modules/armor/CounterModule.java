package slimeknights.tconstruct.tools.modules.armor;

import com.mojang.datafixers.util.Function5;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.ToolActions;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModuleBuilder;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.Util;

import java.util.List;

/** Helpers for counterattack modules */
public interface CounterModule extends ModifierModule, OnAttackedModifierHook, ConditionalModule<IToolStackView> {
  List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<CounterModule>defaultHooks(ModifierHooks.ON_ATTACKED);

  @Override
  default List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  /** Percentage chance this applies */
  LevelingValue chance();

  /** Gets the flat amount to apply */
  LevelingValue constant();

  /** Gets the random amount to apply */
  LevelingValue random();

  /** Checks how much durability is used by this interaction */
  int durabilityUsage();

  /** Evaluates the random chance */
  default boolean checkChance(float level) {
    float chance = chance().compute(level);
    if (chance <= 0) {
      return false;
    }
    if (chance >= 1) {
      return true;
    }
    return TConstruct.RANDOM.nextFloat() < chance;
  }

  /** Checks if the target can be affected by this effect */
  default boolean canApply(Entity target) {
    return true;
  }

  /**
   * Applies the counterattack effect
   * @param tool         Tool being used
   * @param modifier     Modifier being used
   * @param value        Computed value to apply
   * @param context      Information on defender and their equipment
   * @param source       Damage that was dealt
   * @param damageDealt  Amount of damage dealt
   */
  void applyEffect(IToolStackView tool, ModifierEntry modifier, float value, EquipmentContext context, Entity attacker, DamageSource source, float damageDealt);

  @Override
  default void onAttacked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float damageDealt, boolean isDirectDamage) {
    // must be direct damage, and must be armor (held or
    Entity attacker = source.getEntity();
    if (isDirectDamage && attacker != null && canApply(attacker) && condition().matches(tool, modifier)) {
      LivingEntity defender = context.getEntity();
      // level is doubled for shields that are actively blocking
      float level = getLevel(tool, modifier, slotType, defender);
      if (checkChance(level)) {
        float value = LevelingValue.applyRandom(level, constant(), random());
        if (value > 0) {
          applyEffect(tool, modifier, value, context, attacker, source, damageDealt);

          // damage the armor if requested
          int durabilityUsage = durabilityUsage();
          if (durabilityUsage > 0) {
            ToolDamageUtil.damageAnimated(tool, durabilityUsage, defender, slotType);
          }
        }
      }
    }
  }

  /**
   * Checks if the given slot is blocking.
   * Essentially a tool stack version of {@link LivingEntity#isBlocking()}.
   */
  static boolean isBlocking(IToolStackView tool, EquipmentSlot slotType, LivingEntity holder) {
    // holder must be using an item with shield block in the same hand as the slot
    return slotType.getType() == Type.HAND && holder.isUsingItem()
      && Util.getSlotType(holder.getUsedItemHand()) == slotType
      && ModifierUtil.canPerformAction(tool, ToolActions.SHIELD_BLOCK)
      // not sure whether its a modifier or a bow blocking, so we do end up creating a second tool stack to check use duration; luckily needs no modifier list parse
      && holder.getItemBySlot(slotType).getUseDuration() - holder.getUseItemRemainingTicks() >= 5;
  }

  /** Gets the scaled level of the modifier, doubling for shields that are blocking */
  static float getLevel(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slotType, LivingEntity defender) {
    float level = modifier.getEffectiveLevel();
    // if its a hand slot, we are blocking, and the hand
    if (isBlocking(tool, slotType, defender)) {
      level *= 2;
    }
    return level;
  }

  /** Creates a loader with the standard parameter set */
  static <T extends CounterModule> RecordLoadable<T> makeLoader(String value, Function5<LevelingValue,LevelingValue,LevelingValue,Integer,ModifierCondition<IToolStackView>,T> constructor) {
    return RecordLoadable.create(
      LevelingValue.LOADABLE.requiredField("chance", CounterModule::chance),
      LevelingValue.LOADABLE.defaultField("constant_" + value, LevelingValue.ZERO, CounterModule::constant),
      LevelingValue.LOADABLE.defaultField("random_" + value, LevelingValue.ZERO, CounterModule::random),
      IntLoadable.FROM_ZERO.requiredField("durability_usage", CounterModule::durabilityUsage),
      ModifierCondition.TOOL_FIELD,
      constructor);
  }

  /** Extendable builder logic */
  @RequiredArgsConstructor
  @Setter
  @Accessors(fluent = true)
  class Builder<T> extends ModuleBuilder.Stack<Builder<T>> {
    private final Function5<LevelingValue,LevelingValue,LevelingValue,Integer,ModifierCondition<IToolStackView>,T> constructor;
    private LevelingValue chance = LevelingValue.eachLevel(0.15f);
    private LevelingValue constant = LevelingValue.ZERO;
    private LevelingValue random = LevelingValue.ZERO;
    private int durabilityUsage = 1;

    /** Common case of leveling chance */
    public Builder<T> chanceLeveling(float value) {
      return chance(LevelingValue.eachLevel(value));
    }

    /** Common case of a flat constant value */
    public Builder<T> constantFlat(float value) {
      return constant(LevelingValue.flat(value));
    }

    /** Common case of a flat random value */
    public Builder<T> randomFlat(float value) {
      return random(LevelingValue.flat(value));
    }

    /** Builds the module */
    public T build() {
      return constructor.apply(chance, constant, random, durabilityUsage, condition);
    }
  }
}
