package slimeknights.tconstruct.library.json.variable.block;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.crafting.conditions.ICondition;
import slimeknights.mantle.data.loadable.mapping.ConditionalLoadable.ConditionalObject;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.utils.Util;

/**
 * Datagen helper for making conditional {@link BlockVariable}.
 * @param ifTrue      Variable to use if all conditions are true.
 * @param ifFalse     Variable to use if any condition is false.
 * @param conditions  Conditions to evaluate.
 */
@SuppressWarnings("unused") // API
public record LoadConditionBlockVariable(BlockVariable ifTrue, BlockVariable ifFalse, ICondition... conditions) implements BlockVariable, ConditionalObject<BlockVariable> {
  @Override
  public float getValue(BlockState block) {
    return (Util.testConditions(conditions) ? ifTrue : ifFalse).getValue(block);
  }

  @Override
  public RecordLoadable<? extends BlockVariable> getLoader() {
    return BlockVariable.LOADER.getConditionalLoader();
  }
}
