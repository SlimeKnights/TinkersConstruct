package slimeknights.tconstruct.library.modifiers.modules.combat;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IHaveLoader;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.json.math.ModifierFormula;
import slimeknights.tconstruct.library.json.predicate.TinkerPredicate;
import slimeknights.tconstruct.library.json.predicate.tool.ToolStackPredicate;
import slimeknights.tconstruct.library.json.variable.VariableFormula;
import slimeknights.tconstruct.library.json.variable.power.PowerFormula;
import slimeknights.tconstruct.library.json.variable.power.PowerVariable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.entity.ProjectileWithPower;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ConditionalStatTooltip;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.stat.INumericToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Implementation of projectile power conditioned on the attacker or target's properties.
 * It's better to use {@link slimeknights.tconstruct.library.modifiers.modules.behavior.ConditionalStatModule} unless you need the target condition.
 * @param target        Target condition
 * @param holder        Condition on entity using bow
 * @param formula       Power formula
 * @param modifierLevel Modifier level condition
 */
public record ConditionalPowerModule(IJsonPredicate<LivingEntity> target, IJsonPredicate<LivingEntity> holder, PowerFormula formula, IntRange modifierLevel) implements ModifierModule, ProjectileLaunchModifierHook, ProjectileHitModifierHook, ConditionalStatTooltip {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<ConditionalPowerModule>defaultHooks(ModifierHooks.PROJECTILE_LAUNCH, ModifierHooks.PROJECTILE_HIT, ModifierHooks.TOOLTIP);
  public static final RecordLoadable<ConditionalPowerModule> LOADER = RecordLoadable.create(
    LivingEntityPredicate.LOADER.defaultField("target", ConditionalPowerModule::target),
    LivingEntityPredicate.LOADER.defaultField("holder", ConditionalPowerModule::holder),
    PowerFormula.LOADER.directField(ConditionalPowerModule::formula),
    ModifierEntry.VALID_LEVEL.defaultField("modifier_level", ConditionalPowerModule::modifierLevel),
    ConditionalPowerModule::new);
  /** Persistent data key for the stat multiplier */
  private static final ResourceLocation MULTIPLIER = ToolStats.PROJECTILE_DAMAGE.getName().withSuffix("_multiplier");

  /** @apiNote Internal constructor, use {@link #builder()} */
  @Internal
  public ConditionalPowerModule {}

  @Override
  public boolean percent() {
    return formula.percent();
  }

  @Nullable
  @Override
  public Integer getPriority() {
    // run multipliers a bit later
    return percent() ? 75 : null;
  }

  @Override
  public RecordLoadable<? extends IHaveLoader> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
    // copy projectile multiplier into the arrow damage so we can use it later
    persistentData.putFloat(MULTIPLIER, tool.getMultiplier(ToolStats.PROJECTILE_DAMAGE));
  }

  @Override
  public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
    if (modifierLevel.test(modifier.getLevel()) && TinkerPredicate.matches(this.target, target) && TinkerPredicate.matches(this.holder, attacker)) {
      if (projectile instanceof AbstractArrow arrow) {
        arrow.setBaseDamage(formula.apply(modifiers, persistentData, modifier, projectile, hit, attacker, target, arrow.getBaseDamage(), persistentData.getFloat(MULTIPLIER)));
      } else if (projectile instanceof ProjectileWithPower withPower) {
        withPower.setPower(formula.apply(modifiers, persistentData, modifier, projectile, hit, attacker, target, withPower.getPower(), persistentData.getFloat(MULTIPLIER)));
      }
    }
    return false;
  }

  @Override
  public INumericToolStat<?> stat() {
    return ToolStats.PROJECTILE_DAMAGE;
  }

  @Override
  public ModifierCondition<IToolStackView> condition() {
    return new ModifierCondition<>(ToolStackPredicate.ANY, modifierLevel);
  }

  @Override
  public boolean matchesTool(IToolStackView tool, ModifierEntry entry) {
    return modifierLevel.test(entry.getLevel());
  }

  @Override
  public float computeTooltipValue(IToolStackView tool, ModifierEntry entry, @Nullable Player player) {
    return formula.apply(tool.getModifiers(), tool.getPersistentData(), entry, null, null, player, null, 1, tool.getMultiplier(ToolStats.PROJECTILE_DAMAGE));
  }


  /* Builder */

  /** Creates a builder instance */
  public static Builder builder() {
    return new Builder();
  }

  /** Builder class */
  @Setter
  @Accessors(fluent = true)
  public static class Builder extends VariableFormula.Builder<ConditionalPowerModule.Builder,ConditionalPowerModule, PowerVariable> {
    private IJsonPredicate<LivingEntity> target = LivingEntityPredicate.ANY;
    private IJsonPredicate<LivingEntity> holder = LivingEntityPredicate.ANY;

    private Builder() {
      super(PowerFormula.VARIABLES);
    }

    @Override
    protected ConditionalPowerModule build(ModifierFormula formula) {
      return new ConditionalPowerModule(target, holder, new PowerFormula(formula, variables, percent), condition.modifierLevel());
    }
  }
}
