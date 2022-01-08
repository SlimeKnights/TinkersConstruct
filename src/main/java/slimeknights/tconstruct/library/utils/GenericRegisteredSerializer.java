package slimeknights.tconstruct.library.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import slimeknights.tconstruct.library.utils.GenericRegisteredSerializer.IJsonSerializable;

/** @deprecated use {@link slimeknights.mantle.util.GenericRegisteredSerializer} */
@Deprecated
public class GenericRegisteredSerializer<T extends IJsonSerializable> extends slimeknights.mantle.util.GenericRegisteredSerializer<T> {
  /** @deprecated use {@link slimeknights.mantle.util.GenericRegisteredSerializer.IJsonSerializable} */
  @Deprecated
  public interface IJsonSerializable extends slimeknights.mantle.util.GenericRegisteredSerializer.IJsonSerializable {
    @Override
    default JsonObject serialize(JsonSerializationContext context) {
      throw new IllegalArgumentException(this.getClass().getSimpleName() + " cannot be serialized");
    }
  }
}
