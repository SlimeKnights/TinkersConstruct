package slimeknights.tconstruct.library.json.variable.block;

import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.block.BlockPredicate;
import slimeknights.tconstruct.library.json.variable.ConditionalVariable;

/**
 * Gets one of two block properties based on the condition
 */
public record ConditionalBlockVariable(IJsonPredicate<BlockState> condition, BlockVariable ifTrue, BlockVariable ifFalse) implements BlockVariable, ConditionalVariable<IJsonPredicate<BlockState>,BlockVariable> {
  public static final RecordLoadable<ConditionalBlockVariable> LOADER = ConditionalVariable.loadable(BlockPredicate.LOADER, BlockVariable.LOADER, ConditionalBlockVariable::new);

  @Override
  public float getValue(BlockState state) {
    return condition.matches(state) ? ifTrue.getValue(state) : ifFalse.getValue(state);
  }

  @Override
  public RecordLoadable<ConditionalBlockVariable> getLoader() {
    return LOADER;
  }
}
