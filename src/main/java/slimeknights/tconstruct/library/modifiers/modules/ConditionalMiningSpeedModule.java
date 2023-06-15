package slimeknights.tconstruct.library.modifiers.modules;

import com.google.gson.JsonObject;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.block.BlockPredicate;
import slimeknights.tconstruct.common.TinkerTags.Items;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.BreakSpeedModifierHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.List;

/** Module for boosting the speed of block breaking conditioned on a block state predicate */
public record ConditionalMiningSpeedModule(IJsonPredicate<BlockState> predicate, boolean requireEffective, float bonus) implements BreakSpeedModifierHook, TooltipModifierHook, ModifierModule {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.BREAK_SPEED);

  @Override
  public Integer getPriority() {
    return 125; // run before percentage conditional boosts
  }

  @Override
  public void onBreakSpeed(IToolStackView tool, ModifierEntry modifier, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
    if ((isEffective || !requireEffective) && predicate.matches(event.getState())) {
      event.setNewSpeed(event.getNewSpeed() * (modifier.getEffectiveLevel(tool) * bonus * tool.getMultiplier(ToolStats.MINING_SPEED) * miningSpeedModifier));
    }
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    TooltipModifierHook.addStatBoost(tool, modifier.getModifier(), ToolStats.MINING_SPEED, Items.HARVEST, bonus * modifier.getEffectiveLevel(tool), tooltip);
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
      IJsonPredicate<BlockState> predicate = BlockPredicate.LOADER.getAndDeserialize(json, "blocks");
      boolean requireEffective = GsonHelper.getAsBoolean(json, "require_effective");
      float bonus = GsonHelper.getAsFloat(json, "bonus");
      return new ConditionalMiningSpeedModule(predicate, requireEffective, bonus);
    }

    @Override
    public void serialize(ConditionalMiningSpeedModule object, JsonObject json) {
      json.add("blocks", BlockPredicate.LOADER.serialize(object.predicate));
      json.addProperty("require_effective", object.requireEffective);
      json.addProperty("bonus", object.bonus);
    }

    @Override
    public ConditionalMiningSpeedModule fromNetwork(FriendlyByteBuf buffer) {
      IJsonPredicate<BlockState> predicate = BlockPredicate.LOADER.fromNetwork(buffer);
      boolean requireEffective = buffer.readBoolean();
      float bonus = buffer.readFloat();
      return new ConditionalMiningSpeedModule(predicate, requireEffective, bonus);
    }

    @Override
    public void toNetwork(ConditionalMiningSpeedModule object, FriendlyByteBuf buffer) {
      BlockPredicate.LOADER.toNetwork(object.predicate, buffer);
      buffer.writeBoolean(object.requireEffective);
      buffer.writeFloat(object.bonus);
    }
  };
}
