package slimeknights.tconstruct.library.json.variable;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import slimeknights.mantle.data.GenericLoaderRegistry;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.GenericLoaderRegistry.IHaveLoader;
import slimeknights.mantle.data.loader.NestedLoader;
import slimeknights.tconstruct.library.json.variable.ConditionalVariableLoader.ConditionalVariable;

/**
 * Loader used for a conditioned value
 * @param conditionLoader  Loader for the condition object
 * @param variableLoader   Loader for the variable objects
 * @param constructor      Constructor for the object being loaded
 * @param <T>
 * @param <C>
 * @param <V>
 */
public record ConditionalVariableLoader<T extends IHaveLoader<?> & ConditionalVariable<C,V>, C extends IHaveLoader<C>, V extends IHaveLoader<V>>(
    GenericLoaderRegistry<C> conditionLoader, GenericLoaderRegistry<V> variableLoader, ConditionalVariableConstructor<T,C,V> constructor) implements IGenericLoader<T> {
  @Override
  public T deserialize(JsonObject json) {
    NestedLoader.mapType(json, "condition_type");
    return constructor.apply(conditionLoader.deserialize(json), variableLoader.getAndDeserialize(json, "if_true"), variableLoader.getAndDeserialize(json, "if_false"));
  }

  @Override
  public void serialize(T object, JsonObject json) {
    NestedLoader.serializeInto(json, "condition_type", conditionLoader, object.condition());
    json.add("if_true", variableLoader.serialize(object.ifTrue()));
    json.add("if_false", variableLoader.serialize(object.ifFalse()));
  }

  @Override
  public T fromNetwork(FriendlyByteBuf buffer) {
    return constructor.apply(conditionLoader.fromNetwork(buffer), variableLoader.fromNetwork(buffer), variableLoader.fromNetwork(buffer));
  }

  @Override
  public void toNetwork(T object, FriendlyByteBuf buffer) {
    conditionLoader.toNetwork(object.condition(), buffer);
    variableLoader.toNetwork(object.ifTrue(), buffer);
    variableLoader.toNetwork(object.ifFalse(), buffer);
  }

  /** Interface representing a conditional variable constructor */
  public interface ConditionalVariableConstructor<T extends IHaveLoader<?>,C,V> {
    /** Constructs this object */
    T apply(C condition, V ifTrue, V ifFalse);
  }

  /** Interface for conditional variables */
  public interface ConditionalVariable<C,V> {
    /** Condition */
    C condition();
    /** Value if true */
    V ifTrue();
    /** Value if false */
    V ifFalse();
  }
}
