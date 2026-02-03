package slimeknights.tconstruct.library.modifiers.modules.combat;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.library.json.math.ModifierFormula;
import slimeknights.tconstruct.library.json.predicate.modifier.ModifierPredicate;
import slimeknights.tconstruct.library.json.variable.VariableFormula;
import slimeknights.tconstruct.library.json.variable.stat.ConditionalStatFormula;
import slimeknights.tconstruct.library.json.variable.stat.ConditionalStatVariable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.hook.special.sling.SlingForceModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/**
 * Module to multiply force of a sling modifier. Allows adding additional bonuses beyond those provided by {@link slimeknights.tconstruct.library.tools.stat.ToolStats#PROJECTILE_DAMAGE}, conditioned on the modifier causing the sling.
 * Does not support formulas with custom variables, for that functionality see {@link slimeknights.tconstruct.library.modifiers.modules.behavior.ConditionalStatModule} or {@link ConditionalPowerModule}.
 * @param target     Filter on entities to receive knockback
 * @param formula    Formula to compute the force multiplier
 */
public record SlingForceModule(IJsonPredicate<LivingEntity> target, IJsonPredicate<LivingEntity> holder, IJsonPredicate<ModifierId> sling, ConditionalStatFormula formula, ModifierCondition<IToolStackView> condition) implements ModifierModule, SlingForceModifierHook, ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<SlingForceModule>defaultHooks(ModifierHooks.SLING_FORCE);
  /** Loader instance */
  public static final RecordLoadable<SlingForceModule> LOADER = RecordLoadable.create(
    LivingEntityPredicate.LOADER.defaultField("target", SlingForceModule::target),
    LivingEntityPredicate.LOADER.defaultField("holder", SlingForceModule::target),
    ModifierPredicate.LOADER.defaultField("sling", SlingForceModule::sling),
    ConditionalStatFormula.LOADER.directField(SlingForceModule::formula),
    ModifierCondition.TOOL_FIELD,
    SlingForceModule::new);

  /** @apiNote Internal constructor, use {@link #builder()} */
  @Internal
  public SlingForceModule {}

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public float modifySlingForce(IToolStackView tool, ModifierEntry modifier, LivingEntity holder, LivingEntity target, ModifierEntry slingSource, float force, float multiplier) {
  if (this.condition.matches(tool, modifier) && this.target.matches(target) && this.holder.matches(holder) && this.sling.matches(slingSource.getId())) {
      return formula.apply(tool, modifier, holder, force, multiplier);
    }
    return force;
  }

  @Override
  public RecordLoadable<SlingForceModule> getLoader() {
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
  public static class Builder extends VariableFormula.Builder<Builder,SlingForceModule, ConditionalStatVariable> {
    private IJsonPredicate<LivingEntity> target = LivingEntityPredicate.ANY;
    private IJsonPredicate<LivingEntity> holder = LivingEntityPredicate.ANY;
    private IJsonPredicate<ModifierId> sling = ModifierPredicate.ANY;

    private Builder() {
      super(ConditionalStatFormula.VARIABLES);
    }

    @Override
    protected SlingForceModule build(ModifierFormula formula) {
      return new SlingForceModule(target, holder, sling, new ConditionalStatFormula(formula, variables, percent), condition);
    }
  }
}
