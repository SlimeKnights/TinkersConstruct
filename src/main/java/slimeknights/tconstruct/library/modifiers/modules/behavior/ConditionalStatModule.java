package slimeknights.tconstruct.library.modifiers.modules.behavior;

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
import slimeknights.tconstruct.library.json.variable.stat.ConditionalStatFormula;
import slimeknights.tconstruct.library.json.variable.stat.ConditionalStatVariable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModuleCondition;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.INumericToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Module for common conditional stats, such as on ranged tools
 * @param stat        Stat to boost
 * @param holder      Condition on the tool holder
 * @param formula     Formula to apply
 * @param percent     If true, the formula is a percent formula
 * @param condition   Standard modifier module conditions
 */
public record ConditionalStatModule(INumericToolStat<?> stat, IJsonPredicate<LivingEntity> holder, ConditionalStatFormula formula, boolean percent, ModifierModuleCondition condition) implements ModifierModule, ConditionalStatModifierHook, ConditionalStatTooltip {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.CONDITIONAL_STAT, TinkerHooks.TOOLTIP);
  // variables for the formula
  /** Value from the previous conditional modifier */
  public static final int VALUE = 1;
  /** Stat multiplier from the tool */
  public static final int MULTIPLIER = 2;

  @Nullable
  @Override
  public Integer getPriority() {
    // run multipliers a bit later
    return percent ? 75 : null;
  }

  @Override
  public float modifyStat(IToolStackView tool, ModifierEntry modifier, LivingEntity living, FloatToolStat stat, float baseValue, float multiplier) {
    if (this.stat == stat && condition.matches(tool, modifier) && this.holder.matches(living)) {
      return formula.apply(tool, modifier, living, baseValue, multiplier);
    }
    return baseValue;
  }

  @Override
  public float computeTooltipValue(IToolStackView tool, ModifierEntry entry, @Nullable Player player) {
    return formula.apply(tool, entry, player, 1, 1);
  }

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public IGenericLoader<? extends ConditionalStatModule> getLoader() {
    return LOADER;
  }

  public static final IGenericLoader<ConditionalStatModule> LOADER = new IGenericLoader<>() {
    @Override
    public ConditionalStatModule deserialize(JsonObject json) {
      boolean percent = GsonHelper.getAsBoolean(json, "percent", false);
      return new ConditionalStatModule(
        ToolStats.numericFromJson(GsonHelper.getAsString(json, "stat")),
        LivingEntityPredicate.LOADER.getAndDeserialize(json, "entity"),
        ConditionalStatFormula.deserialize(json, percent), percent,
        ModifierModuleCondition.deserializeFrom(json)
      );
    }

    @Override
    public void serialize(ConditionalStatModule object, JsonObject json) {
      object.condition.serializeInto(json);
      json.addProperty("stat", object.stat.getName().toString());
      json.add("entity", LivingEntityPredicate.LOADER.serialize(object.holder));
      json.addProperty("percent", object.percent);
      object.formula.serialize(json);
    }

    @Override
    public ConditionalStatModule fromNetwork(FriendlyByteBuf buffer) {
      boolean percent = buffer.readBoolean();
      return new ConditionalStatModule(
        ToolStats.numericFromNetwork(buffer),
        LivingEntityPredicate.LOADER.fromNetwork(buffer),
        ConditionalStatFormula.fromNetwork(buffer, percent), percent,
        ModifierModuleCondition.fromNetwork(buffer));
    }

    @Override
    public void toNetwork(ConditionalStatModule object, FriendlyByteBuf buffer) {
      buffer.writeBoolean(object.percent);
      buffer.writeUtf(object.stat.getName().toString());
      LivingEntityPredicate.LOADER.toNetwork(object.holder, buffer);
      object.formula.toNetwork(buffer);
      object.condition.toNetwork(buffer);
    }
  };


  /* Builder */

  /** Creates a builder instance */
  public static Builder stat(INumericToolStat<?> stat) {
    return new Builder(stat);
  }

  /** Builder class */
  public static class Builder extends VariableFormula.Builder<Builder,ConditionalStatModule,ConditionalStatVariable> {
    private final INumericToolStat<?> stat;
    @Setter
    @Accessors(fluent = true)
    private IJsonPredicate<LivingEntity> holder = LivingEntityPredicate.ANY;
    private boolean percent = false;

    private Builder(INumericToolStat<?> stat) {
      super(ConditionalStatFormula.VARIABLES);
      this.stat = stat;
    }

    /** Sets this to a percent boost formula */
    public Builder percent() {
      this.percent = true;
      return this;
    }

    @Override
    protected ConditionalStatModule build(ModifierFormula formula) {
      return new ConditionalStatModule(stat, holder, new ConditionalStatFormula(formula, variables), percent, condition);
    }
  }
}
