package slimeknights.tconstruct.library.modifiers.modules.combat;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.json.math.FormulaLoadable;
import slimeknights.tconstruct.library.json.math.ModifierFormula;
import slimeknights.tconstruct.library.json.math.ModifierFormula.FallbackFormula;
import slimeknights.tconstruct.library.json.predicate.TinkerPredicate;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModuleCondition;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModuleCondition.ConditionalModifierModule;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/**
 * Module to add knockback to a melee attack
 * @param entity     Filter on entities to receive knockback
 * @param formula    Formula to compute the knockback amount
 */
public record KnockbackModule(IJsonPredicate<LivingEntity> entity, ModifierFormula formula, ModifierModuleCondition condition) implements MeleeHitModifierHook, ModifierModule, ConditionalModifierModule {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.MELEE_HIT);
  /** Setup for the formula */
  private static final FormulaLoadable FORMULA = new FormulaLoadable(FallbackFormula.ADD, "level", "knockback");
  /** Loader instance */
  public static final RecordLoadable<KnockbackModule> LOADER = RecordLoadable.create(
    LivingEntityPredicate.LOADER.defaultField("entity", KnockbackModule::entity),
    FORMULA.directField(KnockbackModule::formula),
    ModifierModuleCondition.FIELD,
    KnockbackModule::new);

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
    if (this.condition.matches(tool, modifier)) {
      // might want to consider an entity predicate here, this special casing is a bit odd
      if (TinkerPredicate.matches(entity, context.getLivingTarget())) {
        return formula.apply(formula.computeLevel(tool, modifier), knockback);
      }
    }
    return knockback;
  }


  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }


  /* Builder */

  /** Creates a builder instance */
  public static Builder builder() {
    return new Builder();
  }

  /** Builder class */
  public static class Builder extends ModifierFormula.Builder<Builder,KnockbackModule> {
    @Setter
    @Accessors(fluent = true)
    private IJsonPredicate<LivingEntity> entity = LivingEntityPredicate.ANY;

    private Builder() {
      super(FORMULA.variables());
    }

    @Override
    protected KnockbackModule build(ModifierFormula formula) {
      return new KnockbackModule(entity, formula, condition);
    }
  }
}
