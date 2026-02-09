package slimeknights.tconstruct.library.modifiers.modules.armor;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.damage.DamageSourcePredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.library.json.math.ModifierFormula;
import slimeknights.tconstruct.library.json.predicate.TinkerPredicate;
import slimeknights.tconstruct.library.json.variable.VariableFormula;
import slimeknights.tconstruct.library.json.variable.protection.ProtectionFormula;
import slimeknights.tconstruct.library.json.variable.protection.ProtectionVariable;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Module preventing some damage when at full health.
 * Note this module has no default hooks. You will want to register it with {@link ModifierHooks#TOOLTIP} plus one of {@link ModifierHooks#MODIFY_HURT} or {@link ModifierHooks#MODIFY_DAMAGE}.
 * @param formula      Formula to apply for damage reduction.
 * @param holder       Condition on the entity wearing the armor.
 * @param damageSource Condition on the damage source.
 * @param tooltipValue Value to use for computing the tooltip formula. Used since often formulas are reductions.
 * @param condition    Condition on the tool and modifier entry.
 */
public record AdjustDamageModule(ProtectionFormula formula, IJsonPredicate<LivingEntity> holder, IJsonPredicate<DamageSource> damageSource, float tooltipValue, ModifierCondition<IToolStackView> condition) implements ModifierModule, ModifyDamageModifierHook, TooltipModifierHook, ConditionalModule<IToolStackView> {
  public static final RecordLoadable<AdjustDamageModule> LOADER = RecordLoadable.create(
    ProtectionFormula.DAMAGE_LOADER.directField(AdjustDamageModule::formula),
    LivingEntityPredicate.LOADER.defaultField("holder", AdjustDamageModule::holder),
    DamageSourcePredicate.LOADER.defaultField("damage_source", AdjustDamageModule::damageSource),
    FloatLoadable.ANY.defaultField("tooltip_value", 1f, true, AdjustDamageModule::tooltipValue),
    ModifierCondition.TOOL_FIELD,
    AdjustDamageModule::new);

  /** @apiNote use {@link #builder()} */
  @Internal
  public AdjustDamageModule {}

  @Override
  public RecordLoadable<? extends ModifierModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return List.of();
  }

  @Override
  public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
    if (condition.matches(tool, modifier) && this.holder.matches(context.getEntity()) && this.damageSource.matches(source)) {
      amount = formula.apply(tool, modifier, context, context.getEntity(), slotType, source, amount);
    }
    return amount;
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry entry, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    if (condition.matches(tool, entry) && TinkerPredicate.matchesInTooltip(holder, player, tooltipKey)) {
      // using tooltip value then subtracting it later ensures we produce a proper offset within the allowed range
      float reduction = formula.apply(tool, entry, null, tooltipKey == TooltipKey.SHIFT ? player : null, null, null, tooltipValue) - tooltipValue;
      if (reduction != 0) {
        Modifier modifier = entry.getModifier();
        Component name = Component.translatable(modifier.getTranslationKey() + ".damage_adjustment");
        if (formula.percent()) {
          TooltipModifierHook.addPercentBoost(modifier, name, reduction, tooltip);
        } else {
          TooltipModifierHook.addFlatBoost(modifier, name, reduction, tooltip);
        }
      }
    }
  }


  /* Builder */

  /** Creates a new builder instance */
  public static Builder builder() {
    return new Builder();
  }

  @Setter
  @Accessors(fluent = true)
  public static class Builder extends VariableFormula.Builder<Builder, AdjustDamageModule, ProtectionVariable> {
    private IJsonPredicate<LivingEntity> holder = LivingEntityPredicate.ANY;
    private IJsonPredicate<DamageSource> source = DamageSourcePredicate.CAN_PROTECT;
    private float tooltipValue = 1;

    private Builder() {
      super(ProtectionFormula.VARIABLES);
    }

    /** Sets the source to the given sources anded together */
    @SafeVarargs
    public final Builder sources(IJsonPredicate<DamageSource>... sources) {
      return source(DamageSourcePredicate.and(sources));
    }

    @Override
    protected AdjustDamageModule build(ModifierFormula formula) {
      return new AdjustDamageModule(new ProtectionFormula(formula, variables, percent), holder, source, tooltipValue, condition);
    }
  }
}
