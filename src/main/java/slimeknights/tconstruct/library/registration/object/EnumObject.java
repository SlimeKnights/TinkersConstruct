package slimeknights.tconstruct.library.registration.object;

import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
public class EnumObject<T extends Enum<T>, I extends IForgeRegistryEntry<? super I>> {
  private Map<T,Supplier<? extends I>> map;
  // use builder to create
  protected EnumObject(Map<T,Supplier<? extends I>> map) {
    this.map = map;
  }

  /**
   * Gets a block supplier for the given value
   * @param value  Value to get
   * @return  BlockItemObject
   */
  public Supplier<? extends I> getSupplier(T value) {
    return map.get(value);
  }

  /**
   * Gets the value for the given enum
   * @param value  Value to get
   * @return  Block
   */
  public I get(T value) {
    if (!map.containsKey(value)) {
      return null;
    }
    return getSupplier(value).get();
  }

  /**
   * Checks if this enum object contains the given value
   * @param value  Value to check for
   * @return  True if the value is contained, false otherwise
   */
  public boolean contains(IForgeRegistryEntry<? super I> value) {
    return this.map.values().stream().map(Supplier::get).anyMatch(value::equals);
  }

  /**
   * Gets a list of values in this enum object
   * @return  List of values in the object
   */
  public List<I> values() {
    return this.map.values().stream().map(Supplier::get).collect(Collectors.toList());
  }

  /**
   * Enum object builder, to more conveiently create it from items, a map, or another enum object
   * @param <T>  Enum type
   * @param <I>  Entry type
   */
  @SuppressWarnings("UnusedReturnValue")
  public static class Builder<T extends Enum<T>, I extends IForgeRegistryEntry<? super I>> {
    private final Map<T, Supplier<? extends I>> map;
    public Builder(Class<T> clazz) {
      this.map = new EnumMap<>(clazz);
    }

    /**
     * Adds the given key and value to the object
     * @param key    Key
     * @param value  Value
     * @return  Builder instance
     */
    public Builder<T,I> put(T key, Supplier<? extends I> value) {
      this.map.put(key, value);
      return this;
    }

    /**
     * Adds all values from the given map
     * @param map  Map
     * @return  Builder instance
     */
    public Builder<T,I> putAll(Map<T, Supplier<? extends I>> map) {
      this.map.putAll(map);
      return this;
    }

    /**
     * Adds all values from the given enum object
     * @param object  Enum object
     * @return  Builder instance
     */
    public Builder<T,I> putAll(EnumObject<T,I> object) {
      this.map.putAll(object.map);
      return this;
    }

    /**
     * Creates the final enum object
     * @return  Constructed enum object
     */
    public EnumObject<T,I> build() {
      return new EnumObject<>(map);
    }
  }
}
