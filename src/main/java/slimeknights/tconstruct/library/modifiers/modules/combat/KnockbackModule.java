package slimeknights.tconstruct.library.modifiers.modules.combat;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.library.json.math.FormulaLoadable;
import slimeknights.tconstruct.library.json.math.ModifierFormula;
import slimeknights.tconstruct.library.json.math.ModifierFormula.FallbackFormula;
import slimeknights.tconstruct.library.json.predicate.TinkerPredicate;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ProjectilePredicate;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/**
 * Module to add knockback to a melee attack
 * @param entity     Filter on entities to receive knockback
 * @param formula    Formula to compute the knockback amount
 */
public record KnockbackModule(IJsonPredicate<LivingEntity> entity, ModifierFormula formula, ProjectilePredicate projectile, ModifierCondition<IToolStackView> condition) implements MeleeHitModifierHook, ModifierModule, ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<KnockbackModule>defaultHooks(ModifierHooks.MELEE_HIT);
  /** Setup for the formula */
  private static final FormulaLoadable FORMULA = new FormulaLoadable(FallbackFormula.ADD, "level", "knockback");
  /** Loader instance */
  public static final RecordLoadable<KnockbackModule> LOADER = RecordLoadable.create(
    LivingEntityPredicate.LOADER.defaultField("entity", KnockbackModule::entity),
    FORMULA.directField(KnockbackModule::formula),
    ProjectilePredicate.LOADABLE.defaultField("allow", ProjectilePredicate.ALWAYS, KnockbackModule::projectile),
    ModifierCondition.TOOL_FIELD,
    KnockbackModule::new);

  /** @apiNote Internal constructor, use {@link #builder()} */
  @Internal
  public KnockbackModule {}

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
    // might want to consider an entity predicate here, this special casing is a bit odd
    if (this.condition.matches(tool, modifier) && projectile.test(context.isProjectile()) && TinkerPredicate.matches(entity, context.getLivingTarget())) {
      return formula.apply(formula.processLevel(modifier), knockback);
    }
    return knockback;
  }


  @Override
  public RecordLoadable<KnockbackModule> getLoader() {
    return LOADER;
  }


  /* Builder */

  /** Creates a builder instance */
  public static Builder builder() {
    return new Builder();
  }

  /** Builder class */
  @Setter
  @Accessors(fluent = true)
  public static class Builder extends ModifierFormula.Builder<Builder,KnockbackModule> {
    private IJsonPredicate<LivingEntity> entity = LivingEntityPredicate.ANY;
    private ProjectilePredicate projectile = ProjectilePredicate.ALWAYS;

    private Builder() {
      super(FORMULA.variables());
    }

    @Override
    protected KnockbackModule build(ModifierFormula formula) {
      return new KnockbackModule(entity, formula, projectile, condition);
    }
  }
}
