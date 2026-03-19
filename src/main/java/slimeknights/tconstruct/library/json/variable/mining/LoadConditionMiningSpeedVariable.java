package slimeknights.tconstruct.library.json.variable.mining;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.mantle.data.loadable.mapping.ConditionalLoadable.ConditionalObject;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.modifiers.hook.mining.BreakSpeedContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;

/**
 * Datagen helper for making conditional {@link MiningSpeedVariable}.
 * @param ifTrue      Variable to use if all conditions are true.
 * @param ifFalse     Variable to use if any condition is false.
 * @param conditions  Conditions to evaluate.
 */
@SuppressWarnings("unused") // API
public record LoadConditionMiningSpeedVariable(MiningSpeedVariable ifTrue, MiningSpeedVariable ifFalse, ICondition... conditions) implements MiningSpeedVariable, ConditionalObject<MiningSpeedVariable> {
  @Override
  public float getValue(IToolStackView tool, @Nullable BreakSpeed event, @Nullable Player player, @Nullable Direction sideHit) {
    return (Util.testConditions(conditions) ? ifTrue : ifFalse).getValue(tool, event, player, sideHit);
  }

  @Override
  public float getValue(IToolStackView tool, @Nullable BreakSpeedContext context, @Nullable Player player) {
    return (Util.testConditions(conditions) ? ifTrue : ifFalse).getValue(tool, context, player);
  }

  @Override
  public RecordLoadable<? extends MiningSpeedVariable> getLoader() {
    return MiningSpeedVariable.LOADER.getConditionalLoader();
  }
}
