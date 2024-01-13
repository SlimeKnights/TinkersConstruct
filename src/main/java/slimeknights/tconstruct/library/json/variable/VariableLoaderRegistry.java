package slimeknights.tconstruct.library.json.variable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.data.GenericLoaderRegistry;
import slimeknights.mantle.data.GenericLoaderRegistry.IHaveLoader;

/**
 * Generic loader registry for variables, has special handling for constant float values to a custom float type
 * @param <T>  Object in registry
 */
public class VariableLoaderRegistry<T extends IHaveLoader<T>> extends GenericLoaderRegistry<T> {
  /** Constructor for constant values */
  private final FloatFunction<? extends T> constantConstructor;

  /**
   * Creates a new instance with the given constructor
   * @param constantConstructor  Constructor for constant values, should extend {@link ConstantFloat}
   */
  public VariableLoaderRegistry(FloatFunction<? extends T> constantConstructor) {
    super(true);
    this.constantConstructor = constantConstructor;
  }

  @Override
  public T deserialize(JsonElement element) {
    if (element.isJsonPrimitive()) {
      JsonPrimitive primitive = element.getAsJsonPrimitive();
      if (primitive.isNumber()) {
        return constantConstructor.apply(primitive.getAsFloat());
      }
    }
    return super.deserialize(element);
  }

  @Override
  public JsonElement serialize(T src) {
    if (src instanceof ConstantFloat constant) {
      return new JsonPrimitive(constant.value());
    }
    return super.serialize(src);
  }

  @Override
  public T fromNetwork(FriendlyByteBuf buffer) {
    return super.fromNetwork(buffer);
  }

  /** Interface for a float constructor */
  public interface FloatFunction<T> {
    T apply(float value);
  }

  /** Class for constant float instances */
  public interface ConstantFloat {
    float value();
  }

  /** Loader for a constant float */
  public record ConstantLoader<T extends ConstantFloat & IHaveLoader<?>>(FloatFunction<T> constructor) implements IGenericLoader<T> {
    @Override
    public T deserialize(JsonObject json) {
      return constructor.apply(GsonHelper.getAsFloat(json, "value"));
    }

    @Override
    public void serialize(T object, JsonObject json) {
      json.addProperty("value", object.value());
    }

    @Override
    public T fromNetwork(FriendlyByteBuf buffer) {
      return constructor.apply(buffer.readFloat());
    }

    @Override
    public void toNetwork(T object, FriendlyByteBuf buffer) {
      buffer.writeFloat(object.value());
    }
  }
}
