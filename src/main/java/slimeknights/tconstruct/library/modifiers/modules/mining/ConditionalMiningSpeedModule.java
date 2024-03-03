package slimeknights.tconstruct.library.modifiers.modules.mining;

import com.google.gson.JsonObject;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.block.BlockPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.library.json.math.ModifierFormula;
import slimeknights.tconstruct.library.json.variable.VariableFormula;
import slimeknights.tconstruct.library.json.variable.mining.MiningSpeedFormula;
import slimeknights.tconstruct.library.json.variable.mining.MiningSpeedVariable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.mining.BreakSpeedModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModuleCondition;
import slimeknights.tconstruct.library.modifiers.modules.behavior.ConditionalStatTooltip;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.INumericToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Implementation of attack damage conditioned on the attacker or target's properties
 * @param block      Blocks to boost speed
 * @param holder     Condition on the entity holding this tool
 * @param formula    Damage formula
 * @param percent    If true, formula acts as a percent (try to display as a percent)
 * @param condition  Standard modifier conditions
 */
public record ConditionalMiningSpeedModule(
  IJsonPredicate<BlockState> block, IJsonPredicate<LivingEntity> holder, boolean requireEffective,
  MiningSpeedFormula formula, boolean percent, ModifierModuleCondition condition
) implements BreakSpeedModifierHook, ConditionalStatTooltip, ModifierModule {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.BREAK_SPEED, TinkerHooks.TOOLTIP);

  @Nullable
  @Override
  public Integer getPriority() {
    // run multipliers a bit later
    return percent ? 75 : null;
  }

  @Override
  public void onBreakSpeed(IToolStackView tool, ModifierEntry modifier, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
    Player player = event.getPlayer();
    if ((isEffective || !requireEffective) && condition.matches(tool, modifier) && block.matches(event.getState()) && holder.matches(player)) {
      event.setNewSpeed(formula.apply(tool, modifier, event, player, sideHit, event.getOriginalSpeed(), event.getNewSpeed(), miningSpeedModifier));
    }
  }

  @Override
  public INumericToolStat<?> stat() {
    return ToolStats.MINING_SPEED;
  }

  @Override
  public float computeTooltipValue(IToolStackView tool, ModifierEntry entry, @Nullable Player player) {
    return formula.apply(tool, entry, null, player, null, 1, 1, 1);
  }

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }

  public static final IGenericLoader<ConditionalMiningSpeedModule> LOADER = new IGenericLoader<>() {
    @Override
    public ConditionalMiningSpeedModule deserialize(JsonObject json) {
      boolean percent = GsonHelper.getAsBoolean(json, "percent", false);
      return new ConditionalMiningSpeedModule(
        BlockPredicate.LOADER.getAndDeserialize(json, "blocks"),
        LivingEntityPredicate.LOADER.getAndDeserialize(json, "entity"),
        GsonHelper.getAsBoolean(json, "require_effective", true),
        MiningSpeedFormula.deserialize(json, percent), percent,
        ModifierModuleCondition.deserializeFrom(json)
      );
    }

    @Override
    public void serialize(ConditionalMiningSpeedModule object, JsonObject json) {
      object.condition.serializeInto(json);
      json.add("blocks", BlockPredicate.LOADER.serialize(object.block));
      json.add("entity", LivingEntityPredicate.LOADER.serialize(object.holder));
      json.addProperty("require_effective", object.requireEffective);
      json.addProperty("percent", object.percent);
      object.formula.serialize(json);
    }

    @Override
    public ConditionalMiningSpeedModule fromNetwork(FriendlyByteBuf buffer) {
      boolean percent = buffer.readBoolean();
      return new ConditionalMiningSpeedModule(
        BlockPredicate.LOADER.fromNetwork(buffer),
        LivingEntityPredicate.LOADER.fromNetwork(buffer),
        buffer.readBoolean(),
        MiningSpeedFormula.fromNetwork(buffer, percent), percent,
        ModifierModuleCondition.fromNetwork(buffer)
      );
    }

    @Override
    public void toNetwork(ConditionalMiningSpeedModule object, FriendlyByteBuf buffer) {
      buffer.writeBoolean(object.percent);
      BlockPredicate.LOADER.toNetwork(object.block, buffer);
      LivingEntityPredicate.LOADER.toNetwork(object.holder, buffer);
      buffer.writeBoolean(object.requireEffective);
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
  public static class Builder extends VariableFormula.Builder<Builder,ConditionalMiningSpeedModule,MiningSpeedVariable> {
    @Setter
    private IJsonPredicate<BlockState> blocks = BlockPredicate.ANY;
    @Setter
    private IJsonPredicate<LivingEntity> holder = LivingEntityPredicate.ANY;
    private boolean percent = false;
    private boolean requireEffective = true;

    private Builder() {
      super(MiningSpeedFormula.VARIABLES);
    }

    /** Sets this to a percent boost formula */
    public Builder percent() {
      this.percent = true;
      return this;
    }

    /** Sets this to a percent boost formula */
    public Builder allowIneffective() {
      this.requireEffective = false;
      return this;
    }

    @Override
    protected ConditionalMiningSpeedModule build(ModifierFormula formula) {
      return new ConditionalMiningSpeedModule(blocks, holder, requireEffective, new MiningSpeedFormula(formula, variables), percent, condition);
    }
  }
}
