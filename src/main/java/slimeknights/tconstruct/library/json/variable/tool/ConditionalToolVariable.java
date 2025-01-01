package slimeknights.tconstruct.library.json.variable.tool;

import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.tconstruct.library.json.predicate.tool.ToolContextPredicate;
import slimeknights.tconstruct.library.json.variable.ConditionalVariable;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/**
 * Gets one of two entity properties based on the condition
 */
public record ConditionalToolVariable(IJsonPredicate<IToolContext> condition, ToolVariable ifTrue, ToolVariable ifFalse) implements ToolVariable, ConditionalVariable<IJsonPredicate<IToolContext>,ToolVariable> {
  public static final RecordLoadable<ConditionalToolVariable> LOADER = ConditionalVariable.loadable(ToolContextPredicate.LOADER, ToolVariable.LOADER, ConditionalToolVariable::new);

  @Override
  public float getValue(IToolStackView tool) {
    return condition.matches(tool) ? ifTrue.getValue(tool) : ifFalse.getValue(tool);
  }

  @Override
  public RecordLoadable<ConditionalToolVariable> getLoader() {
    return LOADER;
  }
}
