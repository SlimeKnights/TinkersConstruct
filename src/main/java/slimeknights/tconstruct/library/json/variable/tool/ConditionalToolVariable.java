package slimeknights.tconstruct.library.json.variable.tool;

import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.tconstruct.library.json.predicate.tool.ToolContextPredicate;
import slimeknights.tconstruct.library.json.variable.ConditionalVariableLoader;
import slimeknights.tconstruct.library.json.variable.ConditionalVariableLoader.ConditionalVariable;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/**
 * Gets one of two entity properties based on the condition
 */
public record ConditionalToolVariable(IJsonPredicate<IToolContext> condition, ToolVariable ifTrue, ToolVariable ifFalse)
    implements ToolVariable, ConditionalVariable<IJsonPredicate<IToolContext>,ToolVariable> {
  public static final IGenericLoader<ConditionalToolVariable> LOADER = new ConditionalVariableLoader<>(ToolContextPredicate.LOADER, ToolVariable.LOADER, ConditionalToolVariable::new);

  @Override
  public float getValue(IToolStackView tool) {
    return condition.matches(tool) ? ifTrue.getValue(tool) : ifFalse.getValue(tool);
  }

  @Override
  public IGenericLoader<? extends ToolVariable> getLoader() {
    return LOADER;
  }
}
