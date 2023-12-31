package slimeknights.tconstruct.library.modifiers.modules.combat;

import com.google.gson.JsonObject;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.json.math.ModifierFormula;
import slimeknights.tconstruct.library.json.math.ModifierFormula.FallbackFormula;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModuleCondition;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.List;

import static slimeknights.tconstruct.library.json.math.ModifierFormula.FallbackFormula.PERCENT;
import static slimeknights.tconstruct.library.json.math.ModifierFormula.LEVEL;

/**
 * Implementation of attack damage conditioned on the attacker or target's properties
 * @param target     Target condition
 * @param attacker   Attacker condition
 * @param formula    Damage formula
 * @param percent    If true, formula acts as a percent (try to display as a percent)
 * @param condition  Standard modifier conditions
 */
public record ConditionalDamageModule(
  IJsonPredicate<LivingEntity> target, IJsonPredicate<LivingEntity> attacker,
  ModifierFormula formula, boolean percent,
  ModifierModuleCondition condition
) implements MeleeDamageModifierHook, TooltipModifierHook, ModifierModule {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.MELEE_DAMAGE, TinkerHooks.TOOLTIP);
  /** Variables for the modifier formula */
  private static final String[] VARIABLES = { "level", "base_damage", "damage", "multiplier" };
  // variables for the formula
  /** Damage before any conditional modifiers ran */
  public static final int BASE_DAMAGE = 1;
  /** Damage from the previous conditional modifier */
  public static final int DAMAGE = 2;
  /** Damage multiplier from the tool */
  public static final int MULTIPLIER = 3;
  /** Flat damage fallback */
  public static final FallbackFormula BOOST = arguments -> arguments[DAMAGE] + arguments[LEVEL] * arguments[MULTIPLIER];

  @Override
  public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
    if (condition.matches(tool, modifier) && attacker.matches(context.getAttacker())) {
      LivingEntity target = context.getLivingTarget();
      if (target != null && this.target.matches(target)) {
        damage = formula.apply(formula.computeLevel(tool, modifier), baseDamage, damage, tool.getMultiplier(ToolStats.ATTACK_DAMAGE));
      }
    }
    return damage;
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry entry, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    // if holding shift, or we have no attacker condition, then we don't need the player to show the tooltip
    if (tool.hasTag(TinkerTags.Items.MELEE) && condition.matches(tool, entry) && (tooltipKey == TooltipKey.SHIFT || attacker == LivingEntityPredicate.ANY || player != null && attacker.matches(player))) {
      // it's hard to display a good tooltip value without knowing the details of the formula, best we can do is guess based on the boolean
      // if this is inaccurate, just add this module without the tooltip hook to ignore
      Modifier modifier = entry.getModifier();
      Component stat = TooltipModifierHook.statName(modifier, ToolStats.ATTACK_DAMAGE);
      // subtracting 1 will cancel out the base value or the 100%, based on the type
      float value = formula.apply(formula.computeLevel(tool, entry), 1, 1, tool.getMultiplier(ToolStats.ATTACK_DAMAGE)) - 1;
      if (value != 0) {
        if (percent) {
          TooltipModifierHook.addPercentBoost(modifier, stat, value, tooltip);
        } else {
          TooltipModifierHook.addFlatBoost(modifier, stat, value, tooltip);
        }
      }
    }
  }

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }

  public static final IGenericLoader<ConditionalDamageModule> LOADER = new IGenericLoader<>() {
    @Override
    public ConditionalDamageModule deserialize(JsonObject json) {
      boolean percent = GsonHelper.getAsBoolean(json, "percent", false);
      return new ConditionalDamageModule(
        LivingEntityPredicate.LOADER.getAndDeserialize(json, "target"),
        LivingEntityPredicate.LOADER.getAndDeserialize(json, "attacker"),
        ModifierFormula.deserialize(json, VARIABLES, percent ? PERCENT : BOOST), percent,
        ModifierModuleCondition.deserializeFrom(json)
      );
    }

    @Override
    public void serialize(ConditionalDamageModule object, JsonObject json) {
      object.condition.serializeInto(json);
      json.add("target", LivingEntityPredicate.LOADER.serialize(object.target));
      json.addProperty("percent", object.percent);
      object.formula.serialize(json, VARIABLES);
    }

    @Override
    public ConditionalDamageModule fromNetwork(FriendlyByteBuf buffer) {
      boolean percent = buffer.readBoolean();
      return new ConditionalDamageModule(
        LivingEntityPredicate.LOADER.fromNetwork(buffer),
        LivingEntityPredicate.LOADER.fromNetwork(buffer),
        ModifierFormula.fromNetwork(buffer, VARIABLES.length, percent ? PERCENT : BOOST), percent,
        ModifierModuleCondition.fromNetwork(buffer));
    }

    @Override
    public void toNetwork(ConditionalDamageModule object, FriendlyByteBuf buffer) {
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
  public static class Builder extends ModifierFormula.Builder<Builder> {
    private final IJsonPredicate<LivingEntity> target;
    @Setter
    @Accessors(fluent = true)
    private IJsonPredicate<LivingEntity> attacker = LivingEntityPredicate.ANY;
    private boolean percent = false;

    private Builder(IJsonPredicate<LivingEntity> target) {
      super(VARIABLES, BOOST);
      this.target = target;
    }

    @Override
    protected FallbackFormula getFormula() {
      return percent ? PERCENT : BOOST;
    }

    /** Sets this to a percent boost formula */
    public Builder percent() {
      this.percent = true;
      return this;
    }

    @Override
    protected ModifierModule build(ModifierFormula formula) {
      return new ConditionalDamageModule(target, attacker, formula, percent, condition);
    }
  }
}
