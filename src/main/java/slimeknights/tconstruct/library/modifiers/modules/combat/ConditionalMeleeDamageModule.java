package slimeknights.tconstruct.library.modifiers.modules.combat;

import com.google.gson.JsonObject;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.library.json.math.ModifierFormula;
import slimeknights.tconstruct.library.json.variable.VariableFormula;
import slimeknights.tconstruct.library.json.variable.melee.MeleeFormula;
import slimeknights.tconstruct.library.json.variable.melee.MeleeVariable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModuleCondition;
import slimeknights.tconstruct.library.modifiers.modules.behavior.ConditionalStatTooltip;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.INumericToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Implementation of attack damage conditioned on the attacker or target's properties
 * @param target     Target condition
 * @param attacker   Attacker condition
 * @param formula    Damage formula
 * @param percent    If true, formula acts as a percent (try to display as a percent)
 * @param condition  Standard modifier conditions
 */
public record ConditionalMeleeDamageModule(
  IJsonPredicate<LivingEntity> target, IJsonPredicate<LivingEntity> attacker,
  MeleeFormula formula, boolean percent,
  ModifierModuleCondition condition
) implements MeleeDamageModifierHook, ConditionalStatTooltip, ModifierModule {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.MELEE_DAMAGE, TinkerHooks.TOOLTIP);

  @Nullable
  @Override
  public Integer getPriority() {
    // run multipliers a bit later
    return percent ? 75 : null;
  }

  @Override
  public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
    if (condition.matches(tool, modifier) && attacker.matches(context.getAttacker())) {
      LivingEntity target = context.getLivingTarget();
      if (target != null && this.target.matches(target)) {
        damage = formula.apply(tool, modifier, context, context.getAttacker(), baseDamage, damage);
      }
    }
    return damage;
  }

  @Override
  public IJsonPredicate<LivingEntity> holder() {
    return attacker;
  }

  @Override
  public INumericToolStat<?> stat() {
    return ToolStats.ATTACK_DAMAGE;
  }

  @Override
  public float computeTooltipValue(IToolStackView tool, ModifierEntry entry, @Nullable Player player) {
    return formula.apply(tool, entry, null, player, 1, 1);
  }

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }

  public static final IGenericLoader<ConditionalMeleeDamageModule> LOADER = new IGenericLoader<>() {
    @Override
    public ConditionalMeleeDamageModule deserialize(JsonObject json) {
      boolean percent = GsonHelper.getAsBoolean(json, "percent", false);
      return new ConditionalMeleeDamageModule(
        LivingEntityPredicate.LOADER.getAndDeserialize(json, "target"),
        LivingEntityPredicate.LOADER.getAndDeserialize(json, "attacker"),
        MeleeFormula.deserialize(json, percent), percent,
        ModifierModuleCondition.deserializeFrom(json)
      );
    }

    @Override
    public void serialize(ConditionalMeleeDamageModule object, JsonObject json) {
      object.condition.serializeInto(json);
      json.add("target", LivingEntityPredicate.LOADER.serialize(object.target));
      json.addProperty("percent", object.percent);
      object.formula.serialize(json);
    }

    @Override
    public ConditionalMeleeDamageModule fromNetwork(FriendlyByteBuf buffer) {
      boolean percent = buffer.readBoolean();
      return new ConditionalMeleeDamageModule(
        LivingEntityPredicate.LOADER.fromNetwork(buffer),
        LivingEntityPredicate.LOADER.fromNetwork(buffer),
        MeleeFormula.fromNetwork(buffer, percent), percent,
        ModifierModuleCondition.fromNetwork(buffer));
    }

    @Override
    public void toNetwork(ConditionalMeleeDamageModule object, FriendlyByteBuf buffer) {
      buffer.writeBoolean(object.percent);
      LivingEntityPredicate.LOADER.toNetwork(object.target, buffer);
      LivingEntityPredicate.LOADER.toNetwork(object.attacker, buffer);
      object.formula.toNetwork(buffer);
      object.condition.toNetwork(buffer);
    }
  };


  /* Builder */

  /** Creates a builder instance */
  public static Builder builder() {
    return new Builder();
  }

  /** Builder class */
  @Accessors(fluent = true)
  public static class Builder extends VariableFormula.Builder<Builder,ConditionalMeleeDamageModule,MeleeVariable> {
    @Setter
    private IJsonPredicate<LivingEntity> target = LivingEntityPredicate.ANY;
    @Setter
    private IJsonPredicate<LivingEntity> attacker = LivingEntityPredicate.ANY;
    private boolean percent = false;

    private Builder() {
      super(MeleeFormula.VARIABLES);
    }

    /** Sets this to a percent boost formula */
    public Builder percent() {
      this.percent = true;
      return this;
    }

    @Override
    protected ConditionalMeleeDamageModule build(ModifierFormula formula) {
      return new ConditionalMeleeDamageModule(target, attacker, new MeleeFormula(formula, variables), percent, condition);
    }
  }
}
