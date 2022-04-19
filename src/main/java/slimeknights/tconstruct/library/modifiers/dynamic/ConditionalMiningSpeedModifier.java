package slimeknights.tconstruct.library.modifiers.dynamic;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.json.predicate.IJsonPredicate;
import slimeknights.tconstruct.library.json.predicate.block.BlockPredicate;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.impl.IncrementalModifier;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.TooltipKey;

import javax.annotation.Nullable;
import java.util.List;

/** Modifier that conditionally boosts mining speed */
@RequiredArgsConstructor
public class ConditionalMiningSpeedModifier extends IncrementalModifier {
  private final IJsonPredicate<BlockState> predicate;
  private final boolean requireEffective;
  private final float bonus;

  @Override
  public int getPriority() {
    return 125; // run before percentage conditional boosts
  }

  @Override
  public void onBreakSpeed(IToolStackView tool, int level, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
    if ((isEffective || !requireEffective) && predicate.matches(event.getState())) {
      event.setNewSpeed(event.getNewSpeed() + (getScaledLevel(tool, level) * bonus * tool.getMultiplier(ToolStats.MINING_SPEED) * miningSpeedModifier));
    }
  }

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    addStatTooltip(tool, ToolStats.MINING_SPEED, TinkerTags.Items.HARVEST, bonus * getScaledLevel(tool, level), tooltip);
  }

  @Override
  public IGenericLoader<? extends Modifier> getLoader() {
    return LOADER;
  }

  /** Loader instance */
  public static final IGenericLoader<ConditionalMiningSpeedModifier> LOADER = new IGenericLoader<>() {
    @Override
    public ConditionalMiningSpeedModifier deserialize(JsonObject json) {
      IJsonPredicate<BlockState> blocks = BlockPredicate.LOADER.getAndDeserialize(json, "blocks");
      boolean requireEffective = GsonHelper.getAsBoolean(json, "require_effective");
      float bonus = GsonHelper.getAsFloat(json, "bonus");
      return new ConditionalMiningSpeedModifier(blocks, requireEffective, bonus);
    }

    @Override
    public void serialize(ConditionalMiningSpeedModifier object, JsonObject json) {
      json.add("blocks", BlockPredicate.LOADER.serialize(object.predicate));
      json.addProperty("require_effective", object.requireEffective);
      json.addProperty("bonus", object.bonus);
    }

    @Override
    public ConditionalMiningSpeedModifier fromNetwork(FriendlyByteBuf buffer) {
      IJsonPredicate<BlockState> blocks = BlockPredicate.LOADER.fromNetwork(buffer);
      boolean requireEffective = buffer.readBoolean();
      float bonus = buffer.readFloat();
      return new ConditionalMiningSpeedModifier(blocks, requireEffective, bonus);
    }

    @Override
    public void toNetwork(ConditionalMiningSpeedModifier object, FriendlyByteBuf buffer) {
      BlockPredicate.LOADER.toNetwork(object.predicate, buffer);
      buffer.writeBoolean(object.requireEffective);
      buffer.writeFloat(object.bonus);
    }
  };
}
