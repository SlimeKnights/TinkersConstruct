package slimeknights.tconstruct.library.json.variable;

import com.mojang.datafixers.util.Function3;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IHaveLoader;

/**
 * Interface for conditional variables
 */
public interface ConditionalVariable<C, V> {
  /** Condition */
  C condition();

  /** Value if true */
  V ifTrue();

  /** Value if false */
  V ifFalse();


  /** Creates a loadable instance for a conditional variable */
  static <C extends IHaveLoader, V extends IHaveLoader, CV extends ConditionalVariable<C,V>> RecordLoadable<CV> loadable(GenericLoaderRegistry<C> conditionLoader, GenericLoaderRegistry<V> variableLoader, Function3<C,V,V,CV> constructor) {
    return RecordLoadable.create(conditionLoader.directField("condition_type", ConditionalVariable::condition), variableLoader.requiredField("if_true", ConditionalVariable::ifTrue), variableLoader.requiredField("if_false", ConditionalVariable::ifFalse), constructor);
  }
}
