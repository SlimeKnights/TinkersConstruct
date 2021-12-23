package slimeknights.tconstruct.library.recipe.modifiers.spilling.effects;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.PacketBuffer;

/**
 * Logic to parse spilling effects
 */
public interface ISpillingEffectLoader<T extends ISpillingEffect> {
  /** Deserializes the recipe from json */
  T deserialize(JsonObject json);

  /** Reads the recipe from the packet buffer */
  T read(PacketBuffer buffer);

  /** Writes this effect to json */
  default void serialize(T effect, JsonObject json) {}

  /** Writes this effect to the packet buffer */
  default void write(T effect, PacketBuffer buffer) {}

  /** Loader instance for a spilling effect with only one implementation */
  @RequiredArgsConstructor
  class Singleton<T extends ISpillingEffect> implements ISpillingEffectLoader<T> {
    private final T instance;

    @Override
    public T deserialize(JsonObject json) {
      return instance;
    }

    @Override
    public T read(PacketBuffer buffer) {
      return instance;
    }
  }
}
