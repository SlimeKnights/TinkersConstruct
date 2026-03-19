package slimeknights.tconstruct.library.modifiers.modules.capacity;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.json.predicate.TinkerPredicate;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;

/** Module that adds capacity whenever the tool breaks any number of blocks. */
public record MeleeCapacityModule(IJsonPredicate<LivingEntity> target, IJsonPredicate<LivingEntity> attacker, LevelingInt grant, boolean before, @Nullable ModifierId owner, ModifierCondition<IToolStackView> condition) implements ModifierModule, MeleeHitModifierHook, CapacitySourceModule, ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<MeleeCapacityModule>defaultHooks(ModifierHooks.MELEE_HIT);
  public static final RecordLoadable<MeleeCapacityModule> LOADER = RecordLoadable.create(
    LivingEntityPredicate.LOADER.defaultField("target", MeleeCapacityModule::target),
    LivingEntityPredicate.LOADER.defaultField("attacker", MeleeCapacityModule::attacker),
    LevelingInt.LOADABLE.defaultField("grant", LevelingInt.ZERO, false, MeleeCapacityModule::grant),
    BooleanLoadable.INSTANCE.defaultField("run_before", false, true, MeleeCapacityModule::before),
    OWNER_FIELD, ModifierCondition.TOOL_FIELD,
    MeleeCapacityModule::new);

  /** @apiNote use {@link #builder()} */
  @Internal
  public MeleeCapacityModule {}

  @Override
  public RecordLoadable<MeleeCapacityModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  /** Applies the capacity boost to the modifier. */
  private void apply(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context) {
    if (condition.matches(tool, modifier) && this.attacker.matches(context.getAttacker()) && TinkerPredicate.matches(this.target, context.getLivingTarget())) {
      CapacitySourceModule.apply(tool, barModifier(tool, modifier), 1, grant.compute(modifier.getEffectiveLevel()));
    }
  }

  @Override
  public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
    if (before) {
      apply(tool, modifier, context);
    }
    return knockback;
  }

  @Override
  public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
    if (!before) {
      apply(tool, modifier, context);
    };
  }


  /* Builder */

  /** Creates a new builder instance */
  public static Builder builder() {
    return new Builder();
  }

  @Setter
  @Accessors(fluent = true)
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder extends CapacitySourceModule.Builder<Builder> implements LevelingInt.Builder<MeleeCapacityModule>  {
    private IJsonPredicate<LivingEntity> target = LivingEntityPredicate.ANY;
    private IJsonPredicate<LivingEntity> attacker = LivingEntityPredicate.ANY;
    /** If true, runs in the before hook instead of the after */
    private boolean before = false;

    /** Sets the builder to run before mining. */
    public Builder before() {
      return before(true);
    }

    @Override
    public MeleeCapacityModule amount(int flat, int eachLevel) {
      return new MeleeCapacityModule(target, attacker, new LevelingInt(flat, eachLevel), before, owner, condition);
    }
  }
}
