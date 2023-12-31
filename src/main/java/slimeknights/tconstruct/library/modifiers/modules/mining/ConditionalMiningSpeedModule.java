package slimeknights.tconstruct.library.modifiers.modules.mining;

import com.google.gson.JsonObject;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.block.BlockPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.json.math.ModifierFormula;
import slimeknights.tconstruct.library.json.math.ModifierFormula.FallbackFormula;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.BreakSpeedModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModuleCondition;
import slimeknights.tconstruct.library.modifiers.modules.combat.ConditionalDamageModule;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.List;

import static slimeknights.tconstruct.library.json.math.ModifierFormula.FallbackFormula.PERCENT;
import static slimeknights.tconstruct.library.json.math.ModifierFormula.LEVEL;

/**
 * Implementation of attack damage conditioned on the attacker or target's properties
 * @param block      Blocks to boost speed
 * @param holder     Condition on the entity holding this tool
 * @param formula    Damage formula
 * @param percent    If true, formula acts as a percent (try to display as a percent)
 * @param condition  Standard modifier conditions
 */
public record ConditionalMiningSpeedModule(IJsonPredicate<BlockState> block, IJsonPredicate<LivingEntity> holder, boolean requireEffective, ModifierFormula formula, boolean percent, ModifierModuleCondition condition) implements BreakSpeedModifierHook, TooltipModifierHook, ModifierModule {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.BREAK_SPEED);
  /** Variables for the modifier formula */
  private static final String[] VARIABLES = { "level", "original_speed", "new_speed", "multiplier" };
  /** Speed before event listeners ran */
  public static final int ORIGINAL_SPEED = 1;
  /** Speed after modifiers ran */
  public static final int NEW_SPEED = 2;
  /** Mining speed multiplier */
  public static final int MULTIPLIER = 3;
  /** Flat damage fallback */
  public static final FallbackFormula BOOST = arguments -> arguments[NEW_SPEED] + arguments[LEVEL] * arguments[MULTIPLIER];

  @Override
  public void onBreakSpeed(IToolStackView tool, ModifierEntry modifier, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
    if ((isEffective || !requireEffective) && condition.matches(tool, modifier) && block.matches(event.getState())) {
      event.setNewSpeed(formula.apply(formula.computeLevel(tool, modifier), event.getOriginalSpeed(), event.getNewSpeed(), tool.getMultiplier(ToolStats.MINING_SPEED) * miningSpeedModifier));
    }
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry entry, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    // if holding shift, or we have no attacker condition, then we don't need the player to show the tooltip
    if (tool.hasTag(TinkerTags.Items.HARVEST) && condition.matches(tool, entry) && (tooltipKey == TooltipKey.SHIFT || holder == LivingEntityPredicate.ANY || player != null && holder.matches(player))) {
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

  public static final IGenericLoader<ConditionalMiningSpeedModule> LOADER = new IGenericLoader<>() {
    @Override
    public ConditionalMiningSpeedModule deserialize(JsonObject json) {
      boolean percent = GsonHelper.getAsBoolean(json, "percent", false);
      return new ConditionalMiningSpeedModule(
        BlockPredicate.LOADER.getAndDeserialize(json, "blocks"),
        LivingEntityPredicate.LOADER.getAndDeserialize(json, "entity"),
        GsonHelper.getAsBoolean(json, "require_effective", true),
        ModifierFormula.deserialize(json, VARIABLES, percent ? PERCENT : BOOST), percent,
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
        ModifierFormula.fromNetwork(buffer, VARIABLES, percent ? PERCENT : BOOST), percent,
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
  public static Builder blocks(IJsonPredicate<BlockState> blocks) {
    return new Builder(blocks);
  }

  /** Builder class */
  public static class Builder extends ModifierFormula.Builder<ConditionalDamageModule.Builder> {
    private final IJsonPredicate<BlockState> blocks;
    @Setter
    @Accessors(fluent = true)
    private IJsonPredicate<LivingEntity> holder = LivingEntityPredicate.ANY;
    private boolean percent = false;
    private boolean requireEffective = true;

    private Builder(IJsonPredicate<BlockState> blocks) {
      super(VARIABLES, BOOST);
      this.blocks = blocks;
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

    /** Sets this to a percent boost formula */
    public Builder allowIneffective() {
      this.requireEffective = false;
      return this;
    }

    @Override
    protected ModifierModule build(ModifierFormula formula) {
      return new ConditionalMiningSpeedModule(blocks, holder, requireEffective, formula, percent, condition);
    }
  }
}
