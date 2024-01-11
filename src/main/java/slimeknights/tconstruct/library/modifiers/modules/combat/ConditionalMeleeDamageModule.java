package slimeknights.tconstruct.library.modifiers.modules.combat;

import com.google.gson.JsonObject;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.library.json.math.ModifierFormula;
import slimeknights.tconstruct.library.json.math.ModifierFormula.FallbackFormula;
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

import static slimeknights.tconstruct.library.json.math.ModifierFormula.FallbackFormula.BOOST;
import static slimeknights.tconstruct.library.json.math.ModifierFormula.FallbackFormula.PERCENT;

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
  ModifierFormula formula, boolean percent,
  ModifierModuleCondition condition
) implements MeleeDamageModifierHook, ConditionalStatTooltip, ModifierModule {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.MELEE_DAMAGE, TinkerHooks.TOOLTIP);
  /** Variables for the modifier formula */
  private static final String[] VARIABLES = { "level", "damage", "multiplier", "base_damage" };

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
        damage = formula.apply(formula.computeLevel(tool, modifier), damage, tool.getMultiplier(ToolStats.ATTACK_DAMAGE), baseDamage);
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
  public float computeTooltipValue(IToolStackView tool, ModifierEntry entry) {
    return formula.apply(formula.computeLevel(tool, entry), 1, tool.getMultiplier(ToolStats.ATTACK_DAMAGE), 1);
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
        ModifierFormula.deserialize(json, VARIABLES, percent ? PERCENT : BOOST), percent,
        ModifierModuleCondition.deserializeFrom(json)
      );
    }

    @Override
    public void serialize(ConditionalMeleeDamageModule object, JsonObject json) {
      object.condition.serializeInto(json);
      json.add("target", LivingEntityPredicate.LOADER.serialize(object.target));
      json.addProperty("percent", object.percent);
      object.formula.serialize(json, VARIABLES);
    }

    @Override
    public ConditionalMeleeDamageModule fromNetwork(FriendlyByteBuf buffer) {
      boolean percent = buffer.readBoolean();
      return new ConditionalMeleeDamageModule(
        LivingEntityPredicate.LOADER.fromNetwork(buffer),
        LivingEntityPredicate.LOADER.fromNetwork(buffer),
        ModifierFormula.fromNetwork(buffer, VARIABLES.length, percent ? PERCENT : BOOST), percent,
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
  public static Builder target(IJsonPredicate<LivingEntity> target) {
    return new Builder(target);
  }

  /** Builder class */
  public static class Builder extends ModifierFormula.Builder<Builder,ConditionalMeleeDamageModule> {
    private final IJsonPredicate<LivingEntity> target;
    @Setter
    @Accessors(fluent = true)
    private IJsonPredicate<LivingEntity> attacker = LivingEntityPredicate.ANY;
    private boolean percent = false;

    private Builder(IJsonPredicate<LivingEntity> target) {
      super(VARIABLES);
      this.target = target;
    }

    /** Sets this to a percent boost formula */
    public Builder percent() {
      this.percent = true;
      return this;
    }

    @Override
    protected ConditionalMeleeDamageModule build(ModifierFormula formula) {
      return new ConditionalMeleeDamageModule(target, attacker, formula, percent, condition);
    }
  }
}
