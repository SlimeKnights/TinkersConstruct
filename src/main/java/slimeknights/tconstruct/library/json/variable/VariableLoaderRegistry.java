package slimeknights.tconstruct.library.json.variable;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IHaveLoader;
import slimeknights.mantle.util.typed.TypedMap;

import java.util.function.Function;

/**
 * Generic loader registry for variables, has special handling for constant float values to a custom float type
 * @param <T>  Object in registry
 */
public class VariableLoaderRegistry<T extends IHaveLoader> extends GenericLoaderRegistry<T> {
  /** Constructor for constant values */
  private final FloatFunction<? extends T> constantConstructor;

  /**
   * Creates a new instance with the given constructor
   * @param constantConstructor  Constructor for constant values, should extend {@link ConstantFloat}
   */
  public VariableLoaderRegistry(String name, FloatFunction<? extends T> constantConstructor) {
    super(name, true);
    this.constantConstructor = constantConstructor;
  }

  @Override
  public T convert(JsonElement element, String key, TypedMap context) throws JsonSyntaxException {
    if (element.isJsonPrimitive()) {
      JsonPrimitive primitive = element.getAsJsonPrimitive();
      if (primitive.isNumber()) {
        return constantConstructor.apply(primitive.getAsFloat());
      }
    }
    return super.convert(element, key, context);
  }

  @Override
  public JsonElement serialize(T src) {
    if (src instanceof ConstantFloat constant) {
      return new JsonPrimitive(constant.value());
    }
    return super.serialize(src);
  }

  /** Interface for a float constructor */
  public interface FloatFunction<T> {
    T apply(float value);
  }

  /** Class for constant float instances */
  public interface ConstantFloat {
    float value();
  }

  private static final LoadableField<Float,ConstantFloat> VALUE_FIELD = FloatLoadable.ANY.requiredField("value", ConstantFloat::value);

  /** Creates a new constant loader */
  public static <T extends ConstantFloat & IHaveLoader> RecordLoadable<T> constantLoader(Function<Float,T> constructor) {
    return RecordLoadable.create(VALUE_FIELD, constructor);
  }
}
